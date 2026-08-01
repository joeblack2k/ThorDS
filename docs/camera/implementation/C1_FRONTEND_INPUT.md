# C1 — frontend smooth input

## New pure mapping

Implement a platform-independent mapper with:

```text
deadzone
radial rescale
response exponent
invert X/Y
clamp
```

Suggested formula:

```kotlin
val magnitude = sqrt(x * x + y * y)
if (magnitude <= deadzone) return 0f to 0f
val radial = ((magnitude - deadzone) / (1f - deadzone)).coerceIn(0f, 1f)
val curved = radial.pow(responseExponent)
val scale = curved / magnitude
return x * scale to y * scale
```

## InputProcessor changes

- Replace `CameraDpadHysteresis` use for ordinary camera motion.
- Reserve right-stick axes before generic mapping.
- Send a `Slot2CameraState`.
- Consume R3 only when Smooth Orbit is effective.
- Use `repeatCount == 0`.
- Keep sequence monotonic during a session.
- Send neutral on release/disconnect.
- Restore ordinary mappings in Original.

## Backward compatibility

`CameraDpadInputState` may remain for D-pad ownership tests only if still used elsewhere. Remove dead code when proven unused; do not leave two active camera paths.
