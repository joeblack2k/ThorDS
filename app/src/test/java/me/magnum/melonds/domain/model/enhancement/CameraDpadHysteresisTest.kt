package me.magnum.melonds.domain.model.enhancement

import me.magnum.melonds.domain.model.Input
import org.junit.Assert.assertEquals
import org.junit.Test

class CameraDpadHysteresisTest {
    @Test
    fun cameraDirectionUsesPressAndReleaseHysteresis() {
        val camera = CameraDpadHysteresis()
        assertEquals(emptySet<Input>(), camera.update(0.54f, 0f))
        assertEquals(setOf(Input.RIGHT), camera.update(0.56f, 0f))
        assertEquals(setOf(Input.RIGHT), camera.update(0.40f, 0f))
        assertEquals(emptySet<Input>(), camera.update(0.35f, 0f))
    }

    @Test
    fun cameraAllowsStableDiagonals() {
        val camera = CameraDpadHysteresis()
        assertEquals(setOf(Input.LEFT, Input.DOWN), camera.update(-0.8f, 0.8f))
    }

    @Test
    fun cameraAndPhysicalDpadReleaseOnlyAfterBothSourcesRelease() {
        val state = CameraDpadInputState()
        assertEquals(true, state.controllerPressed(Input.LEFT))
        assertEquals(emptySet<Input>(), state.updateCamera(setOf(Input.LEFT)).pressed)
        assertEquals(false, state.controllerReleased(Input.LEFT))
        assertEquals(setOf(Input.LEFT), state.updateCamera(emptySet()).released)
    }
}
