package me.magnum.melonds.domain.model

import kotlin.math.max
import kotlin.math.sqrt

data class Slot2AnalogMapping(
    val deviceId: Int? = null,
    val useDeviceFilter: Boolean = false,
    val axisXCode: Int = DEFAULT_AXIS_X_CODE,
    val axisYCode: Int = DEFAULT_AXIS_Y_CODE,
    val invertX: Boolean = false,
    val invertY: Boolean = false,
    val deadzone: Float = DEFAULT_DEADZONE,
) {
    fun normalizedDeadzone(): Float {
        return deadzone.coerceIn(0f, 1f)
    }

    fun effectiveDeviceId(): Int? {
        return if (useDeviceFilter) deviceId else null
    }

    fun processRadial(rawX: Float, rawY: Float): Pair<Float, Float> {
        val x = (if (invertX) -rawX else rawX).coerceIn(-1f, 1f)
        val y = (if (invertY) -rawY else rawY).coerceIn(-1f, 1f)
        val magnitude = sqrt(x * x + y * y)
        val deadzone = normalizedDeadzone()
        if (magnitude <= deadzone || magnitude == 0f) return 0f to 0f
        val normalizedMagnitude = ((magnitude - deadzone) / max(1f - deadzone, Float.MIN_VALUE)).coerceIn(0f, 1f)
        val scale = normalizedMagnitude / magnitude
        return (x * scale) to (y * scale)
    }

    companion object {
        const val DEFAULT_AXIS_X_CODE = 0
        const val DEFAULT_AXIS_Y_CODE = 1
        const val DEFAULT_DEADZONE = 0.1f
    }
}
