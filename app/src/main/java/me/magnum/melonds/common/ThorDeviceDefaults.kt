package me.magnum.melonds.common

import android.content.SharedPreferences
import android.os.Build
import androidx.core.content.edit

object ThorDeviceDefaults {
    const val SAFE_MODE_KEY = "thords_safe_mode"
    const val TRUE_WIDESCREEN_KEY = "thords_true_widescreen"
    const val SOFT_INPUT_BEHAVIOUR_KEY = "soft_input_behaviour"
    const val HIDDEN_SOFT_INPUT_BEHAVIOUR = "always_invisible"

    fun apply(
        sharedPreferences: SharedPreferences,
        manufacturer: String = Build.MANUFACTURER,
        model: String = Build.MODEL,
    ) {
        val applySoftInput = shouldApplySoftInputDefault(
            manufacturer,
            model,
            sharedPreferences.contains(SOFT_INPUT_BEHAVIOUR_KEY),
        )
        val applyTrueWidescreen = !sharedPreferences.contains(TRUE_WIDESCREEN_KEY)
        val repairThorWidescreenRenderer = shouldRepairWidescreenRenderer(
            manufacturer,
            model,
            sharedPreferences.getBoolean(TRUE_WIDESCREEN_KEY, false),
            sharedPreferences.getString("video_renderer", null),
        )
        if (!applySoftInput && !applyTrueWidescreen && !repairThorWidescreenRenderer) {
            return
        }

        sharedPreferences.edit {
            if (applySoftInput) {
                putString(SOFT_INPUT_BEHAVIOUR_KEY, HIDDEN_SOFT_INPUT_BEHAVIOUR)
            }
            if (applyTrueWidescreen) {
                putBoolean(TRUE_WIDESCREEN_KEY, defaultTrueWidescreenEnabled(manufacturer, model))
            }
            if (repairThorWidescreenRenderer) {
                putString("video_renderer", "vulkan")
            }
        }
    }

    fun shouldApplySoftInputDefault(manufacturer: String, model: String, hasUserPreference: Boolean): Boolean {
        return ThorDeviceCapabilities.isThor(manufacturer, model) && !hasUserPreference
    }

    fun defaultTrueWidescreenEnabled(manufacturer: String, model: String): Boolean {
        return ThorDeviceCapabilities.isThor(manufacturer, model)
    }

    fun shouldRepairWidescreenRenderer(
        manufacturer: String,
        model: String,
        trueWidescreenEnabled: Boolean,
        persistedRenderer: String?,
    ): Boolean {
        return ThorDeviceCapabilities.isThor(manufacturer, model) &&
            trueWidescreenEnabled &&
            persistedRenderer == "software"
    }
}
