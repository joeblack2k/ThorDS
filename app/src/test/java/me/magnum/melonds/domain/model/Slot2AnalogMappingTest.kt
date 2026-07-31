package me.magnum.melonds.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.sqrt

class Slot2AnalogMappingTest {
    private val mapping = Slot2AnalogMapping(deadzone = 0.1f)

    @Test
    fun radialDeadzoneRemovesCenterDrift() {
        assertEquals(0f to 0f, mapping.processRadial(0.07f, 0.07f))
    }

    @Test
    fun radialDeadzoneRescalesCardinalAndDiagonalInput() {
        assertEquals(1f to 0f, mapping.processRadial(1f, 0f))
        val (x, y) = mapping.processRadial(1f, 1f)
        assertEquals(1f, sqrt(x * x + y * y), 0.0001f)
        assertEquals(x, y, 0.0001f)
    }

    @Test
    fun inversionAppliesBeforeRadialNormalization() {
        val inverted = Slot2AnalogMapping(invertX = true, invertY = true)
        val (x, y) = inverted.processRadial(1f, -1f)
        assertEquals(-1f, x / kotlin.math.abs(x), 0.0001f)
        assertEquals(1f, y / kotlin.math.abs(y), 0.0001f)
    }
}
