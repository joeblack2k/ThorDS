package me.magnum.melonds.ui.emulator.input

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import me.magnum.melonds.MelonEmulator.onScreenRelease
import me.magnum.melonds.domain.model.Input
import me.magnum.melonds.domain.model.Point

class HybridScreenTouchscreenInputHandler(inputListener: IInputListener) : BaseInputHandler(inputListener) {
    private val touchPoint = Point()
    private var touchActive = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val screenY = event.averageY()
        val bottomScreenTop = v.height / 2f
        val isInBottomScreen = screenY >= bottomScreenTop

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (!isInBottomScreen) {
                    return true
                }
                touchActive = true
                inputListener.onKeyPress(Input.TOUCHSCREEN)
                inputListener.onTouch(normalizeTouchCoordinates(event, v.width, v.height, bottomScreenTop))
            }
            MotionEvent.ACTION_MOVE -> {
                if (touchActive) {
                    inputListener.onTouch(normalizeTouchCoordinates(event, v.width, v.height, bottomScreenTop))
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (touchActive) {
                    inputListener.onKeyReleased(Input.TOUCHSCREEN)
                    onScreenRelease()
                    touchActive = false
                }
            }
        }

        return true
    }

    private fun normalizeTouchCoordinates(event: MotionEvent, viewWidth: Int, viewHeight: Int, bottomScreenTop: Float): Point {
        val averageTouchX = event.averageX()
        val averageTouchY = event.averageY()
        val bottomScreenHeight = (viewHeight - bottomScreenTop).coerceAtLeast(1f)

        touchPoint.x = (averageTouchX / viewWidth * 256f).toInt().coerceIn(0, 255)
        touchPoint.y = ((averageTouchY - bottomScreenTop) / bottomScreenHeight * 192f).toInt().coerceIn(0, 191)
        return touchPoint
    }

    private fun MotionEvent.averageX(): Float {
        var average = 0f
        for (i in 0 until pointerCount) {
            average += getX(i)
        }
        return average / pointerCount
    }

    private fun MotionEvent.averageY(): Float {
        var average = 0f
        for (i in 0 until pointerCount) {
            average += getY(i)
        }
        return average / pointerCount
    }

}
