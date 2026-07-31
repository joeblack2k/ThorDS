package me.magnum.melonds.common

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
    }

    @Test
    fun onlySetsTheThorDefaultWithoutAnExistingUserChoice() {
        assertTrue(ThorDeviceDefaults.shouldApplySoftInputDefault("AYN", "AYN Thor", hasUserPreference = false))
        assertFalse(ThorDeviceDefaults.shouldApplySoftInputDefault("AYN", "AYN Thor", hasUserPreference = true))
        assertFalse(ThorDeviceDefaults.shouldApplySoftInputDefault("Google", "Pixel", hasUserPreference = false))
    }
}
