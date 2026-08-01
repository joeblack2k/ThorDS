package me.magnum.melonds.ui.emulator

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmulatorViewModelLaunchPolicyTest {
    @Test
    fun launchDecisionFailureIsFailClosedAndCleansUpEndpointBeforeBootstrap() {
        val source = File("src/main/java/me/magnum/melonds/ui/emulator/EmulatorViewModel.kt").readText()
        val launchRom = source
            .substringAfter("private suspend fun launchRom(rom: Rom) = coroutineScope {")
            .substringBefore("    private suspend fun isRetroAchievementsEnabledForLaunch")
        val decisionStart = launchRom.indexOf("val launchDecision =")
        val cancellationCatch = launchRom.indexOf("catch (exception: CancellationException)", decisionStart)
        val cancellationRethrow = launchRom.indexOf("throw exception", cancellationCatch)
        val failureCatch = launchRom.indexOf("catch (exception: Throwable)", cancellationRethrow)
        val failureEnd = launchRom.indexOf("\n            } else {", failureCatch)
        val failurePath = launchRom.substring(failureCatch, failureEnd)
        val bootstrapStart = launchRom.indexOf("startEmulatorSession(", decisionStart)

        assertTrue("launch decision block must exist", decisionStart >= 0)
        assertTrue("CancellationException must be rethrown", cancellationCatch > decisionStart && cancellationRethrow > cancellationCatch)
        assertTrue("generic failure must be handled after cancellation", failureCatch > cancellationRethrow)
        assertTrue("failure path must log", failurePath.contains("Log.e(\"EmulatorViewModel\""))
        assertTrue("failure path must end the endpoint session", failurePath.contains("retroAchievementsEndpointProvider.endSession()"))
        assertTrue("failure path must report the generic ROM error", failurePath.contains("_emulatorState.value = EmulatorState.RomLoadError"))
        assertTrue("failure path must return before bootstrap", failurePath.contains("return@coroutineScope"))
        assertFalse("failure path must not downgrade to a fallback launch decision", failurePath.contains("RetroAchievementsLaunchDecision("))
        assertTrue("bootstrap must remain after the fail-closed return", bootstrapStart > failureEnd)
    }
}
