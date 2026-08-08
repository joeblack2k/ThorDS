package me.magnum.melonds.ui.settings.fragments

import android.os.Bundle
import me.magnum.melonds.R
import me.magnum.melonds.ui.settings.PreferenceFragmentTitleProvider

class ProfilesPreferencesFragment : BasePreferenceFragment(), PreferenceFragmentTitleProvider {
    override fun getTitle() = getString(R.string.thords_profiles)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_profiles, rootKey)
    }
}
