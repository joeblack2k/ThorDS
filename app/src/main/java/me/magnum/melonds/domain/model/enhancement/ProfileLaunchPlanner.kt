package me.magnum.melonds.domain.model.enhancement

import me.magnum.melonds.domain.model.Cheat
import me.magnum.melonds.domain.model.RomInfo
import me.magnum.melonds.domain.model.rom.Rom
import me.magnum.melonds.domain.model.rom.config.RomGbaSlotConfig
import me.magnum.melonds.domain.model.retroachievements.RetroAchievementsPolicy
import me.magnum.melonds.domain.model.retroachievements.RetroAchievementsPolicyResolver

data class PlannedRomLaunch(
    val rom: Rom,
    val cheats: List<Cheat>,
    val plan: ResolvedSessionPlan,
    val retroAchievementsPolicy: RetroAchievementsPolicy,
    val requestedWidescreenMode: WidescreenPresentationMode,
    val effectiveWidescreenMode: WidescreenPresentationMode,
)

data class LaunchProfileResolution(
    val plan: ResolvedSessionPlan,
    val useSlot2Analog: Boolean,
    val retroAchievementsPolicy: RetroAchievementsPolicy,
    val requestedWidescreenMode: WidescreenPresentationMode,
    val effectiveWidescreenMode: WidescreenPresentationMode,
)

class ProfileLaunchPlanner(private val catalog: ProfileCatalog) {
    private val sessionPlanBuilder = SessionPlanBuilder(catalog)
    private val retroAchievementsPolicyResolver = RetroAchievementsPolicyResolver()

    fun plan(
        rom: Rom,
        romInfo: RomInfo?,
        userCheats: List<Cheat>,
        enhancementsEnabled: Boolean,
        trueWidescreenRequested: Boolean = true,
        trueWidescreenProductSupported: Boolean = false,
        developerWidescreenDiagnostic: Boolean = false,
        developerWidescreenDiagnosticSupported: Boolean = false,
        requestedRaMode: ProfileRaMode,
        saveStateResumeEnabled: Boolean = false,
        requestedArm9Percent: Int = Arm9OverclockPolicy.DEFAULT_PERCENT,
        profilePreferences: ProfilePreferences? = null,
    ): PlannedRomLaunch {
        val identity = romInfo?.let { RomIdentity(it.gameCode, it.revision, rom.retroAchievementsHash) }
        val resolution = resolve(
            identity = identity,
            currentSlot = rom.config.gbaSlotConfig,
            userCheats = userCheats,
            enhancementsEnabled = enhancementsEnabled,
            trueWidescreenRequested = trueWidescreenRequested,
            trueWidescreenProductSupported = trueWidescreenProductSupported,
            developerWidescreenDiagnostic = developerWidescreenDiagnostic,
            developerWidescreenDiagnosticSupported = developerWidescreenDiagnosticSupported,
            requestedRaMode = requestedRaMode,
            saveStateResumeEnabled = saveStateResumeEnabled,
            requestedArm9Percent = requestedArm9Percent,
            profilePreferences = profilePreferences,
        )
        val gbaSlotConfig = when {
            resolution.useSlot2Analog -> RomGbaSlotConfig.AnalogInput
            !enhancementsEnabled && rom.config.gbaSlotConfig is RomGbaSlotConfig.AnalogInput -> RomGbaSlotConfig.None
            else -> rom.config.gbaSlotConfig
        }
        return PlannedRomLaunch(
            rom = rom.copy(config = rom.config.copy(gbaSlotConfig = gbaSlotConfig)),
            cheats = RuntimeActionReplayComposer.compose(resolution.plan),
            plan = resolution.plan,
            retroAchievementsPolicy = resolution.retroAchievementsPolicy,
            requestedWidescreenMode = resolution.requestedWidescreenMode,
            effectiveWidescreenMode = resolution.effectiveWidescreenMode,
        )
    }

    fun resolve(
        identity: RomIdentity?,
        currentSlot: RomGbaSlotConfig,
        userCheats: List<Cheat>,
        enhancementsEnabled: Boolean = true,
        trueWidescreenRequested: Boolean = true,
        trueWidescreenProductSupported: Boolean = false,
        developerWidescreenDiagnostic: Boolean = false,
        developerWidescreenDiagnosticSupported: Boolean = false,
        requestedRaMode: ProfileRaMode,
        saveStateResumeEnabled: Boolean = false,
        requestedArm9Percent: Int = Arm9OverclockPolicy.DEFAULT_PERCENT,
        profilePreferences: ProfilePreferences? = null,
    ): LaunchProfileResolution {
        val preferences = profilePreferences ?: ProfilePreferences(
            requestedRaMode = requestedRaMode,
            requestedArm9Percent = requestedArm9Percent,
        )
        val requestedProfile = if (enhancementsEnabled) {
            identity?.let { catalog.exactProfiles(it).firstOrNull { profile ->
                profile.id == (preferences.selectedProfileId ?: "sm64ds.eu.thor-enhanced")
            } }?.id
        } else {
            identity?.let { catalog.exactProfiles(it).firstOrNull { profile -> profile.id == "original.sm64ds.eu" } }?.id
        }
        val capabilities = buildSet {
            add(EnhancementCapability.NDS_EMULATION)
            add(EnhancementCapability.ACTION_REPLAY)
            if (currentSlot !is RomGbaSlotConfig.GbaRom) add(EnhancementCapability.SLOT2_ANALOG)
            if (
                trueWidescreenProductSupported ||
                (developerWidescreenDiagnostic && developerWidescreenDiagnosticSupported)
            ) {
                add(EnhancementCapability.VULKAN)
                add(EnhancementCapability.VULKAN_STRUCTURED_COMPOSITOR)
                add(EnhancementCapability.THOR_DUAL_INTERNAL_DISPLAY)
            }
        }
        val requestedWidescreenMode = when {
            developerWidescreenDiagnostic -> WidescreenPresentationMode.DEVELOPER_DIAGNOSTIC
            trueWidescreenRequested -> WidescreenPresentationMode.TRUE_WIDESCREEN
            else -> WidescreenPresentationMode.NATIVE_4_3
        }
        val resolved = sessionPlanBuilder.build(
            identity = identity,
            // The ARM9 scheduler plumbing is present; hardware validation promotes this to VALIDATED.
            device = DeviceProfileContext(
                capabilities = capabilities,
                arm9OverclockCapability = Arm9OverclockCapability.EXPERIMENTAL,
            ),
            preferences = preferences.copy(
                selectedProfileId = requestedProfile,
                enabledEnhancements = preferences.enabledEnhancements + mapOf(
                    "60fps-dev-cadence" to enhancementsEnabled,
                    "true-widescreen" to (
                        enhancementsEnabled &&
                            (trueWidescreenRequested || developerWidescreenDiagnostic)
                        ),
                ),
                requestedRaMode = preferences.requestedRaMode,
                requestedArm9Percent = preferences.requestedArm9Percent,
            ),
            userCheats = userCheats,
            safeMode = !enhancementsEnabled,
        )
        val analogEnabled = resolved.enhancements.any { it.id == "analog" && it.enabled }
        val widescreenEnabled = resolved.enhancements.any { it.id == "true-widescreen" && it.enabled }
        val effectiveWidescreenMode = when {
            !widescreenEnabled -> WidescreenPresentationMode.NATIVE_4_3
            developerWidescreenDiagnostic -> WidescreenPresentationMode.DEVELOPER_DIAGNOSTIC
            else -> WidescreenPresentationMode.TRUE_WIDESCREEN
        }
        return LaunchProfileResolution(
            plan = resolved,
            useSlot2Analog = analogEnabled,
            retroAchievementsPolicy = retroAchievementsPolicyResolver.resolve(
                plan = resolved,
                arm9Percent = resolved.effectiveArm9Percent,
                saveStateResumeEnabled = saveStateResumeEnabled,
            ),
            requestedWidescreenMode = requestedWidescreenMode,
            effectiveWidescreenMode = effectiveWidescreenMode,
        )
    }
}
