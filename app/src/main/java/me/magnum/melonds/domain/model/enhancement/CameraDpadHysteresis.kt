package me.magnum.melonds.domain.model.enhancement

import me.magnum.melonds.domain.model.Input
import kotlin.math.absoluteValue

class CameraDpadHysteresis(
    private val pressThreshold: Float = 0.55f,
    private val releaseThreshold: Float = 0.35f,
) {
    private var horizontal: Input? = null
    private var vertical: Input? = null

    fun update(x: Float, y: Float): Set<Input> {
        horizontal = updateAxis(x, horizontal, Input.LEFT, Input.RIGHT)
        vertical = updateAxis(y, vertical, Input.UP, Input.DOWN)
        return setOfNotNull(horizontal, vertical)
    }

    fun reset() {
        horizontal = null
        vertical = null
    }

    private fun updateAxis(value: Float, active: Input?, negative: Input, positive: Input): Input? {
        if (active != null && value.absoluteValue <= releaseThreshold) return null
        if (value >= pressThreshold) return positive
        if (value <= -pressThreshold) return negative
        return active
    }
}

data class CameraDpadInputEdges(
    val pressed: Set<Input> = emptySet(),
    val released: Set<Input> = emptySet(),
)

class CameraDpadInputState {
    private val controllerInputs = mutableSetOf<Input>()
    private var cameraInputs = emptySet<Input>()

    fun updateCamera(next: Set<Input>): CameraDpadInputEdges {
        val edges = CameraDpadInputEdges(
            pressed = (next - cameraInputs).filterNot(controllerInputs::contains).toSet(),
            released = (cameraInputs - next).filterNot(controllerInputs::contains).toSet(),
        )
        cameraInputs = next
        return edges
    }

    fun controllerPressed(input: Input): Boolean {
        return controllerInputs.add(input) && input !in cameraInputs
    }

    fun controllerReleased(input: Input): Boolean {
        return controllerInputs.remove(input) && input !in cameraInputs
    }

    fun releaseAll(): Set<Input> {
        val released = controllerInputs + cameraInputs
        controllerInputs.clear()
        cameraInputs = emptySet()
        return released
    }
}
