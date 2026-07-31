package me.magnum.melonds.debug

import android.app.Activity
import android.app.Presentation
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.TextView

/**
 * Debug-only physical display probe used by the Thor hardware acceptance tests.
 */
internal class ThorDisplayDiagnosticActivity : Activity() {
    private val presentations = mutableListOf<Presentation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(buildDisplayView(this, "PRIMARY ACTIVITY"))
    }

    override fun onStart() {
        super.onStart()
        val displayManager = getSystemService(DisplayManager::class.java)
        displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)
            .filter { it.displayId != display?.displayId }
            .forEach { secondaryDisplay ->
                Presentation(this, secondaryDisplay).apply {
                    setContentView(buildDisplayView(context, "SECONDARY PRESENTATION"))
                    show()
                    presentations += this
                }
            }
    }

    override fun onStop() {
        presentations.forEach(Presentation::dismiss)
        presentations.clear()
        super.onStop()
    }

    @Suppress("DEPRECATION")
    private fun buildDisplayView(context: Context, role: String): TextView {
        val currentDisplay = context.display
        val realSize = Point().also(currentDisplay::getRealSize)
        val windowManager = context.getSystemService(WindowManager::class.java)
        val windowBounds = windowManager.currentWindowMetrics.bounds
        val currentMode = currentDisplay.mode

        return TextView(context).apply {
            setBackgroundColor(Color.rgb(12, 18, 30))
            setTextColor(Color.rgb(208, 255, 236))
            gravity = Gravity.CENTER
            textSize = 22f
            setPadding(48, 48, 48, 48)
            text = buildString {
                appendLine("THORDS M2 DISPLAY PROBE")
                appendLine()
                appendLine("role: $role")
                appendLine("displayId: ${currentDisplay.displayId}")
                appendLine("name: ${currentDisplay.name}")
                appendLine("state: ${currentDisplay.state}")
                appendLine("real: ${realSize.x}x${realSize.y}")
                appendLine("window: ${windowBounds.width()}x${windowBounds.height()}")
                appendLine("mode: ${currentMode.physicalWidth}x${currentMode.physicalHeight} @ ${currentMode.refreshRate}Hz")
                append("presentation: ${(currentDisplay.flags and Display.FLAG_PRESENTATION) != 0}")
            }
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    Log.i(TAG, "probe_touch role=$role displayId=${currentDisplay.displayId} x=${event.x.toInt()} y=${event.y.toInt()}")
                }
                true
            }
        }
    }

    private companion object {
        const val TAG = "ThorDisplayProbe"
    }
}
