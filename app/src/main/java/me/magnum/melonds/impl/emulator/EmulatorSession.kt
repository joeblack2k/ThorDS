package me.magnum.melonds.impl.emulator

import me.magnum.melonds.domain.model.ConsoleType
import me.magnum.melonds.domain.model.enhancement.ProfileIntegrity
import me.magnum.melonds.domain.model.enhancement.WidescreenPresentationMode
import me.magnum.melonds.domain.model.rom.Rom
import me.magnum.melonds.domain.model.emulator.EmulatorSessionUpdateAction
import me.magnum.melonds.domain.model.retroachievements.GameAchievementData
import me.magnum.melonds.domain.model.retroachievements.RetroAchievementsEffectiveMode

data class SessionStatusSnapshot(
    val profileIntegrity: ProfileIntegrity,
    val effectiveArm9Percent: Int,
    val retroAchievementsMode: RetroAchievementsEffectiveMode,
    val requestedWidescreenMode: WidescreenPresentationMode,
    val effectiveWidescreenMode: WidescreenPresentationMode,
)

class EmulatorSession {

    var isRetroAchievementsHardcoreModeEnabled = false
        private set

    private var areRetroAchievementsEnabled = false
    private var sessionHasAchievements = false
    private var retroAchievementsOfflineModeEnabled = false
    private var sessionType: SessionType? = null
    private var sessionStatusSnapshot: SessionStatusSnapshot? = null

    fun startSession(
        areRetroAchievementsEnabled: Boolean,
        isRetroAchievementsHardcoreModeEnabled: Boolean,
        sessionType: SessionType,
        sessionStatusSnapshot: SessionStatusSnapshot? = null,
    ) {
        this.areRetroAchievementsEnabled = areRetroAchievementsEnabled
        // Hardcore mode can only be enabled if RetroAchievements are available when the session starts
        this.isRetroAchievementsHardcoreModeEnabled = areRetroAchievementsEnabled && isRetroAchievementsHardcoreModeEnabled
        this.retroAchievementsOfflineModeEnabled = false
        this.sessionType = sessionType
        sessionStatusSnapshot?.let {
            if (this.sessionStatusSnapshot == null) {
                this.sessionStatusSnapshot = it
            }
        }
    }

    fun reset() {
        areRetroAchievementsEnabled = false
        isRetroAchievementsHardcoreModeEnabled = false
        sessionHasAchievements = false
        retroAchievementsOfflineModeEnabled = false
        sessionType = null
        sessionStatusSnapshot = null
    }

    fun updateRetroAchievementsSettings(areRetroAchievementsEnabled: Boolean, isHardcoreModeEnabled: Boolean): List<EmulatorSessionUpdateAction> {
        val updateActions = mutableListOf<EmulatorSessionUpdateAction>()

        if (this.areRetroAchievementsEnabled != areRetroAchievementsEnabled) {
            if (areRetroAchievementsEnabled) {
                updateActions.add(EmulatorSessionUpdateAction.EnableRetroAchievements)
            } else {
                updateActions.add(EmulatorSessionUpdateAction.DisableRetroAchievements)
            }
            this.areRetroAchievementsEnabled = areRetroAchievementsEnabled
        }

        if (areRetroAchievementsEnabled && this.isRetroAchievementsHardcoreModeEnabled != isHardcoreModeEnabled) {
            // It's not possible to change the hardcore mode setting while the emulator session is running
            updateActions.add(EmulatorSessionUpdateAction.NotifyRetroAchievementsModeSwitch)
        }

        return updateActions
    }

    fun updateRetroAchievementsIntegrationStatus(integrationStatus: GameAchievementData.IntegrationStatus) {
        sessionHasAchievements = integrationStatus == GameAchievementData.IntegrationStatus.ENABLED_FULL
        if (!sessionHasAchievements) {
            retroAchievementsOfflineModeEnabled = false
        }
    }

    fun updateRetroAchievementsOfflineModeEnabled(enabled: Boolean) {
        retroAchievementsOfflineModeEnabled = enabled && areRetroAchievementsEnabled()
    }

    fun isRetroAchievementsEnabledForSession(): Boolean {
        return areRetroAchievementsEnabled
    }

    fun areRetroAchievementsEnabled(): Boolean {
        return areRetroAchievementsEnabled && sessionHasAchievements
    }

    fun isRetroAchievementsOfflineModeEnabled(): Boolean {
        return areRetroAchievementsEnabled() && retroAchievementsOfflineModeEnabled
    }

    fun areLeaderboardsEnabled(): Boolean {
        // Leaderboards are only enabled in RetroAchievements hardcore mode
        return isRetroAchievementsHardcoreModeEnabled
    }

    fun areSaveStatesAllowed(): Boolean {
        return true
    }

    fun areSaveStateLoadsAllowed(): Boolean {
        // Cannot load save-states when RA hardcore is enabled.
        return !isRetroAchievementsHardcoreModeEnabled || !areRetroAchievementsEnabled()
    }

    fun areCheatsEnabled(): Boolean {
        // Cannot use cheats when RA hardcore is enabled
        return !isRetroAchievementsHardcoreModeEnabled || !areRetroAchievementsEnabled
    }

    fun currentSessionType(): SessionType? {
        return sessionType
    }

    fun sessionStatusSnapshot(): SessionStatusSnapshot? {
        return sessionStatusSnapshot
    }

    sealed class SessionType {
        data class RomSession(val rom: Rom) : SessionType()
        data class FirmwareSession(val consoleType: ConsoleType) : SessionType()
    }
}
