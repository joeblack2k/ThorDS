package me.magnum.melonds.ui.emulator.component

import me.magnum.melonds.ui.emulator.model.EmulatorOverlay

class EmulatorOverlayTracker(
    private val onOverlaysCleared: () -> Unit,
    private val onOverlaysPresent: () -> Unit,
) {

    private val activeOverlays = mutableSetOf<EmulatorOverlay>()

    fun addActiveOverlay(overlay: EmulatorOverlay) {
        if (activeOverlays.add(overlay) && activeOverlays.size == 1) {
            onOverlaysPresent()
        }
    }

    fun removeActiveOverlay(overlay: EmulatorOverlay) {
        if (activeOverlays.remove(overlay) && activeOverlays.isEmpty()) {
            onOverlaysCleared()
        }
    }

    fun hasActiveOverlays(): Boolean {
        return activeOverlays.isNotEmpty()
    }
}
