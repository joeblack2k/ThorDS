# Kotlin code skeletons

These are implementation-ready shapes, not a substitute for inspecting current constructors and package conventions.

## Smooth mapping

```kotlin
package me.magnum.melonds.domain.model.enhancement

import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

data class SmoothCameraMapping(
    val deadzone: Float = 0.12f,
    val responseExponent: Float = 1.50f,
    val invertX: Boolean = false,
    val invertY: Boolean = false,
) {
    fun process(rawX: Float, rawY: Float): Pair<Float, Float> {
        val x = (if (invertX) -rawX else rawX).coerceIn(-1f, 1f)
        val y = (if (invertY) -rawY else rawY).coerceIn(-1f, 1f)
        val magnitude = sqrt(x * x + y * y)
        val dz = deadzone.coerceIn(0f, 0.95f)
        if (magnitude <= dz || magnitude == 0f) return 0f to 0f

        val radial = ((magnitude - dz) / max(1f - dz, Float.MIN_VALUE))
            .coerceIn(0f, 1f)
        val curved = radial.pow(responseExponent.coerceIn(0.25f, 4f))
        val scale = curved / magnitude
        return (x * scale).coerceIn(-1f, 1f) to
            (y * scale).coerceIn(-1f, 1f)
    }
}
```

## Protocol state

```kotlin
data class Slot2CameraState(
    val yaw: Float = 0f,
    val pitch: Float = 0f,
    val yawUnitsPerTick: Int = 0,
    val enabled: Boolean = false,
    val recenterSound: Boolean = true,
    val recenterSequence: Int = 0,
) {
    val flags: Int
        get() = (if (enabled) 1 else 0) or
            (if (recenterSound) 1 shl 1 else 0)
}
```

## Profile configuration

```kotlin
data class SmoothCameraProfileConfiguration(
    val axisXCode: Int,
    val axisYCode: Int,
    val recenterKeyCode: Int,
    val mapping: SmoothCameraMapping = SmoothCameraMapping(),
    val yawUnitsPerTick: Int,
    val recenterSound: Boolean = true,
)
```

Keep Android constants at the Android adapter boundary if the domain layer currently avoids them.

## R3 edge

```kotlin
private var cameraRecenterSequence = 0

private fun handleProfileRecenter(event: KeyEvent): Boolean {
    val config = controllerConfiguration.smoothCamera ?: return false
    if (event.keyCode != config.recenterKeyCode) return false

    if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
        cameraRecenterSequence = (cameraRecenterSequence + 1) and 0xFFFF
        publishSmoothCameraState()
    }
    return true
}
```

## Axis reservation

```kotlin
private fun isProfileOwnedAxis(axisCode: Int): Boolean {
    val camera = controllerConfiguration.smoothCamera ?: return false
    return axisCode == camera.axisXCode || axisCode == camera.axisYCode
}
```

Skip generic axis mapping for owned axes.
