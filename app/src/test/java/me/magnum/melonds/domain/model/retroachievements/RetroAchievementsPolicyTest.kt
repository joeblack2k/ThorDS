package me.magnum.melonds.domain.model.retroachievements

import me.magnum.melonds.domain.model.Cheat
import me.magnum.melonds.domain.model.enhancement.ProfileRaMode
import me.magnum.melonds.domain.model.enhancement.RuntimeActionReplayCode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RetroAchievementsPolicyTest {
    private val resolver = RetroAchievementsPolicyResolver()
    private val curatedCode = RuntimeActionReplayCode(
        id = "curated.test",
        codeWords = listOf("00000000 00000000"),
        codeSha256 = "a".repeat(64),
    )
    private val enabledCheat = Cheat(
        id = 1,
        cheatDatabaseId = 1,
        name = "Enabled",
        description = null,
        code = "00000000 00000000",
        enabled = true,
    )
    private val disabledCheat = enabledCheat.copy(enabled = false)

    @Test
    fun offRemainsOffAndDoesNotGateRuntimeFeatures() {
        val policy = resolver.resolve(
            requestedRaMode = ProfileRaMode.OFF,
            profile = RetroAchievementsProfile.ENHANCED,
            activeEnhancementIds = setOf("analog"),
            curatedRuntimeCodes = listOf(curatedCode),
            enabledUserCheats = listOf(enabledCheat),
            arm9Percent = 125,
            rewindEnabled = true,
            stateLoadEnabled = true,
            saveStateResumeEnabled = true,
            slowdownEnabled = true,
            frameAdvanceEnabled = true,
        )

        assertEquals(RetroAchievementsEffectiveMode.OFF, policy.effectiveMode)
        assertTrue(policy.reasonCodes.isEmpty())
        assertFalse(policy.requiresReset)
        assertAllRuntimeFeaturesAllowed(policy)
    }

    @Test
    fun originalProfileKeepsOffAndCasualRequests() {
        listOf(
            ProfileRaMode.OFF to RetroAchievementsEffectiveMode.OFF,
            ProfileRaMode.CASUAL to RetroAchievementsEffectiveMode.CASUAL,
        ).forEach { (requestedMode, expectedMode) ->
            val policy = resolver.resolve(
                requestedRaMode = requestedMode,
                profile = RetroAchievementsProfile.ORIGINAL,
            )

            assertEquals(expectedMode, policy.effectiveMode)
            assertTrue(policy.reasonCodes.isEmpty())
            assertFalse(policy.requiresReset)
            assertAllRuntimeFeaturesAllowed(policy)
        }
    }

    @Test
    fun casualRemainsCasualWithEnhancementsAndDoesNotDisableThem() {
        val policy = resolver.resolve(
            requestedRaMode = ProfileRaMode.CASUAL,
            profile = RetroAchievementsProfile.ENHANCED,
            activeEnhancementIds = setOf("analog", "true-widescreen"),
            curatedRuntimeCodes = listOf(curatedCode),
            enabledUserCheats = listOf(enabledCheat),
            arm9Percent = 125,
            rewindEnabled = true,
            stateLoadEnabled = true,
            saveStateResumeEnabled = true,
            slowdownEnabled = true,
            frameAdvanceEnabled = true,
        )

        assertEquals(RetroAchievementsEffectiveMode.CASUAL, policy.effectiveMode)
        assertTrue(policy.reasonCodes.isEmpty())
        assertFalse(policy.requiresReset)
        assertAllRuntimeFeaturesAllowed(policy)
    }

    @Test
    fun cleanOriginalHardcoreIsEligibleAndRequiresReset() {
        val policy = resolver.resolve(
            requestedRaMode = ProfileRaMode.HARDCORE,
            profile = RetroAchievementsProfile.ORIGINAL,
        )

        assertEquals(RetroAchievementsEffectiveMode.HARDCORE, policy.effectiveMode)
        assertTrue(policy.reasonCodes.isEmpty())
        assertTrue(policy.requiresReset)
        assertNoIntegrityAffectingRuntimeFeatures(policy)
    }

    @Test
    fun lifecycleResumeDoesNotBlockHardcore() {
        val policy = resolver.resolve(
            requestedRaMode = ProfileRaMode.HARDCORE,
            profile = RetroAchievementsProfile.ORIGINAL,
            stateLoadEnabled = false,
            saveStateResumeEnabled = false,
        )

        assertEquals(RetroAchievementsEffectiveMode.HARDCORE, policy.effectiveMode)
        assertTrue(policy.reasonCodes.isEmpty())
    }

    @Test
    fun saveStateResumeBlocksHardcore() {
        val policy = resolver.resolve(
            requestedRaMode = ProfileRaMode.HARDCORE,
            profile = RetroAchievementsProfile.ORIGINAL,
            saveStateResumeEnabled = true,
        )

        assertEquals(RetroAchievementsEffectiveMode.BLOCKED, policy.effectiveMode)
        assertEquals(
            listOf("save_state_resume_enabled"),
            policy.reasonCodeValues,
        )
        assertTrue(policy.requiresReset)
    }

    @Test
    fun enhancedHardcoreIsBlockedWithoutDowngrading() {
        val policy = resolver.resolve(
            requestedRaMode = ProfileRaMode.HARDCORE,
            profile = RetroAchievementsProfile.ENHANCED,
            activeEnhancementIds = setOf("analog"),
        )

        assertEquals(RetroAchievementsEffectiveMode.BLOCKED, policy.effectiveMode)
        assertEquals(
            listOf("enhanced_profile", "active_enhancements"),
            policy.reasonCodeValues,
        )
        assertTrue(policy.requiresReset)
        assertNoIntegrityAffectingRuntimeFeatures(policy)
    }

    @Test
    fun hardcoreReportsEveryIntegrityConflictInStableOrder() {
        val policy = resolver.resolve(
            requestedRaMode = ProfileRaMode.HARDCORE,
            profile = RetroAchievementsProfile.ORIGINAL,
            activeEnhancementIds = setOf("analog"),
            curatedRuntimeCodes = listOf(curatedCode),
            enabledUserCheats = listOf(enabledCheat),
            arm9Percent = 101,
            rewindEnabled = true,
            stateLoadEnabled = true,
            saveStateResumeEnabled = true,
            slowdownEnabled = true,
            frameAdvanceEnabled = true,
        )

        assertEquals(RetroAchievementsEffectiveMode.BLOCKED, policy.effectiveMode)
        assertEquals(
            listOf(
                "active_enhancements",
                "curated_runtime_codes",
                "enabled_user_cheats",
                "arm9_percent_not_100",
                "rewind_enabled",
                "state_load_enabled",
                "save_state_resume_enabled",
                "slowdown_enabled",
                "frame_advance_enabled",
            ),
            policy.reasonCodeValues,
        )
    }

    @Test
    fun eachHardcoreConflictBlocksIndependently() {
        val cases = listOf(
            "enhanced profile" to RetroAchievementsPolicyInput(
                requestedRaMode = ProfileRaMode.HARDCORE,
                profile = RetroAchievementsProfile.ENHANCED,
            ),
            "active enhancement" to RetroAchievementsPolicyInput(
                requestedRaMode = ProfileRaMode.HARDCORE,
                profile = RetroAchievementsProfile.ORIGINAL,
                activeEnhancementIds = setOf("analog"),
            ),
            "curated runtime code" to RetroAchievementsPolicyInput(
                requestedRaMode = ProfileRaMode.HARDCORE,
                profile = RetroAchievementsProfile.ORIGINAL,
                curatedRuntimeCodes = listOf(curatedCode),
            ),
            "enabled user cheat" to RetroAchievementsPolicyInput(
                requestedRaMode = ProfileRaMode.HARDCORE,
                profile = RetroAchievementsProfile.ORIGINAL,
                enabledUserCheats = listOf(enabledCheat),
            ),
            "non-default ARM9" to RetroAchievementsPolicyInput(
                requestedRaMode = ProfileRaMode.HARDCORE,
                profile = RetroAchievementsProfile.ORIGINAL,
                arm9Percent = 99,
            ),
            "rewind" to RetroAchievementsPolicyInput(
                requestedRaMode = ProfileRaMode.HARDCORE,
                profile = RetroAchievementsProfile.ORIGINAL,
                rewindEnabled = true,
            ),
            "state load" to RetroAchievementsPolicyInput(
                requestedRaMode = ProfileRaMode.HARDCORE,
                profile = RetroAchievementsProfile.ORIGINAL,
                stateLoadEnabled = true,
            ),
            "save-state resume" to RetroAchievementsPolicyInput(
                requestedRaMode = ProfileRaMode.HARDCORE,
                profile = RetroAchievementsProfile.ORIGINAL,
                saveStateResumeEnabled = true,
            ),
            "slowdown" to RetroAchievementsPolicyInput(
                requestedRaMode = ProfileRaMode.HARDCORE,
                profile = RetroAchievementsProfile.ORIGINAL,
                slowdownEnabled = true,
            ),
            "frame advance" to RetroAchievementsPolicyInput(
                requestedRaMode = ProfileRaMode.HARDCORE,
                profile = RetroAchievementsProfile.ORIGINAL,
                frameAdvanceEnabled = true,
            ),
        )

        cases.forEach { (name, input) ->
            val policy = resolver.resolve(input)
            assertEquals(name, RetroAchievementsEffectiveMode.BLOCKED, policy.effectiveMode)
            assertEquals(name, 1, policy.reasonCodes.size)
            assertTrue(name, policy.requiresReset)
        }
    }

    @Test
    fun disabledUserCheatsDoNotBlockHardcore() {
        val policy = resolver.resolve(
            requestedRaMode = ProfileRaMode.HARDCORE,
            profile = RetroAchievementsProfile.ORIGINAL,
            enabledUserCheats = listOf(disabledCheat),
        )

        assertEquals(RetroAchievementsEffectiveMode.HARDCORE, policy.effectiveMode)
        assertTrue(policy.reasonCodes.isEmpty())
        assertFalse(policy.runtimeFeaturePermissions.allowUserCheats)
    }

    private fun assertAllRuntimeFeaturesAllowed(policy: RetroAchievementsPolicy) {
        with(policy.runtimeFeaturePermissions) {
            assertTrue(allowEnhancements)
            assertTrue(allowCuratedRuntimeCodes)
            assertTrue(allowUserCheats)
            assertTrue(allowArm9Overclock)
            assertTrue(allowRewind)
            assertTrue(allowStateLoad)
            assertTrue(allowSlowdown)
            assertTrue(allowFrameAdvance)
        }
    }

    private fun assertNoIntegrityAffectingRuntimeFeatures(policy: RetroAchievementsPolicy) {
        with(policy.runtimeFeaturePermissions) {
            assertFalse(allowEnhancements)
            assertFalse(allowCuratedRuntimeCodes)
            assertFalse(allowUserCheats)
            assertFalse(allowArm9Overclock)
            assertFalse(allowRewind)
            assertFalse(allowStateLoad)
            assertFalse(allowSlowdown)
            assertFalse(allowFrameAdvance)
        }
    }
}
