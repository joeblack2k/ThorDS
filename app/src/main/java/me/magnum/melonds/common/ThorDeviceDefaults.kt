package me.magnum.melonds.common

import android.content.SharedPreferences
import android.os.Build
import androidx.core.content.edit

object ThorDeviceDefaults {
    const val SAFE_MODE_KEY = "thords_safe_mode"
    const val SOFT_INPUT_BEHAVIOUR_KEY = "soft_input_behaviour"
    const val HIDDEN_SOFT_INPUT_BEHAVIOUR = "always_invisible"

    fun apply(
        sharedPreferences: SharedPreferences,
        manufacturer: String = Build.MANUFACTURER,
        model: String = Build.MODEL,
    ) {
        if (!shouldApplySoftInputDefault(manufacturer, model, sharedPreferences.contains(SOFT_INPUT_BEHAVIOUR_KEY))) {
            return
        }

        sharedPreferences.edit {
            putString(SOFT_INPUT_BEHAVIOUR_KEY, HIDDEN_SOFT_INPUT_BEHAVIOUR)
        }
    }

    fun shouldApplySoftInputDefault(manufacturer: String, model: String, hasUserPreference: Boolean): Boolean {
        return ThorDeviceCapabilities.isThor(manufacturer, model) && !hasUserPreference
    }
}
