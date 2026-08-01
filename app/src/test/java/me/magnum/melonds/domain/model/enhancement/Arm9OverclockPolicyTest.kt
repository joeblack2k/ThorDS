package me.magnum.melonds.domain.model.enhancement

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class Arm9OverclockPolicyTest {
    @Test
    fun allowedRequestsAreTheOnlyRequestsThatCanBeEffective() {
        assertEquals(setOf(100, 125, 150, 175, 200), Arm9OverclockPolicy.allowedRequests)
        Arm9OverclockPolicy.allowedRequests.forEach { requestedPercent ->
            val resolution = Arm9OverclockPolicy.resolve(
                requestedPercent = requestedPercent,
                capability = Arm9OverclockCapability.VALIDATED,
            )
            assertEquals(requestedPercent, resolution.effectivePercent)
        }
        assertEquals(
            100,
            Arm9OverclockPolicy.resolve(124, Arm9OverclockCapability.VALIDATED).effectivePercent,
        )
    }

    @Test
    fun unsupportedAndPlumbingOnlyCapabilitiesStayAtDefault() {
        Arm9OverclockCapability.entries
            .filter { it == Arm9OverclockCapability.UNSUPPORTED || it == Arm9OverclockCapability.PLUMBING_ONLY }
            .forEach { capability ->
                val resolution = Arm9OverclockPolicy.resolve(125, capability)
                assertEquals(125, resolution.requestedPercent)
                assertEquals(100, resolution.effectivePercent)
                assertEquals(capability, resolution.capability)
            }
    }

    @Test
    fun safeModeAndHardcoreStayAtDefault() {
        assertEquals(
            100,
            Arm9OverclockPolicy.resolve(
                requestedPercent = 125,
                capability = Arm9OverclockCapability.VALIDATED,
                safeMode = true,
            ).effectivePercent,
        )
        assertEquals(
            100,
            Arm9OverclockPolicy.resolve(
                requestedPercent = 125,
                capability = Arm9OverclockCapability.VALIDATED,
                requestedRaMode = ProfileRaMode.HARDCORE,
            ).effectivePercent,
        )
    }

    @Test
    fun experimentalAndValidatedCapabilitiesAllowNonHardcoreRequests() {
        Arm9OverclockCapability.entries
            .filter { it == Arm9OverclockCapability.EXPERIMENTAL || it == Arm9OverclockCapability.VALIDATED }
            .forEach { capability ->
                assertEquals(
                    150,
                    Arm9OverclockPolicy.resolve(150, capability).effectivePercent,
                )
            }
        assertFalse(
            Arm9OverclockPolicy.resolve(150, Arm9OverclockCapability.EXPERIMENTAL).effectivePercent == 100,
        )
    }

    @Test
    fun capabilityProbeDoesNotPublishArm9CoreSupportAsALiveEnhancementCapability() {
        val context = CapabilityProbe.from(
            hasSlot2Analog = false,
            hasVulkan = false,
            hasStructuredVulkanCompositor = false,
            isThorDualDisplay = false,
            hasArm9OverclockSupport = true,
            hasRetroAchievements = false,
        )

        assertFalse(EnhancementCapability.ARM9_OC_CORE_SUPPORT in context.capabilities)
        assertEquals(Arm9OverclockCapability.PLUMBING_ONLY, context.arm9OverclockCapability)
    }
}
