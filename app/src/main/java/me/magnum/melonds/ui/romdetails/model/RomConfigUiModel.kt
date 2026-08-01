package me.magnum.melonds.ui.romdetails.model

import me.magnum.melonds.domain.model.ConsoleType
import me.magnum.melonds.domain.model.MicSource
import me.magnum.melonds.domain.model.VideoFiltering
import me.magnum.melonds.domain.model.VideoRenderer
import me.magnum.melonds.domain.model.rom.config.RomInputMode
import me.magnum.melonds.domain.model.rom.config.RuntimeConsoleType
import me.magnum.melonds.domain.model.rom.config.RuntimeMicSource
import java.util.UUID

data class RomConfigUiModel(
    val thorProfile: ThorProfileUiModel? = null,
    val runtimeConsoleType: RuntimeConsoleType = RuntimeConsoleType.DEFAULT,
    val globalRuntimeConsoleType: ConsoleType = ConsoleType.DS,
    val runtimeMicSource: RuntimeMicSource = RuntimeMicSource.DEFAULT,
    val globalRuntimeMicSource: MicSource = MicSource.BLOW,
    val layoutId: UUID? = null,
    val layoutName: String? = null,
    val globalLayoutName: String? = null,
    val gbaSlotConfig: RomGbaSlotConfigUiModel = RomGbaSlotConfigUiModel(),
    val customName: String? = null,
    val useHgEngineFix: Boolean = false,
    val inputMode: RomInputMode = RomInputMode.GLOBAL,
    val videoRenderer: VideoRenderer? = null,
    val globalVideoRenderer: VideoRenderer = VideoRenderer.SOFTWARE,
    val threadedRendering: Boolean? = null,
    val globalThreadedRendering: Boolean = true,
    val internalResolutionScaling: Int? = null,
    val globalInternalResolutionScaling: Int = 1,
    val videoFiltering: VideoFiltering? = null,
    val globalVideoFiltering: VideoFiltering = VideoFiltering.NONE,
    val retroArchShaderPresetPath: String? = null,
    val globalRetroArchShaderPresetPath: String? = null,
    val retroArchShaderParameters: String? = null,
    val globalRetroArchShaderParameters: String? = null,
    val hasValidRetroArchShaderRoot: Boolean = false,
    val retroAchievementsEnabled: Boolean? = null,
    val globalRetroAchievementsEnabled: Boolean = true,
)

data class ThorProfileUiModel(
    val profileId: String,
    val match: String,
    val integrity: String,
    val arm9Percent: Int,
    val retroAchievementsMode: String,
    val widescreenMode: String,
    val requestedRaMode: String,
    val enhancedRequested: Boolean,
)
