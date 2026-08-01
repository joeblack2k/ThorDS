package me.magnum.melonds.ui.romdetails.model

import android.net.Uri
import me.magnum.melonds.domain.model.VideoFiltering
import me.magnum.melonds.domain.model.VideoRenderer
import me.magnum.melonds.domain.model.rom.config.RomInputMode
import me.magnum.melonds.domain.model.rom.config.RuntimeConsoleType
import me.magnum.melonds.domain.model.rom.config.RuntimeMicSource
import me.magnum.melonds.domain.model.enhancement.ProfileRaMode
import java.util.UUID

sealed class RomConfigUpdateEvent {
    data class RuntimeConsoleUpdate(val newRuntimeConsole: RuntimeConsoleType) : RomConfigUpdateEvent()
    data class RuntimeMicSourceUpdate(val newRuntimeMicSource: RuntimeMicSource) : RomConfigUpdateEvent()
    data class UseHgEngineFixUpdate(val enabled: Boolean) : RomConfigUpdateEvent()
    data class InputModeUpdate(val inputMode: RomInputMode) : RomConfigUpdateEvent()
    data class LayoutUpdate(val newLayoutId: UUID?) : RomConfigUpdateEvent()
    data class GbaSlotTypeUpdated(val type: RomGbaSlotConfigUiModel.Type) : RomConfigUpdateEvent()
    data class GbaRomPathUpdate(val gbaRomPath: Uri?) : RomConfigUpdateEvent()
    data class GbaSavePathUpdate(val gbaSavePath: Uri?) : RomConfigUpdateEvent()
    data class CustomNameUpdate(val customName: String?) : RomConfigUpdateEvent()
    data class VideoRendererUpdate(val videoRenderer: VideoRenderer?) : RomConfigUpdateEvent()
    data class ThreadedRenderingUpdate(val threadedRendering: Boolean?) : RomConfigUpdateEvent()
    data class InternalResolutionScalingUpdate(val internalResolutionScaling: Int?) : RomConfigUpdateEvent()
    data class VideoFilteringUpdate(val videoFiltering: VideoFiltering?) : RomConfigUpdateEvent()
    data class RetroArchShaderPresetPathUpdate(val presetPath: String?) : RomConfigUpdateEvent()
    data class RetroArchShaderParametersUpdate(val parameters: String?) : RomConfigUpdateEvent()
    data class RetroAchievementsEnabledUpdate(val enabled: Boolean?) : RomConfigUpdateEvent()
    data class ThorProfileModeUpdate(val enhanced: Boolean) : RomConfigUpdateEvent()
    data class ThorProfileRaModeUpdate(val mode: ProfileRaMode) : RomConfigUpdateEvent()
}
