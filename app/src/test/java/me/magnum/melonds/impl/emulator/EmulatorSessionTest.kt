package me.magnum.melonds.impl.emulator

import me.magnum.melonds.domain.model.ConsoleType
import me.magnum.melonds.domain.model.enhancement.ProfileIntegrity
import me.magnum.melonds.domain.model.retroachievements.RetroAchievementsEffectiveMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EmulatorSessionTest {
    @Test
    fun sessionStatusSnapshotLatchesPreservesAcrossFallbackAndClearsOnReset() {
        val session = EmulatorSession()
        val snapshot = SessionStatusSnapshot(
            profileIntegrity = ProfileIntegrity.ENHANCED,
            effectiveArm9Percent = 125,
            retroAchievementsMode = RetroAchievementsEffectiveMode.CASUAL,
        )
        val sessionType = EmulatorSession.SessionType.FirmwareSession(ConsoleType.DS)

        session.startSession(
            areRetroAchievementsEnabled = true,
            isRetroAchievementsHardcoreModeEnabled = false,
            sessionType = sessionType,
            sessionStatusSnapshot = snapshot,
        )
        assertEquals(snapshot, session.sessionStatusSnapshot())

        val replacementSnapshot = snapshot.copy(
            profileIntegrity = ProfileIntegrity.ORIGINAL,
            effectiveArm9Percent = 100,
            retroAchievementsMode = RetroAchievementsEffectiveMode.OFF,
        )
        session.startSession(
            areRetroAchievementsEnabled = false,
            isRetroAchievementsHardcoreModeEnabled = false,
            sessionType = sessionType,
            sessionStatusSnapshot = replacementSnapshot,
        )
        assertEquals(snapshot, session.sessionStatusSnapshot())

        session.startSession(
            areRetroAchievementsEnabled = true,
            isRetroAchievementsHardcoreModeEnabled = false,
            sessionType = sessionType,
        )
        assertEquals(snapshot, session.sessionStatusSnapshot())

        session.reset()
        assertNull(session.sessionStatusSnapshot())
    }
}
