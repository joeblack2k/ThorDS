package me.magnum.melonds.ui.emulator.component

import me.magnum.melonds.ui.emulator.model.EmulatorOverlay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmulatorOverlayTrackerTest {
    @Test
    fun duplicateAddAndAbsentRemoveDoNotLeaveAHiddenBlocker() {
        var present = 0
        var cleared = 0
        val tracker = EmulatorOverlayTracker(
            onOverlaysCleared = { cleared++ },
            onOverlaysPresent = { present++ },
        )

        tracker.addActiveOverlay(EmulatorOverlay.PAUSE_MENU)
        tracker.addActiveOverlay(EmulatorOverlay.PAUSE_MENU)
        tracker.removeActiveOverlay(EmulatorOverlay.REWIND_WINDOW)

        assertEquals(1, present)
        assertTrue(tracker.hasActiveOverlays())
        assertEquals(0, cleared)

        tracker.removeActiveOverlay(EmulatorOverlay.PAUSE_MENU)
        tracker.removeActiveOverlay(EmulatorOverlay.PAUSE_MENU)

        assertFalse(tracker.hasActiveOverlays())
        assertEquals(1, cleared)
    }
}
