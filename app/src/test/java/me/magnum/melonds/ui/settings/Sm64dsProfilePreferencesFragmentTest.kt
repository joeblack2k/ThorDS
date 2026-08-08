package me.magnum.melonds.ui.settings

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class Sm64dsProfilePreferencesFragmentTest {
    @Test
    fun saveNormalizesEveryVisibleControlBeforeWriting() {
        val source = File(
            "src/main/java/me/magnum/melonds/ui/settings/fragments/Sm64dsProfilePreferencesFragment.kt",
        ).readText()
        val save = source
            .substringAfter("findPreference<Preference>(\"sm64ds_save\")!!.setOnPreferenceClickListener")
            .substringBefore("Toast.makeText")

        assertTrue(save.contains("\"true-widescreen\" to (aspect.value == \"16:9\")"))
        assertTrue(save.contains("\"analog\" to analog.isChecked"))
        assertTrue(save.contains("\"right-stick-camera\" to camera.isChecked"))
        assertTrue(save.contains(".withSm64dsFrameRate(fps.value == \"60\")"))
        assertTrue(save.contains(".copy(requestedArm9Percent = arm9Percent(arm9.value))"))
        assertTrue(save.indexOf("draft = draft") < save.indexOf("repository.write(key, draft)"))
    }
}
