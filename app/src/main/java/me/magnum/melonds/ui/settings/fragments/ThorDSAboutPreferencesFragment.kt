package me.magnum.melonds.ui.settings.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import me.magnum.melonds.R
import me.magnum.melonds.ui.settings.PreferenceFragmentTitleProvider

class ThorDSAboutPreferencesFragment : BasePreferenceFragment(), PreferenceFragmentTitleProvider {
    override fun getTitle() = getString(R.string.thords_about)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_thords_about, rootKey)
        link("thords_about_product_source", "https://github.com/joeblack2k/ThorDS")
        link("thords_about_upstream_source", "https://github.com/SapphireRhodonite/melonDS-android")
    }

    private fun link(key: String, url: String) {
        findPreference<Preference>(key)?.intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }
}
