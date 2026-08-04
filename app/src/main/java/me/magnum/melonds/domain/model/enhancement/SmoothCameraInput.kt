package me.magnum.melonds.domain.model.enhancement

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sqrt

data class SmoothCameraInputConfig(
    val deadzone: Float = DEFAULT_DEADZONE,
    val responseExponent: Float = DEFAULT_RESPONSE_EXPONENT,
    val invertX: Boolean = true,
) {
    companion object {
        const val DEFAULT_DEADZONE = 0.12f
        const val DEFAULT_RESPONSE_EXPONENT = 1.50f
    }
}

class SmoothCameraInput(
    private val config: SmoothCameraInputConfig = SmoothCameraInputConfig(),
) {
    fun yaw(rawX: Float, rawY: Float): Float {
        val x = (if (config.invertX) -rawX else rawX).coerceIn(-1f, 1f)
        val y = rawY.coerceIn(-1f, 1f)
        val magnitude = sqrt(x * x + y * y)
        val deadzone = config.deadzone.coerceIn(0f, 0.99f)
        if (magnitude <= deadzone || magnitude == 0f) return 0f

        val rescaledMagnitude = ((magnitude - deadzone) / (1f - deadzone)).coerceIn(0f, 1f)
        val normalizedX = x / magnitude * rescaledMagnitude
        val exponent = config.responseExponent.coerceAtLeast(0.01f)
        return normalizedX.signPow(exponent).coerceIn(-1f, 1f)
    }

    fun pitch(rawY: Float): Float {
        val value = rawY.coerceIn(-1f, 1f)
        val deadzone = config.deadzone.coerceIn(0f, 0.99f)
        if (abs(value) <= deadzone) return 0f
        val rescaled = ((abs(value) - deadzone) / (1f - deadzone)).coerceIn(0f, 1f)
        val exponent = config.responseExponent.coerceAtLeast(0.01f)
        return rescaled.pow(exponent) * value.sign
    }

    private fun Float.signPow(exponent: Float): Float {
        return abs(this).pow(exponent) * if (this < 0f) -1f else 1f
    }
}
