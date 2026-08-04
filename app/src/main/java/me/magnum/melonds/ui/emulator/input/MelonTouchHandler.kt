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
            if (key == Input.TOUCHSCREEN) {
                TouchPipelineTrace.record("input_release")
            }
        }
    }

    override fun onTouch(point: Point) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.i(TAG, "mapped_ds_touch x=${point.x} y=${point.y}")
        }
        TouchPipelineTrace.record("mapped_ds_touch", "dsX" to point.x, "dsY" to point.y, "jni" to "onScreenTouch")
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
