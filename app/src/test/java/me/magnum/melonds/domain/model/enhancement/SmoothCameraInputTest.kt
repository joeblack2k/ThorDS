package me.magnum.melonds.domain.model.enhancement

import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SmoothCameraInputTest {
    private val input = SmoothCameraInput()

    @Test
    fun deadzoneProducesNeutralYaw() {
        assertEquals(0f, input.yaw(0.1f, 0f))
        assertEquals(0f, input.yaw(0f, 0.11f))
    }

    @Test
    fun deflectionProducesSymmetricContinuousYaw() {
        val negative = input.yaw(-0.5f, 0f)
        val positive = input.yaw(0.5f, 0f)

        assertTrue(negative < 0f)
        assertTrue(positive > 0f)
        assertEquals(abs(negative), positive, 0.0001f)
        assertTrue(input.yaw(0.75f, 0f) > positive)
        assertTrue(positive < input.yaw(1f, 0f))
    }

    @Test
    fun verticalStickDoesNotCreateYaw() {
        assertEquals(0f, input.yaw(0f, 1f))
        assertEquals(0f, input.yaw(0f, -1f))
    }

    @Test
    fun invertXReversesOnlyYaw() {
        val normal = SmoothCameraInput(SmoothCameraInputConfig(invertX = false))
        val inverted = SmoothCameraInput(SmoothCameraInputConfig(invertX = true))

        assertEquals(-normal.yaw(0.8f, 0f), inverted.yaw(0.8f, 0f), 0.0001f)
    }
}
