# Kotlin — requested/effective state

```kotlin
enum class Fps60ValidationState {
    UNAVAILABLE,
    EXPERIMENTAL,
    VALIDATED,
}

data class Fps60Capability(
    val validationState: Fps60ValidationState,
    val minimumArm9Percent: Int?,
    val reason: String? = null,
)

data class Fps60SessionState(
    val requested: Boolean,
    val effective: Boolean,
    val validationState: Fps60ValidationState,
    val requestedArm9Percent: Int,
    val effectiveArm9Percent: Int,
    val patchId: String?,
    val patchSha256: String?,
    val reason: String?,
)
```

## Resolver

```kotlin
fun resolveFps60(
    requested: Boolean,
    exactSm64dsEu: Boolean,
    safeMode: Boolean,
    raMode: ProfileRaMode,
    capability: Fps60Capability,
    effectiveArm9Percent: Int,
): Fps60SessionState {
    val reason = when {
        !requested -> "Not requested"
        !exactSm64dsEu -> "Exact European SM64DS ROM required"
        safeMode -> "Safe Mode uses Original timing"
        raMode == ProfileRaMode.HARDCORE -> "Hardcore requires Original"
        capability.validationState == Fps60ValidationState.UNAVAILABLE ->
            capability.reason ?: "60 FPS capability unavailable"
        capability.minimumArm9Percent != null
            && effectiveArm9Percent < capability.minimumArm9Percent ->
            "Requires ARM9 ${capability.minimumArm9Percent}%"
        else -> null
    }

    return Fps60SessionState(
        requested = requested,
        effective = reason == null,
        validationState = capability.validationState,
        requestedArm9Percent = capability.minimumArm9Percent ?: 100,
        effectiveArm9Percent = effectiveArm9Percent,
        patchId = null,
        patchSha256 = null,
        reason = reason,
    )
}
```

Bind patch ID/SHA after profile/runtime-code resolution.
