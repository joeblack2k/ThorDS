package me.magnum.melonds.ui.emulator.input

import android.util.Log
import me.magnum.melonds.MelonEmulator
import me.magnum.melonds.domain.model.Input
import me.magnum.melonds.domain.model.Point

class MelonTouchHandler : IInputListener {
    private var isLidClosed = false

    override fun onKeyPress(key: Input) {
        if (key == Input.HINGE) {
            handleHingePress()
        } else {
            MelonEmulator.onInputDown(key)
        }
    }

    override fun onKeyReleased(key: Input) {
        if (key != Input.HINGE) {
            MelonEmulator.onInputUp(key)
        }
    }

    override fun onTouch(point: Point) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.i(TAG, "mapped_ds_touch x=${point.x} y=${point.y}")
        }
        MelonEmulator.onScreenTouch(point.x, point.y)
    }

    private fun handleHingePress() {
        isLidClosed = !isLidClosed
        if (isLidClosed) {
            MelonEmulator.onInputDown(Input.HINGE)
        } else {
            MelonEmulator.onInputUp(Input.HINGE)
        }
    }

    private companion object {
        const val TAG = "ThorDsTouch"
    }
}
