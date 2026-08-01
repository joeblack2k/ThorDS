package me.magnum.melonds.ui.emulator

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmulatorViewModelSessionStatusTest {
    @Test
    fun launchLatchesStatusAndPauseMenuOnlyReadsTheSessionSnapshot() {
        val viewModelSource = File("src/main/java/me/magnum/melonds/ui/emulator/EmulatorViewModel.kt").readText()
        val launchRom = viewModelSource
            .substringAfter("private suspend fun launchRom(rom: Rom) = coroutineScope {")
            .substringBefore("    private suspend fun isRetroAchievementsEnabledForLaunch")
        val bootstrapStart = launchRom.indexOf("startEmulatorSession(")
        val sessionStart = launchRom.substring(bootstrapStart).substringBefore("            startObservingMainScreenBackground()")

        assertTrue("ROM launch must pass the resolved session status", sessionStart.contains("sessionStatusSnapshot = SessionStatusSnapshot("))

        val pausePath = viewModelSource
            .substringAfter("fun pauseEmulator(showPauseMenu: Boolean)")
            .substringBefore("    fun resumeEmulator()")
        assertTrue("pause menu must read the latched session snapshot", pausePath.contains("emulatorSession.sessionStatusSnapshot()"))
        assertFalse("pause path must not resolve a new launch policy", pausePath.contains("profileLaunchPlanner"))

        val activitySource = File("src/main/java/me/magnum/melonds/ui/emulator/EmulatorActivity.kt").readText()
        val pauseDialog = activitySource
            .substringAfter("private fun showPauseMenu(pauseMenu: PauseMenu)")
            .substringBefore("    private fun formatSessionStatus")
        assertTrue("pause dialog must preserve the default menu path", pauseDialog.contains("if (pauseMenu.sessionStatus == null)"))
        assertTrue("default pause dialog must keep selectable options", pauseDialog.contains(".setItems(options)"))
        assertTrue("pause dialog must render the read-only status", pauseDialog.contains("text = formatSessionStatus(sessionStatus)"))
        assertTrue("pause dialog must keep status in a scrollable list header", pauseDialog.contains("addHeaderView"))
        assertTrue("pause dialog must keep options and status in one custom view", pauseDialog.contains(".setView(optionList)"))
        assertTrue("pause dialog must keep selectable options", pauseDialog.contains("optionList.setOnItemClickListener"))
        assertFalse("pause dialog must not combine message and items content modes", pauseDialog.contains(".setMessage("))
    }
}
