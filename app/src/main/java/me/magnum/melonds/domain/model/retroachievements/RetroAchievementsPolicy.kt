package me.magnum.melonds.domain.model.retroachievements

import me.magnum.melonds.domain.model.Cheat
import me.magnum.melonds.domain.model.enhancement.ProfileRaMode
import me.magnum.melonds.domain.model.enhancement.RuntimeActionReplayCode

enum class RetroAchievementsProfile {
    ORIGINAL,
    ENHANCED,
}

enum class RetroAchievementsEffectiveMode {
    OFF,
    CASUAL,
    HARDCORE,
    BLOCKED,
}

enum class RetroAchievementsPolicyReason(val code: String) {
    ENHANCED_PROFILE("enhanced_profile"),
    ACTIVE_ENHANCEMENTS("active_enhancements"),
    CURATED_RUNTIME_CODES("curated_runtime_codes"),
    ENABLED_USER_CHEATS("enabled_user_cheats"),
    ARM9_PERCENT_NOT_100("arm9_percent_not_100"),
    REWIND_ENABLED("rewind_enabled"),
    STATE_LOAD_ENABLED("state_load_enabled"),
    SAVE_STATE_RESUME_ENABLED("save_state_resume_enabled"),
    SLOWDOWN_ENABLED("slowdown_enabled"),
    FRAME_ADVANCE_ENABLED("frame_advance_enabled"),
}

data class RetroAchievementsRuntimeFeaturePermissions(
    val allowEnhancements: Boolean,
    val allowCuratedRuntimeCodes: Boolean,
    val allowUserCheats: Boolean,
    val allowArm9Overclock: Boolean,
    val allowRewind: Boolean,
    val allowStateLoad: Boolean,
    val allowSlowdown: Boolean,
    val allowFrameAdvance: Boolean,
)

data class RetroAchievementsPolicyInput(
    val requestedRaMode: ProfileRaMode,
    val profile: RetroAchievementsProfile,
    val activeEnhancementIds: Set<String> = emptySet(),
    val curatedRuntimeCodes: List<RuntimeActionReplayCode> = emptyList(),
    val enabledUserCheats: List<Cheat> = emptyList(),
    val arm9Percent: Int = 100,
    val rewindEnabled: Boolean = false,
    val stateLoadEnabled: Boolean = false,
    val saveStateResumeEnabled: Boolean = false,
    val slowdownEnabled: Boolean = false,
    val frameAdvanceEnabled: Boolean = false,
)

data class RetroAchievementsPolicy(
    val requestedRaMode: ProfileRaMode,
    val effectiveMode: RetroAchievementsEffectiveMode,
    val reasonCodes: List<RetroAchievementsPolicyReason>,
    val requiresReset: Boolean,
    val runtimeFeaturePermissions: RetroAchievementsRuntimeFeaturePermissions,
) {
    val reasonCodeValues: List<String>
        get() = reasonCodes.map { it.code }
}

class RetroAchievementsPolicyResolver {
    fun resolve(input: RetroAchievementsPolicyInput): RetroAchievementsPolicy {
        val conflicts = hardcoreConflicts(input)
        val effectiveMode = when (input.requestedRaMode) {
            ProfileRaMode.OFF -> RetroAchievementsEffectiveMode.OFF
            ProfileRaMode.CASUAL -> RetroAchievementsEffectiveMode.CASUAL
            ProfileRaMode.HARDCORE -> if (conflicts.isEmpty()) {
                RetroAchievementsEffectiveMode.HARDCORE
            } else {
                RetroAchievementsEffectiveMode.BLOCKED
            }
        }
        return RetroAchievementsPolicy(
            requestedRaMode = input.requestedRaMode,
            effectiveMode = effectiveMode,
            reasonCodes = if (input.requestedRaMode == ProfileRaMode.HARDCORE) conflicts else emptyList(),
            requiresReset = input.requestedRaMode == ProfileRaMode.HARDCORE,
            runtimeFeaturePermissions = when (effectiveMode) {
                RetroAchievementsEffectiveMode.OFF,
                RetroAchievementsEffectiveMode.CASUAL,
                -> allRuntimeFeaturesAllowed()

                RetroAchievementsEffectiveMode.HARDCORE,
                RetroAchievementsEffectiveMode.BLOCKED,
                -> hardcoreRuntimeFeatures()
            },
        )
    }

    fun resolve(
        requestedRaMode: ProfileRaMode,
        profile: RetroAchievementsProfile,
        activeEnhancementIds: Set<String> = emptySet(),
        curatedRuntimeCodes: List<RuntimeActionReplayCode> = emptyList(),
        enabledUserCheats: List<Cheat> = emptyList(),
        arm9Percent: Int = 100,
        rewindEnabled: Boolean = false,
        stateLoadEnabled: Boolean = false,
        saveStateResumeEnabled: Boolean = false,
        slowdownEnabled: Boolean = false,
        frameAdvanceEnabled: Boolean = false,
    ): RetroAchievementsPolicy {
        return resolve(
            RetroAchievementsPolicyInput(
                requestedRaMode = requestedRaMode,
                profile = profile,
                activeEnhancementIds = activeEnhancementIds,
                curatedRuntimeCodes = curatedRuntimeCodes,
                enabledUserCheats = enabledUserCheats,
                arm9Percent = arm9Percent,
                rewindEnabled = rewindEnabled,
                stateLoadEnabled = stateLoadEnabled,
                saveStateResumeEnabled = saveStateResumeEnabled,
                slowdownEnabled = slowdownEnabled,
                frameAdvanceEnabled = frameAdvanceEnabled,
            ),
        )
    }

    private fun hardcoreConflicts(input: RetroAchievementsPolicyInput): List<RetroAchievementsPolicyReason> {
        return buildList {
            if (input.profile == RetroAchievementsProfile.ENHANCED) {
                add(RetroAchievementsPolicyReason.ENHANCED_PROFILE)
            }
            if (input.activeEnhancementIds.isNotEmpty()) {
                add(RetroAchievementsPolicyReason.ACTIVE_ENHANCEMENTS)
            }
            if (input.curatedRuntimeCodes.isNotEmpty()) {
                add(RetroAchievementsPolicyReason.CURATED_RUNTIME_CODES)
            }
            if (input.enabledUserCheats.any { it.enabled }) {
                add(RetroAchievementsPolicyReason.ENABLED_USER_CHEATS)
            }
            if (input.arm9Percent != 100) {
                add(RetroAchievementsPolicyReason.ARM9_PERCENT_NOT_100)
            }
            if (input.rewindEnabled) {
                add(RetroAchievementsPolicyReason.REWIND_ENABLED)
            }
            if (input.stateLoadEnabled) {
                add(RetroAchievementsPolicyReason.STATE_LOAD_ENABLED)
            }
            if (input.saveStateResumeEnabled) {
                add(RetroAchievementsPolicyReason.SAVE_STATE_RESUME_ENABLED)
            }
            if (input.slowdownEnabled) {
                add(RetroAchievementsPolicyReason.SLOWDOWN_ENABLED)
            }
            if (input.frameAdvanceEnabled) {
                add(RetroAchievementsPolicyReason.FRAME_ADVANCE_ENABLED)
            }
        }
    }

    private fun allRuntimeFeaturesAllowed() = RetroAchievementsRuntimeFeaturePermissions(
        allowEnhancements = true,
        allowCuratedRuntimeCodes = true,
        allowUserCheats = true,
        allowArm9Overclock = true,
        allowRewind = true,
        allowStateLoad = true,
        allowSlowdown = true,
        allowFrameAdvance = true,
    )

    private fun hardcoreRuntimeFeatures() = RetroAchievementsRuntimeFeaturePermissions(
        allowEnhancements = false,
        allowCuratedRuntimeCodes = false,
        allowUserCheats = false,
        allowArm9Overclock = false,
        allowRewind = false,
        allowStateLoad = false,
        allowSlowdown = false,
        allowFrameAdvance = false,
    )
}
