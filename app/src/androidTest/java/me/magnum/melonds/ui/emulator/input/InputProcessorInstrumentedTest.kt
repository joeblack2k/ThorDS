package me.magnum.melonds.ui.emulator.input

import android.os.SystemClock
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.test.ext.junit.runners.AndroidJUnit4
import me.magnum.melonds.domain.model.ControllerConfiguration
import me.magnum.melonds.domain.model.Input
import me.magnum.melonds.domain.model.InputConfig
import me.magnum.melonds.domain.model.Point
import me.magnum.melonds.domain.model.Slot2AnalogMapping
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@RunWith(AndroidJUnit4::class)
class InputProcessorInstrumentedTest {
    @Test
    fun controllerMotionTraversesRadialMappingWithoutEightWaySnapping() {
        val outputs = mutableListOf<Pair<Float, Float>>()
        val processor = createProcessor(outputs = outputs)
        val magnitudes = floatArrayOf(0.25f, 0.50f, 0.75f, 1.00f)

        for (direction in 0 until 16) {
            val angle = direction * 2.0 * PI / 16
            for (magnitude in magnitudes) {
                val rawX = (cos(angle) * magnitude).toFloat()
                val rawY = (sin(angle) * magnitude).toFloat()
                controllerMotion(rawX, rawY).useEvent {
                    assertTrue(processor.onMotionEvent(it))
                }

                val (outputX, outputY) = outputs.last()
                val outputMagnitude = sqrt(outputX * outputX + outputY * outputY)
                val expectedMagnitude = ((magnitude - 0.1f) / 0.9f).coerceIn(0f, 1f)
                assertEquals(expectedMagnitude, outputMagnitude, 0.0001f)
                assertEquals(angle.toCanonicalRadians(), atan2(outputY, outputX).toDouble().toCanonicalRadians(), 0.0001)
            }
        }

        controllerMotion(0.05f, 0.05f).useEvent(processor::onMotionEvent)
        assertEquals(0f to 0f, outputs.last())
        controllerMotion(0f, 0f).useEvent(processor::onMotionEvent)
        assertEquals(0f to 0f, outputs.last())
    }

    @Test
    fun leftStickMovesWithoutCameraWhileRightStickProducesSmoothCameraState() {
        val outputs = mutableListOf<Pair<Float, Float>>()
        val cameraStates = mutableListOf<List<Short>>()
        val systemInputs = RecordingInputListener()
        val processor = createProcessor(outputs, systemInputs, cameraStates)

        controllerMotion(0.75f, 0f).useEvent(processor::onMotionEvent)
        assertEquals((0.75f - 0.1f) / 0.9f, outputs.last().first, 0.0001f)
        assertTrue(systemInputs.pressed.isEmpty())

        controllerMotion(0f, 0f, cameraX = -0.8f).useEvent(processor::onMotionEvent)
        assertTrue(cameraStates.last()[0] < 0)
        assertTrue(systemInputs.pressed.isEmpty())
        controllerMotion(0f, 0f).useEvent(processor::onMotionEvent)
        assertEquals(0, cameraStates.last()[0])

        controllerMotion(0f, 0f, hatX = -1f).useEvent(processor::onMotionEvent)
        assertEquals(listOf(Input.LEFT), systemInputs.pressed)
        assertEquals(0f to 0f, outputs.last())
        controllerMotion(0f, 0f).useEvent(processor::onMotionEvent)
        assertEquals(listOf(Input.LEFT), systemInputs.released)

        assertTrue(processor.onKeyEvent(controllerKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT)))
        assertEquals(listOf(Input.LEFT, Input.LEFT), systemInputs.pressed)
        assertEquals(0f to 0f, outputs.last())
        assertTrue(processor.onKeyEvent(controllerKey(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT)))
        assertEquals(listOf(Input.LEFT, Input.LEFT), systemInputs.released)

        controllerMotion(0f, 0f, cameraX = 0.8f).useEvent(processor::onMotionEvent)
        assertTrue(cameraStates.last()[0] > 0)
        controllerMotion(0f, 0f, alternateCameraX = -0.8f).useEvent(processor::onMotionEvent)
        assertTrue(cameraStates.last()[0] < 0)
        processor.releaseAllInputs()
        assertEquals(0f to 0f, outputs.last())
        assertEquals(0, cameraStates.last()[0])

        controllerMotion(0.75f, 0f).useEvent(processor::onMotionEvent)
        assertEquals(listOf(Input.LEFT, Input.LEFT), systemInputs.pressed)

        val recreatedOutputs = mutableListOf<Pair<Float, Float>>()
        val recreated = createProcessor(recreatedOutputs, cameraStates = mutableListOf())
        controllerMotion(0f, 0f).useEvent(recreated::onMotionEvent)
        assertEquals(0f to 0f, recreatedOutputs.single())

        processor.onKeyEvent(controllerKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_THUMBR))
        assertEquals(1, cameraStates.last()[3].toInt())
    }

    private fun createProcessor(
        outputs: MutableList<Pair<Float, Float>>,
        systemInputs: RecordingInputListener = RecordingInputListener(),
        cameraStates: MutableList<List<Short>> = mutableListOf(),
    ): InputProcessor {
        val configuration = ControllerConfiguration(
            configList = listOf(
                InputConfig(
                    input = Input.LEFT,
                    assignment = InputConfig.Assignment.Key(null, KeyEvent.KEYCODE_DPAD_LEFT),
                    altAssignment = InputConfig.Assignment.Axis(
                        null,
                        MotionEvent.AXIS_HAT_X,
                        InputConfig.Assignment.Axis.Direction.NEGATIVE,
                    ),
                ),
                InputConfig(
                    input = Input.RIGHT,
                    assignment = InputConfig.Assignment.Axis(
                        null,
                        MotionEvent.AXIS_X,
                        InputConfig.Assignment.Axis.Direction.POSITIVE,
                    ),
                ),
            ),
            slot2AnalogMapping = Slot2AnalogMapping(deadzone = 0.1f),
            profileCameraEnabled = true,
        )
        return InputProcessor(
            controllerConfiguration = configuration,
            systemInputListener = systemInputs,
            frontendInputListener = RecordingInputListener(),
            slot2AnalogInput = { x, y -> outputs.add(x to y) },
            slot2CameraState = { yaw, pitch, units, sequence, flags ->
                cameraStates.add(listOf(yaw, pitch, units, sequence, flags))
            },
        )
    }

    private fun controllerMotion(
        leftX: Float,
        leftY: Float,
        cameraX: Float = 0f,
        cameraY: Float = 0f,
        alternateCameraX: Float = 0f,
        alternateCameraY: Float = 0f,
        hatX: Float = 0f,
    ): MotionEvent {
        val now = SystemClock.uptimeMillis()
        val properties = MotionEvent.PointerProperties().apply {
            id = 0
            toolType = MotionEvent.TOOL_TYPE_UNKNOWN
        }
        val coordinates = MotionEvent.PointerCoords().apply {
            setAxisValue(MotionEvent.AXIS_X, leftX)
            setAxisValue(MotionEvent.AXIS_Y, leftY)
            setAxisValue(MotionEvent.AXIS_Z, cameraX)
            setAxisValue(MotionEvent.AXIS_RZ, cameraY)
            setAxisValue(MotionEvent.AXIS_RX, alternateCameraX)
            setAxisValue(MotionEvent.AXIS_RY, alternateCameraY)
            setAxisValue(MotionEvent.AXIS_HAT_X, hatX)
        }
        return MotionEvent.obtain(
            now,
            now,
            MotionEvent.ACTION_MOVE,
            1,
            arrayOf(properties),
            arrayOf(coordinates),
            0,
            0,
            1f,
            1f,
            0,
            0,
            InputDevice.SOURCE_JOYSTICK,
            0,
        )
    }

    private fun controllerKey(action: Int, keyCode: Int): KeyEvent {
        val now = SystemClock.uptimeMillis()
        return KeyEvent(now, now, action, keyCode, 0, 0, 0, 0, 0, InputDevice.SOURCE_GAMEPAD)
    }

    private inline fun <T> MotionEvent.useEvent(block: (MotionEvent) -> T): T {
        return try {
            block(this)
        } finally {
            recycle()
        }
    }

    private fun Double.toCanonicalRadians(): Double {
        return if (this < 0.0) this + 2.0 * PI else this
    }

    private class RecordingInputListener : IInputListener {
        val pressed = mutableListOf<Input>()
        val released = mutableListOf<Input>()

        override fun onKeyPress(key: Input) {
            pressed.add(key)
        }

        override fun onKeyReleased(key: Input) {
            released.add(key)
        }

        override fun onTouch(point: Point) = Unit
    }
}
