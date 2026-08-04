package me.magnum.melonds.ui.emulator.input

import android.os.SystemClock
import android.util.Log
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import me.magnum.melonds.MelonEmulator
import me.magnum.melonds.domain.model.ControllerConfiguration
import me.magnum.melonds.domain.model.Input
import me.magnum.melonds.domain.model.InputConfig
import me.magnum.melonds.domain.model.enhancement.SmoothCameraInput
import java.util.Locale
import kotlin.math.absoluteValue

class InputProcessor(
    private val controllerConfiguration: ControllerConfiguration,
    private val systemInputListener: IInputListener,
    private val frontendInputListener: IInputListener,
    private val slot2AnalogInput: (Float, Float) -> Unit = MelonEmulator::setSlot2AnalogInput,
    private val slot2CameraState: (Short, Short, Short, Short, Short) -> Unit =
        MelonEmulator::setSlot2CameraState,
) : INativeInputListener {
    companion object {
        private const val TAG = "InputProcessor"
        private const val SLOT2_ANALOG_LOG_INTERVAL_MS = 1500L
        private const val PROFILE_CAMERA_LOG_INTERVAL_MS = 250L
        private const val SLOT2_RAW_ANALOG_PRIORITY_MS = 150L
        private const val SMOOTH_CAMERA_YAW_UNITS_PER_TICK = 850
        private const val SMOOTH_CAMERA_FLAG_ENABLED = 1
        private const val MOTION_EVENT_LOG_INTERVAL_MS = 250L
        private const val SMOOTH_CAMERA_R3 = KeyEvent.KEYCODE_BUTTON_THUMBR
        private val slot2XAxisFallbackCodes = intArrayOf(
            MotionEvent.AXIS_X,
            MotionEvent.AXIS_HAT_X,
            MotionEvent.AXIS_Z,
            MotionEvent.AXIS_RX,
            MotionEvent.AXIS_LTRIGGER,
            MotionEvent.AXIS_RTRIGGER,
        )
        private val slot2YAxisFallbackCodes = intArrayOf(
            MotionEvent.AXIS_Y,
            MotionEvent.AXIS_HAT_Y,
            MotionEvent.AXIS_RY,
            MotionEvent.AXIS_RZ,
            MotionEvent.AXIS_BRAKE,
            MotionEvent.AXIS_GAS,
        )
    }

    private val axisStates: Map<Axis, AxisState>
    private var lastSlot2AnalogLogAtMs = 0L
    private var lastSlot2RawAnalogEventAtMs = 0L
    private var slot2DigitalLeftPressed = false
    private var slot2DigitalRightPressed = false
    private var slot2DigitalUpPressed = false
    private var slot2DigitalDownPressed = false
    private val smoothCameraInput = SmoothCameraInput()
    private var smoothCameraRecenterSequence: Short = 0
    private var lastProfileCameraLogAtMs = 0L
    private var lastMotionEventLogAtMs = 0L

    init {
        val axis = controllerConfiguration.inputMapper.flatMap { inputConfig ->
            listOf(inputConfig.assignment, inputConfig.altAssignment)
        }.mapNotNull { assignment ->
            (assignment as? InputConfig.Assignment.Axis)?.let {
                Axis(it.deviceId, it.axisCode, it.direction)
            }
        }

        axisStates = axis.associateWith { AxisState(0f, false) }
    }

    override fun onKeyEvent(keyEvent: KeyEvent): Boolean {
        if (controllerConfiguration.profileCameraEnabled && keyEvent.keyCode == SMOOTH_CAMERA_R3) {
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyEvent.repeatCount == 0) {
                smoothCameraRecenterSequence = (smoothCameraRecenterSequence + 1).toShort()
                sendSmoothCameraState()
            }
            return true
        }
        val fromController = keyEvent.isFromSource(InputDevice.SOURCE_CLASS_JOYSTICK)
            || keyEvent.isFromSource(InputDevice.SOURCE_JOYSTICK)
            || keyEvent.isFromSource(InputDevice.SOURCE_GAMEPAD)
            || keyEvent.isFromSource(InputDevice.SOURCE_DPAD)
            || keyEvent.device?.supportsSource(InputDevice.SOURCE_JOYSTICK) == true
            || keyEvent.device?.supportsSource(InputDevice.SOURCE_GAMEPAD) == true
        val input = controllerConfiguration.keyToInput(keyEvent.keyCode) ?: return false

        when (keyEvent.action) {
            KeyEvent.ACTION_DOWN -> {
                dispatchInputPressed(input, fromController)
                return true
            }
            KeyEvent.ACTION_UP -> {
                dispatchInputReleased(input, fromController)
                return true
            }
        }
        return false
    }

    override fun onMotionEvent(motionEvent: MotionEvent): Boolean {
        val now = SystemClock.uptimeMillis()
        if (now - lastMotionEventLogAtMs >= MOTION_EVENT_LOG_INTERVAL_MS) {
            lastMotionEventLogAtMs = now
            Log.w(
                TAG,
                "controllerMotion deviceId=${motionEvent.deviceId} source=0x${motionEvent.source.toString(16)} action=${motionEvent.actionMasked} z=${"%.3f".format(Locale.US, motionEvent.getAxisValue(MotionEvent.AXIS_Z))} rz=${"%.3f".format(Locale.US, motionEvent.getAxisValue(MotionEvent.AXIS_RZ))} rx=${"%.3f".format(Locale.US, motionEvent.getAxisValue(MotionEvent.AXIS_RX))} ry=${"%.3f".format(Locale.US, motionEvent.getAxisValue(MotionEvent.AXIS_RY))}",
            )
        }
        if (isControllerMotionEvent(motionEvent)) {
            val slot2Handled = processSlot2AnalogFromMotionEvent(motionEvent)

            val deviceAxis = axisStates.filterKeys { it.deviceId == null || it.deviceId == motionEvent.deviceId }
            deviceAxis.forEach {
                val axis = it.key
                val axisState = it.value
                if (isSmoothCameraAxis(axis.axisCode)
                    || (slot2Handled && isProfileSlot2Axis(axis.axisCode))
                ) {
                    if (axisState.active) {
                        controllerConfiguration.axisToInput(axis.axisCode, axis.direction)?.let { input ->
                            dispatchInputReleased(input, fromController = true)
                        }
                    }
                    axisState.value = 0f
                    axisState.active = false
                    return@forEach
                }

                val newValue = motionEvent.getAxisValue(axis.axisCode)
                val clampedValue = when (axis.direction) {
                    InputConfig.Assignment.Axis.Direction.POSITIVE -> newValue.coerceAtLeast(0f)
                    InputConfig.Assignment.Axis.Direction.NEGATIVE -> newValue.coerceAtMost(0f)
                }

                if (axisState.shouldToggleFor(newValue = clampedValue)) {
                    controllerConfiguration.axisToInput(axis.axisCode, axis.direction)?.let { input ->
                        if (axisState.active) {
                            axisState.active = false
                            dispatchInputReleased(input, fromController = true)
                        } else {
                            axisState.active = true
                            dispatchInputPressed(input, fromController = true)
                        }
                    }
                }
                axisState.value = clampedValue
            }
            if (controllerConfiguration.profileCameraEnabled) {
                updateProfileCamera(motionEvent)
            }
            return slot2Handled || deviceAxis.isNotEmpty()
        } else {
            return false
        }
    }

    private fun isProfileSlot2Axis(axisCode: Int): Boolean {
        if (!controllerConfiguration.profileCameraEnabled) {
            return false
        }
        val mapping = controllerConfiguration.slot2AnalogMapping
        return axisCode == mapping.axisXCode || axisCode == mapping.axisYCode
    }

    private fun updateProfileCamera(motionEvent: MotionEvent) {
        val preferredX = motionEvent.getAxisValue(MotionEvent.AXIS_Z)
        val preferredY = motionEvent.getAxisValue(MotionEvent.AXIS_RZ)
        val alternateX = motionEvent.getAxisValue(MotionEvent.AXIS_RX)
        val alternateY = motionEvent.getAxisValue(MotionEvent.AXIS_RY)
        // Android exposes the Thor's right stick as Z/RZ on some layouts and RX/RY on others.
        val rawX = if (preferredX.absoluteValue > 0.001f || preferredY.absoluteValue > 0.001f) {
            preferredX
        } else {
            alternateX
        }
        val rawY = if (preferredX.absoluteValue > 0.001f || preferredY.absoluteValue > 0.001f) {
            preferredY
        } else {
            alternateY
        }
        val yaw = smoothCameraInput.yaw(
            rawX = rawX.coerceIn(-1f, 1f),
            rawY = rawY.coerceIn(-1f, 1f),
        )
        val pitch = smoothCameraInput.pitch(rawY.coerceIn(-1f, 1f))
        val now = SystemClock.uptimeMillis()
        if (maxOf(preferredX.absoluteValue, preferredY.absoluteValue, alternateX.absoluteValue, alternateY.absoluteValue) > 0.05f
            && now - lastProfileCameraLogAtMs >= PROFILE_CAMERA_LOG_INTERVAL_MS
        ) {
            lastProfileCameraLogAtMs = now
            Log.w(
                TAG,
                "profileCameraInput deviceId=${motionEvent.deviceId} source=0x${motionEvent.source.toString(16)} z=${"%.3f".format(Locale.US, preferredX)} rz=${"%.3f".format(Locale.US, preferredY)} rx=${"%.3f".format(Locale.US, alternateX)} ry=${"%.3f".format(Locale.US, alternateY)} yaw=${"%.3f".format(Locale.US, yaw)}",
            )
        }
        sendSmoothCameraState(yaw, pitch)
    }

    private fun sendSmoothCameraState(yawInput: Float = 0f, pitchInput: Float = 0f) {
        val q12 = (yawInput.coerceIn(-1f, 1f) * 4096f).toInt().coerceIn(-4096, 4096).toShort()
        val pitchQ12 = (pitchInput.coerceIn(-1f, 1f) * 4096f).toInt().coerceIn(-4096, 4096).toShort()
        slot2CameraState(
            q12,
            pitchQ12,
            SMOOTH_CAMERA_YAW_UNITS_PER_TICK.toShort(),
            smoothCameraRecenterSequence,
            SMOOTH_CAMERA_FLAG_ENABLED.toShort(),
        )
    }

    private fun isSmoothCameraAxis(axisCode: Int): Boolean {
        return controllerConfiguration.profileCameraEnabled
            && (axisCode == MotionEvent.AXIS_Z || axisCode == MotionEvent.AXIS_RZ)
    }

    override fun onMotionEventSlot2(motionEvent: MotionEvent): Boolean {
        if (!isControllerMotionEvent(motionEvent)) {
            return false
        }
        return processSlot2AnalogFromMotionEvent(motionEvent)
    }

    private fun dispatchInputPressed(input: Input, fromController: Boolean) {
        updateSlot2DigitalFallback(input, pressed = true, fromController = fromController)
        if (input.isSystemInput) {
            systemInputListener.onKeyPress(input)
        } else {
            frontendInputListener.onKeyPress(input)
        }
    }

    private fun dispatchInputReleased(input: Input, fromController: Boolean) {
        updateSlot2DigitalFallback(input, pressed = false, fromController = fromController)
        if (input.isSystemInput) {
            systemInputListener.onKeyReleased(input)
        } else {
            frontendInputListener.onKeyReleased(input)
        }
    }

    private fun updateSlot2DigitalFallback(input: Input, pressed: Boolean, fromController: Boolean) {
        if (!fromController || controllerConfiguration.profileCameraEnabled) {
            return
        }

        when (input) {
            Input.LEFT -> slot2DigitalLeftPressed = pressed
            Input.RIGHT -> slot2DigitalRightPressed = pressed
            Input.UP -> slot2DigitalUpPressed = pressed
            Input.DOWN -> slot2DigitalDownPressed = pressed
            else -> return
        }

        val now = SystemClock.uptimeMillis()
        if (now - lastSlot2RawAnalogEventAtMs <= SLOT2_RAW_ANALOG_PRIORITY_MS) {
            return
        }

        val digitalX = when {
            slot2DigitalLeftPressed == slot2DigitalRightPressed -> 0f
            slot2DigitalLeftPressed -> -1f
            else -> 1f
        }
        val digitalY = when {
            slot2DigitalUpPressed == slot2DigitalDownPressed -> 0f
            slot2DigitalUpPressed -> -1f
            else -> 1f
        }

        slot2AnalogInput(digitalX, digitalY)
        if ((digitalX.absoluteValue > 0f || digitalY.absoluteValue > 0f)
            && now - lastSlot2AnalogLogAtMs >= SLOT2_ANALOG_LOG_INTERVAL_MS
        ) {
            lastSlot2AnalogLogAtMs = now
            Log.w(
                TAG,
                "slot2AnalogInput source=digital-fallback x=${"%.3f".format(Locale.US, digitalX)} y=${"%.3f".format(Locale.US, digitalY)}",
            )
        }
    }

    private fun isControllerMotionEvent(motionEvent: MotionEvent): Boolean {
        return motionEvent.isFromSource(InputDevice.SOURCE_CLASS_JOYSTICK)
            || motionEvent.isFromSource(InputDevice.SOURCE_JOYSTICK)
            || motionEvent.isFromSource(InputDevice.SOURCE_GAMEPAD)
    }

    private fun processSlot2AnalogFromMotionEvent(motionEvent: MotionEvent): Boolean {
        val slot2Mapping = controllerConfiguration.slot2AnalogMapping
        if (controllerConfiguration.profileCameraEnabled
            && (isSmoothCameraAxis(slot2Mapping.axisXCode) || isSmoothCameraAxis(slot2Mapping.axisYCode))
        ) {
            return false
        }
        val mappedDeviceId = slot2Mapping.effectiveDeviceId()
        val mappedDeviceConnected = mappedDeviceId == null || InputDevice.getDevice(mappedDeviceId) != null
        if (mappedDeviceId != null && mappedDeviceId != motionEvent.deviceId && mappedDeviceConnected) {
            return false
        }

        val rawAnalogX = resolveSlot2AxisValue(
            motionEvent = motionEvent,
            preferredAxisCode = slot2Mapping.axisXCode,
            fallbackAxisCodes = slot2XAxisFallbackCodes,
        ).coerceIn(-1f, 1f)
        val rawAnalogY = resolveSlot2AxisValue(
            motionEvent = motionEvent,
            preferredAxisCode = slot2Mapping.axisYCode,
            fallbackAxisCodes = slot2YAxisFallbackCodes,
        ).coerceIn(-1f, 1f)
        val (analogX, analogY) = slot2Mapping.processRadial(rawAnalogX, rawAnalogY)
        slot2AnalogInput(analogX, analogY)
        val now = SystemClock.uptimeMillis()
        lastSlot2RawAnalogEventAtMs = now
        if ((analogX.absoluteValue > 0f || analogY.absoluteValue > 0f)
            && now - lastSlot2AnalogLogAtMs >= SLOT2_ANALOG_LOG_INTERVAL_MS
        ) {
            lastSlot2AnalogLogAtMs = now
            Log.w(
                TAG,
                "slot2AnalogInput deviceId=${motionEvent.deviceId} source=0x${motionEvent.source.toString(16)} axisX=${slot2Mapping.axisXCode} axisY=${slot2Mapping.axisYCode} x=${"%.3f".format(Locale.US, analogX)} y=${"%.3f".format(Locale.US, analogY)} deadzone=${"%.3f".format(Locale.US, slot2Mapping.normalizedDeadzone())}",
            )
        }
        return true
    }

    override fun releaseAllInputs() {
        axisStates.values.forEach { axisState ->
            axisState.value = 0f
            axisState.active = false
        }
        slot2DigitalLeftPressed = false
        slot2DigitalRightPressed = false
        slot2DigitalUpPressed = false
        slot2DigitalDownPressed = false
        lastSlot2RawAnalogEventAtMs = 0L
        slot2AnalogInput(0f, 0f)
        smoothCameraRecenterSequence = 0
        slot2CameraState(0, 0, 0, 0, 0)
    }

    private fun resolveSlot2AxisValue(
        motionEvent: MotionEvent,
        preferredAxisCode: Int,
        fallbackAxisCodes: IntArray,
    ): Float {
        val preferredValue = motionEvent.getAxisValue(preferredAxisCode)
        if (deviceSupportsAxis(motionEvent, preferredAxisCode)) {
            return preferredValue
        }

        if (preferredValue.absoluteValue > 0.0001f) {
            return preferredValue
        }

        var bestAxisValue = preferredValue
        var bestAxisAbs = preferredValue.absoluteValue
        fallbackAxisCodes.forEach { axisCode ->
            if (axisCode == preferredAxisCode) {
                return@forEach
            }
            if (isSmoothCameraAxis(axisCode)) {
                return@forEach
            }
            if (controllerConfiguration.profileCameraEnabled
                && (axisCode == MotionEvent.AXIS_HAT_X || axisCode == MotionEvent.AXIS_HAT_Y)
            ) {
                return@forEach
            }
            val axisValue = motionEvent.getAxisValue(axisCode)
            val axisAbs = axisValue.absoluteValue
            if (axisAbs > bestAxisAbs) {
                bestAxisAbs = axisAbs
                bestAxisValue = axisValue
            }
        }
        return bestAxisValue
    }

    private fun deviceSupportsAxis(motionEvent: MotionEvent, axisCode: Int): Boolean {
        val device = motionEvent.device ?: return false
        return device.getMotionRange(axisCode, motionEvent.source) != null
            || device.getMotionRange(axisCode, InputDevice.SOURCE_CLASS_JOYSTICK) != null
            || device.getMotionRange(axisCode, InputDevice.SOURCE_JOYSTICK) != null
            || device.getMotionRange(axisCode, InputDevice.SOURCE_GAMEPAD) != null
            || device.getMotionRange(axisCode) != null
    }

    private data class Axis(
        val deviceId: Int?,
        val axisCode: Int,
        val direction: InputConfig.Assignment.Axis.Direction,
    )

    private data class AxisState(
        var value: Float,
        var active: Boolean,
    ) {
        fun shouldToggleFor(newValue: Float): Boolean {
            return if (active) {
                newValue.absoluteValue < 0.5f
            } else {
                newValue.absoluteValue >= 0.5f
            }
        }
    }
}
