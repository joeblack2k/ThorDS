package me.magnum.melonds.ui.emulator.input

import android.annotation.SuppressLint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.MotionEvent.PointerCoords
import android.view.View
import me.magnum.melonds.MelonEmulator.onScreenRelease
import me.magnum.melonds.domain.model.Input
import me.magnum.melonds.domain.model.Point

class TouchscreenInputHandler(
    inputListener: IInputListener,
    private val viewRectProvider: (() -> RectF?)? = null,
) : BaseInputHandler(inputListener) {
    private val touchPoint: Point = Point()

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val viewport = viewRectProvider?.invoke()
                val accepted = isInsideViewport(event, viewport)
                TouchPipelineTrace.begin(event, viewport, v.display?.displayId, accepted)
                if (!accepted) {
                    onScreenRelease()
                    return false
                }
                inputListener.onKeyPress(Input.TOUCHSCREEN)
                inputListener.onTouch(normalizeTouchCoordinates(event, v.width, v.height))
            }
            MotionEvent.ACTION_MOVE -> {
                TouchPipelineTrace.record(
                    "android_move",
                    "pointerCount" to event.pointerCount,
                    "localX" to event.x,
                    "localY" to event.y,
                )
                inputListener.onTouch(normalizeTouchCoordinates(event, v.width, v.height))
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                inputListener.onKeyReleased(Input.TOUCHSCREEN)
                onScreenRelease()
                TouchPipelineTrace.record(
                    if (event.actionMasked == MotionEvent.ACTION_CANCEL) "android_cancel" else "android_release",
                )
            }
        }
        return true
    }

    private fun normalizeTouchCoordinates(event: MotionEvent, viewWidth: Int, viewHeight: Int): Point {
        var averageTouchX = 0f
        var averageTouchY = 0f
        val pointerCoordinates = PointerCoords()

        for (i in 0 until event.pointerCount) {
            event.getPointerCoords(i, pointerCoordinates)
            averageTouchX += pointerCoordinates.x
            averageTouchY += pointerCoordinates.y
        }
        averageTouchX /= event.pointerCount
        averageTouchY /= event.pointerCount

        val rect = viewRectProvider?.invoke()
        if (rect == null || rect.width() <= 0f || rect.height() <= 0f) {
            touchPoint.x = (averageTouchX / viewWidth * 256).toInt().coerceIn(0, 255)
            touchPoint.y = (averageTouchY / viewHeight * 192).toInt().coerceIn(0, 191)
            return touchPoint
        }

        val normalizedX = ((averageTouchX - rect.left) / rect.width() * 256f)
        val normalizedY = ((averageTouchY - rect.top) / rect.height() * 192f)

        touchPoint.x = normalizedX.toInt().coerceIn(0, 255)
        touchPoint.y = normalizedY.toInt().coerceIn(0, 191)
        return touchPoint
    }

    private fun isInsideViewport(event: MotionEvent, rect: RectF?): Boolean {
        rect ?: return true
        return rect.width() > 0f && rect.height() > 0f &&
            event.x >= rect.left && event.x < rect.right &&
            event.y >= rect.top && event.y < rect.bottom
    }
}
