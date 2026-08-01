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
)

data class LaunchProfileResolution(
    val plan: ResolvedSessionPlan,
    val useSlot2Analog: Boolean,
    val retroAchievementsPolicy: RetroAchievementsPolicy,
)

class ProfileLaunchPlanner(private val catalog: ProfileCatalog) {
    private val sessionPlanBuilder = SessionPlanBuilder(catalog)
    private val retroAchievementsPolicyResolver = RetroAchievementsPolicyResolver()

    fun plan(
        rom: Rom,
        romInfo: RomInfo?,
        userCheats: List<Cheat>,
        enhancementsEnabled: Boolean,
        developerWidescreenProbe: Boolean = false,
        requestedRaMode: ProfileRaMode,
        saveStateResumeEnabled: Boolean = false,
    ): PlannedRomLaunch {
        val identity = romInfo?.let { RomIdentity(it.gameCode, it.revision, rom.retroAchievementsHash) }
        val resolution = resolve(
            identity = identity,
            currentSlot = rom.config.gbaSlotConfig,
            userCheats = userCheats,
            enhancementsEnabled = enhancementsEnabled,
            developerWidescreenProbe = developerWidescreenProbe,
            requestedRaMode = requestedRaMode,
            saveStateResumeEnabled = saveStateResumeEnabled,
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
        )
    }

    fun resolve(
        identity: RomIdentity?,
        currentSlot: RomGbaSlotConfig,
        userCheats: List<Cheat>,
        enhancementsEnabled: Boolean = true,
        developerWidescreenProbe: Boolean = false,
        requestedRaMode: ProfileRaMode,
        saveStateResumeEnabled: Boolean = false,
    ): LaunchProfileResolution {
        val requestedProfile = if (enhancementsEnabled) {
            identity?.let { catalog.exactProfiles(it).firstOrNull { profile -> profile.id == "sm64ds.eu.thor-enhanced" } }?.id
        } else {
            identity?.let { catalog.exactProfiles(it).firstOrNull { profile -> profile.id == "original.sm64ds.eu" } }?.id
        }
        val capabilities = buildSet {
            add(EnhancementCapability.NDS_EMULATION)
            add(EnhancementCapability.ACTION_REPLAY)
            if (currentSlot !is RomGbaSlotConfig.GbaRom) add(EnhancementCapability.SLOT2_ANALOG)
            if (developerWidescreenProbe) {
                add(EnhancementCapability.VULKAN)
                add(EnhancementCapability.VULKAN_STRUCTURED_COMPOSITOR)
            }
        }
        val resolved = sessionPlanBuilder.build(
            identity = identity,
            device = DeviceProfileContext(capabilities),
            preferences = ProfilePreferences(
                selectedProfileId = requestedProfile,
                enabledEnhancements = if (developerWidescreenProbe) mapOf("true-widescreen" to true) else emptyMap(),
                requestedRaMode = requestedRaMode,
            ),
            userCheats = userCheats,
        )
        val analogEnabled = resolved.enhancements.any { it.id == "analog" && it.enabled }
        return LaunchProfileResolution(
            plan = resolved,
            useSlot2Analog = analogEnabled,
            retroAchievementsPolicy = retroAchievementsPolicyResolver.resolve(
                plan = resolved,
                saveStateResumeEnabled = saveStateResumeEnabled,
            ),
        )
    }
}
