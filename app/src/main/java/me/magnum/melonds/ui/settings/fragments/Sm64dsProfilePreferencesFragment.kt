package me.magnum.melonds.ui.settings.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import me.magnum.melonds.R
import me.magnum.melonds.domain.model.enhancement.ProfilePreferences
import me.magnum.melonds.domain.model.enhancement.SharedPreferencesProfilePreferencesRepository
import me.magnum.melonds.domain.model.enhancement.defaultSm64dsEnhancedProfilePreferences
import me.magnum.melonds.domain.model.enhancement.sm64dsExactIdentity
import me.magnum.melonds.domain.model.enhancement.withSm64dsFrameRate
import me.magnum.melonds.ui.settings.PreferenceFragmentTitleProvider

class Sm64dsProfilePreferencesFragment : BasePreferenceFragment(), PreferenceFragmentTitleProvider {
    private val repository by lazy { SharedPreferencesProfilePreferencesRepository(requireContext()) }
    private lateinit var draft: ProfilePreferences

    override fun getTitle() = getString(R.string.thords_sm64ds_profile)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_sm64ds_profile, rootKey)
        val key = sm64dsExactIdentity.stableKey()
        draft = if (repository.contains(key)) repository.read(key) else defaultSm64dsEnhancedProfilePreferences()

        val aspect = findPreference<ListPreference>("sm64ds_aspect")!!
        val fps = findPreference<ListPreference>("sm64ds_fps")!!
        val analog = findPreference<SwitchPreference>("sm64ds_analog")!!
        val camera = findPreference<SwitchPreference>("sm64ds_camera")!!
        val arm9 = findPreference<SeekBarPreference>("sm64ds_arm9")!!

        aspect.value = if (draft.enabledEnhancements["true-widescreen"] != false) "16:9" else "4:3"
        fps.value = if (draft.enabledEnhancements["60fps-dev-cadence"] == true) "60" else "30"
        analog.isChecked = draft.enabledEnhancements["analog"] != false
        camera.isChecked = draft.enabledEnhancements["right-stick-camera"] != false
        arm9.value = arm9Index(draft.requestedArm9Percent)
        arm9.summary = "${arm9Percent(arm9.value)}%"

        aspect.setOnPreferenceChangeListener { _, value ->
            draft = draft.withEnhancements("true-widescreen" to (value == "16:9"))
            true
        }
        fps.setOnPreferenceChangeListener { _, value ->
            draft = draft.withSm64dsFrameRate(value == "60")
            true
        }
        analog.setOnPreferenceChangeListener { _, value ->
            draft = draft.withEnhancements("analog" to (value as Boolean))
            true
        }
        camera.setOnPreferenceChangeListener { _, value ->
            draft = draft.withEnhancements("right-stick-camera" to (value as Boolean))
            true
        }
        arm9.setOnPreferenceChangeListener { _, value ->
            val index = value as Int
            draft = draft.copy(requestedArm9Percent = arm9Percent(index))
            arm9.summary = "${arm9Percent(index)}%"
            true
        }
        findPreference<Preference>("sm64ds_save")!!.setOnPreferenceClickListener {
            draft = draft
                .withEnhancements(
                    "true-widescreen" to (aspect.value == "16:9"),
                    "analog" to analog.isChecked,
                    "right-stick-camera" to camera.isChecked,
                )
                .withSm64dsFrameRate(fps.value == "60")
                .copy(requestedArm9Percent = arm9Percent(arm9.value))
            repository.write(key, draft)
            Toast.makeText(requireContext(), R.string.thords_sm64ds_saved, Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun ProfilePreferences.withEnhancements(vararg updates: Pair<String, Boolean>): ProfilePreferences {
        return copy(enabledEnhancements = enabledEnhancements + updates.toMap())
    }

    private fun arm9Index(percent: Int): Int = when (percent) {
        100 -> 0
        125 -> 1
        150 -> 2
        175 -> 3
        else -> 2
    }

    private fun arm9Percent(index: Int): Int = 100 + index.coerceIn(0, 3) * 25
}
