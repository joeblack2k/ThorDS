package me.magnum.melonds.impl

import me.magnum.melonds.domain.model.VideoFiltering
import me.magnum.melonds.domain.model.VideoRenderer
import me.magnum.melonds.domain.model.layout.LayoutConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class ThorDSSafeModePolicyTest {
    @Test
    fun safeModeUsesTransientNativeDefaults() {
        assertEquals(VideoRenderer.SOFTWARE, ThorDSSafeModePolicy.renderer(true, VideoRenderer.VULKAN))
        assertEquals(VideoFiltering.NONE, ThorDSSafeModePolicy.filtering(true, VideoFiltering.RETROARCH))
        assertEquals(LayoutConfiguration.DEFAULT_ID, ThorDSSafeModePolicy.layoutId(true, UUID.randomUUID()))
        assertFalse(ThorDSSafeModePolicy.cheatsEnabled(true, userEnabled = true))
    }

    @Test
    fun disabledSafeModePreservesUserPreferences() {
        val layoutId = UUID.randomUUID()
        assertEquals(VideoRenderer.VULKAN, ThorDSSafeModePolicy.renderer(false, VideoRenderer.VULKAN))
        assertEquals(VideoFiltering.RETROARCH, ThorDSSafeModePolicy.filtering(false, VideoFiltering.RETROARCH))
        assertEquals(layoutId, ThorDSSafeModePolicy.layoutId(false, layoutId))
        assertTrue(ThorDSSafeModePolicy.cheatsEnabled(false, userEnabled = true))
    }
}
