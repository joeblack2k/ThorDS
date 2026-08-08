package me.magnum.melonds.common

import me.magnum.melonds.domain.model.VideoRenderer
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThorDeviceCapabilitiesTest {
    @Test
    fun identifiesThorWithoutMatchingOtherAynDevices() {
        assertTrue(ThorDeviceCapabilities.isThor("AYN", "AYN Thor"))
        assertTrue(ThorDeviceCapabilities.isThor("ayn", "ayn thor"))
        assertFalse(ThorDeviceCapabilities.isThor("AYN", "AYN Odin2"))
        assertFalse(ThorDeviceCapabilities.isThor("Google", "Pixel"))
        assertTrue(ThorDeviceCapabilities.usesFixedLandscape("AYN", "AYN Thor"))
        assertFalse(ThorDeviceCapabilities.usesFixedLandscape("AYN", "AYN Odin2"))
        assertTrue(ThorDeviceCapabilities.supportsTrueWidescreen("AYN", "AYN Thor", VideoRenderer.VULKAN))
        assertFalse(ThorDeviceCapabilities.supportsTrueWidescreen("AYN", "AYN Thor", VideoRenderer.OPENGL))
        assertFalse(ThorDeviceCapabilities.supportsTrueWidescreen("AYN", "AYN Thor", VideoRenderer.SOFTWARE))
        assertFalse(ThorDeviceCapabilities.supportsTrueWidescreen("Google", "Pixel", VideoRenderer.VULKAN))
    }

    @Test
    fun onlySetsTheThorDefaultWithoutAnExistingUserChoice() {
        assertTrue(ThorDeviceDefaults.shouldApplySoftInputDefault("AYN", "AYN Thor", hasUserPreference = false))
        assertFalse(ThorDeviceDefaults.shouldApplySoftInputDefault("AYN", "AYN Thor", hasUserPreference = true))
        assertFalse(ThorDeviceDefaults.shouldApplySoftInputDefault("Google", "Pixel", hasUserPreference = false))
        assertTrue(ThorDeviceDefaults.defaultTrueWidescreenEnabled("AYN", "AYN Thor"))
        assertFalse(ThorDeviceDefaults.defaultTrueWidescreenEnabled("AYN", "AYN Odin2"))
    }

    @Test
    fun repairsOnlyThorTrueWidescreenSoftwareRenderer() {
        assertTrue(
            ThorDeviceDefaults.shouldRepairWidescreenRenderer(
                "AYN",
                "AYN Thor",
                trueWidescreenEnabled = true,
                persistedRenderer = "software",
            ),
        )
        assertFalse(
            ThorDeviceDefaults.shouldRepairWidescreenRenderer(
                "Google",
                "Pixel",
                trueWidescreenEnabled = true,
                persistedRenderer = "software",
            ),
        )
        assertFalse(
            ThorDeviceDefaults.shouldRepairWidescreenRenderer(
                "AYN",
                "AYN Thor",
                trueWidescreenEnabled = false,
                persistedRenderer = "software",
            ),
        )
        assertFalse(
            ThorDeviceDefaults.shouldRepairWidescreenRenderer(
                "AYN",
                "AYN Thor",
                trueWidescreenEnabled = true,
                persistedRenderer = "vulkan",
            ),
        )
    }
}
