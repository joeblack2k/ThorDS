package me.magnum.melonds.domain.model.enhancement

enum class Arm9OverclockCapability {
    UNSUPPORTED,
    PLUMBING_ONLY,
    EXPERIMENTAL,
    VALIDATED,
}

data class Arm9OverclockResolution(
    val requestedPercent: Int,
    val effectivePercent: Int,
    val capability: Arm9OverclockCapability,
)

object Arm9OverclockPolicy {
    const val DEFAULT_PERCENT = 100
    val allowedRequests: Set<Int> = setOf(100, 125, 150, 175, 200)

    fun resolve(
        requestedPercent: Int,
        capability: Arm9OverclockCapability,
        safeMode: Boolean = false,
        requestedRaMode: ProfileRaMode = ProfileRaMode.CASUAL,
    ): Arm9OverclockResolution {
        val effectivePercent = if (
            requestedPercent in allowedRequests &&
            !safeMode &&
            capabilityAllowsOverclock(capability) &&
            requestedRaMode != ProfileRaMode.HARDCORE
        ) {
            requestedPercent
        } else {
            DEFAULT_PERCENT
        }
        return Arm9OverclockResolution(
            requestedPercent = requestedPercent,
            effectivePercent = effectivePercent,
            capability = capability,
        )
    }

    private fun capabilityAllowsOverclock(capability: Arm9OverclockCapability): Boolean {
        return capability == Arm9OverclockCapability.EXPERIMENTAL ||
            capability == Arm9OverclockCapability.VALIDATED
    }
}
