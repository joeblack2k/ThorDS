package me.magnum.melonds.ui.romdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.magnum.melonds.common.Permission
import me.magnum.melonds.common.UriPermissionManager
import me.magnum.melonds.common.romprocessors.RomFileProcessorFactory
import me.magnum.melonds.domain.model.enhancement.ProfileLaunchPlanner
import me.magnum.melonds.domain.model.enhancement.RomIdentity
import me.magnum.melonds.domain.model.enhancement.SharedPreferencesProfilePreferencesRepository
import me.magnum.melonds.domain.model.enhancement.WidescreenPresentationMode
import me.magnum.melonds.domain.model.enhancement.ProfileIntegrity
import me.magnum.melonds.domain.model.enhancement.ProfileRaMode
import me.magnum.melonds.impl.enhancement.EmbeddedProfileCatalog
import me.magnum.melonds.domain.model.VideoFiltering
import me.magnum.melonds.domain.model.VideoRenderer
import me.magnum.melonds.domain.model.rom.Rom
import me.magnum.melonds.domain.model.rom.config.RomConfig
import me.magnum.melonds.domain.model.rom.config.RomInputMode
import me.magnum.melonds.domain.model.rom.config.RomGbaSlotConfig
import me.magnum.melonds.domain.repositories.RomsRepository
import me.magnum.melonds.domain.repositories.SettingsRepository
import me.magnum.melonds.impl.RomIconProvider
import me.magnum.melonds.parcelables.RomParcelable
import me.magnum.melonds.ui.romdetails.model.RomConfigUiState
import me.magnum.melonds.ui.romdetails.model.RomConfigUpdateEvent
import me.magnum.melonds.ui.romdetails.model.RomGbaSlotConfigUiModel
import me.magnum.melonds.ui.romdetails.model.ThorProfileUiModel
import me.magnum.melonds.ui.romlist.RomIcon
import javax.inject.Inject

@HiltViewModel
class RomDetailsViewModel @Inject constructor(
    private val romDetailsUiMapper: RomDetailsUiMapper,
    private val romsRepository: RomsRepository,
    private val settingsRepository: SettingsRepository,
    private val romIconProvider: RomIconProvider,
    private val uriPermissionManager: UriPermissionManager,
    private val romFileProcessorFactory: RomFileProcessorFactory,
    private val context: android.content.Context,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private data class GlobalVideoConfig(
        val renderer: me.magnum.melonds.domain.model.VideoRenderer,
        val threadedRendering: Boolean,
        val internalResolutionScaling: Int,
        val filtering: me.magnum.melonds.domain.model.VideoFiltering,
        val retroArchShaderPresetPath: String?,
        val retroArchShaderParameters: String?,
        val hasValidRetroArchShaderRoot: Boolean,
    )

    private val _rom = MutableStateFlow(savedStateHandle.get<RomParcelable>(RomDetailsActivity.KEY_ROM)!!.rom)
    val rom = _rom.asStateFlow()

    private val _romConfig = MutableStateFlow(_rom.value.config)
    private val _thorProfile = MutableStateFlow<ThorProfileUiModel?>(null)
    private var thorIdentity: RomIdentity? = null
    private val profileLaunchPlanner by lazy { ProfileLaunchPlanner(EmbeddedProfileCatalog(context).catalog) }
    private val profilePreferencesRepository by lazy { SharedPreferencesProfilePreferencesRepository(context) }

    init {
        refreshRom()
    }

    val romConfigUiState by lazy {
        val uiStateFlow = MutableStateFlow<RomConfigUiState>(RomConfigUiState.Loading)
        viewModelScope.launch {
            resolveThorProfile()
            val globalCoreVideoConfig = combine(
                settingsRepository.getVideoRenderer(),
                settingsRepository.isThreadedRenderingEnabled(),
                settingsRepository.getVideoInternalResolutionScaling(),
                settingsRepository.getVideoFiltering(),
            ) { renderer, threadedRendering, internalResolutionScaling, filtering ->
                GlobalVideoConfig(
                    renderer = renderer,
                    threadedRendering = threadedRendering,
                    internalResolutionScaling = internalResolutionScaling,
                    filtering = filtering,
                    retroArchShaderPresetPath = null,
                    retroArchShaderParameters = null,
                    hasValidRetroArchShaderRoot = false,
                )
            }
            val globalShaderConfig = combine(
                settingsRepository.observeRetroArchShaderPresetPath(),
                settingsRepository.observeRetroArchShaderParametersText(),
                settingsRepository.observeRetroArchShaderRootValid(),
            ) { presetPath, parameters, hasValidRoot ->
                Triple(presetPath, parameters, hasValidRoot)
            }
            val globalRuntimeConfig = combine(
                settingsRepository.observeDefaultConsoleType(),
                settingsRepository.observeMicSource(),
            ) { consoleType, micSource ->
                consoleType to micSource
            }
            val baseConfigUi = combine(
                _romConfig,
                globalRuntimeConfig,
                globalCoreVideoConfig,
                globalShaderConfig,
                settingsRepository.observeRetroAchievementsEnabled(),
            ) { romConfig, globalRuntimeConfig, globalVideoConfig, shaderConfig, globalRetroAchievementsEnabled ->
                romDetailsUiMapper.mapRomConfigToUi(
                    romConfig = romConfig,
                    globalRuntimeConsoleType = globalRuntimeConfig.first,
                    globalRuntimeMicSource = globalRuntimeConfig.second,
                    globalVideoRenderer = globalVideoConfig.renderer,
                    globalThreadedRendering = globalVideoConfig.threadedRendering,
                    globalInternalResolutionScaling = globalVideoConfig.internalResolutionScaling,
                    globalVideoFiltering = globalVideoConfig.filtering,
                    globalRetroArchShaderPresetPath = shaderConfig.first,
                    globalRetroArchShaderParameters = shaderConfig.second,
                    hasValidRetroArchShaderRoot = shaderConfig.third,
                    globalRetroAchievementsEnabled = globalRetroAchievementsEnabled,
                )
            }
            combine(baseConfigUi, _thorProfile) { config, thorProfile ->
                config.copy(thorProfile = thorProfile)
            }.collect {
                uiStateFlow.value = RomConfigUiState.Ready(it)
            }
        }

        uiStateFlow.asStateFlow()
    }

    fun onRomConfigUpdateEvent(event: RomConfigUpdateEvent) {
        val currentRomConfig = _romConfig.value
        val newRomConfig = when(event) {
            is RomConfigUpdateEvent.RuntimeConsoleUpdate -> currentRomConfig.copy(runtimeConsoleType = event.newRuntimeConsole)
            is RomConfigUpdateEvent.RuntimeMicSourceUpdate -> currentRomConfig.copy(runtimeMicSource = event.newRuntimeMicSource)
            is RomConfigUpdateEvent.UseHgEngineFixUpdate -> currentRomConfig.copy(useHgEngineFix = event.enabled)
            is RomConfigUpdateEvent.InputModeUpdate -> currentRomConfig.copy(
                inputMode = event.inputMode,
                customControllerConfiguration = when {
                    event.inputMode != RomInputMode.CUSTOM -> currentRomConfig.customControllerConfiguration
                    currentRomConfig.customControllerConfiguration != null -> currentRomConfig.customControllerConfiguration
                    else -> settingsRepository.getControllerConfiguration().copy()
                },
            )
            is RomConfigUpdateEvent.LayoutUpdate -> currentRomConfig.copy(layoutId = event.newLayoutId)
            is RomConfigUpdateEvent.GbaSlotTypeUpdated -> currentRomConfig.let {
                val newGbaSlotConfig = when (event.type) {
                    RomGbaSlotConfigUiModel.Type.None -> RomGbaSlotConfig.None
                    RomGbaSlotConfigUiModel.Type.GbaRom -> RomGbaSlotConfig.GbaRom(null, null)
                    RomGbaSlotConfigUiModel.Type.RumblePak -> RomGbaSlotConfig.RumblePak
                    RomGbaSlotConfigUiModel.Type.MemoryExpansion -> RomGbaSlotConfig.MemoryExpansion
                    RomGbaSlotConfigUiModel.Type.AnalogInput -> RomGbaSlotConfig.AnalogInput
                }
                it.copy(gbaSlotConfig = newGbaSlotConfig)
            }
            is RomConfigUpdateEvent.GbaRomPathUpdate -> currentRomConfig.let {
                (currentRomConfig.gbaSlotConfig as? RomGbaSlotConfig.GbaRom)?.let { gbaConfig ->
                    it.copy(gbaSlotConfig = gbaConfig.copy(romPath = event.gbaRomPath))
                }
            }
            is RomConfigUpdateEvent.GbaSavePathUpdate -> currentRomConfig.let {
                (currentRomConfig.gbaSlotConfig as? RomGbaSlotConfig.GbaRom)?.let { gbaConfig ->
                    it.copy(gbaSlotConfig = gbaConfig.copy(savePath = event.gbaSavePath))
                }
            }
            is RomConfigUpdateEvent.CustomNameUpdate -> currentRomConfig.copy(customName = event.customName)
            is RomConfigUpdateEvent.VideoRendererUpdate -> {
                val targetRenderer = event.videoRenderer
                val targetFiltering = currentRomConfig.videoFiltering
                currentRomConfig.copy(
                    videoRenderer = targetRenderer,
                    videoFiltering = targetFiltering?.takeIf {
                        targetRenderer == null || isFilteringSupported(targetRenderer, it)
                    },
                )
            }
            is RomConfigUpdateEvent.ThreadedRenderingUpdate -> currentRomConfig.copy(threadedRendering = event.threadedRendering)
            is RomConfigUpdateEvent.InternalResolutionScalingUpdate -> currentRomConfig.copy(internalResolutionScaling = event.internalResolutionScaling)
            is RomConfigUpdateEvent.VideoFilteringUpdate -> currentRomConfig.copy(videoFiltering = event.videoFiltering)
            is RomConfigUpdateEvent.RetroArchShaderPresetPathUpdate -> currentRomConfig.copy(retroArchShaderPresetPath = event.presetPath)
            is RomConfigUpdateEvent.RetroArchShaderParametersUpdate -> currentRomConfig.copy(retroArchShaderParameters = event.parameters)
            is RomConfigUpdateEvent.RetroAchievementsEnabledUpdate -> currentRomConfig.copy(retroAchievementsEnabled = event.enabled)
            is RomConfigUpdateEvent.ThorProfileModeUpdate,
            is RomConfigUpdateEvent.ThorProfileRaModeUpdate -> currentRomConfig
        }

        newRomConfig?.let { newConfig ->
            _romConfig.value = newConfig
            _rom.update { it.copy(config = newConfig) }
            saveRomConfig(newConfig)
        }
        when (event) {
            is RomConfigUpdateEvent.ThorProfileModeUpdate -> updateThorProfilePreferences {
                it.copy(
                    selectedProfileId = if (event.enhanced) "sm64ds.eu.thor-enhanced" else "original.sm64ds.eu",
                    enabledEnhancements = mapOf("true-widescreen" to event.enhanced),
                )
            }
            is RomConfigUpdateEvent.ThorProfileRaModeUpdate ->
                updateThorProfilePreferences { it.copy(requestedRaMode = event.mode) }
            else -> Unit
        }
    }

    fun refreshRom() {
        viewModelScope.launch {
            val refreshedRom = romsRepository.getRomAtUri(_rom.value.uri) ?: return@launch
            val currentConfig = _romConfig.value
            val refreshedConfig = if (isDefaultConfig(refreshedRom) && !isDefaultConfig(_rom.value.copy(config = currentConfig))) {
                currentConfig
            } else {
                refreshedRom.config
            }
            _rom.value = refreshedRom.copy(config = refreshedConfig)
            _romConfig.value = refreshedConfig
            resolveThorProfile()
        }
    }

    private suspend fun resolveThorProfile() {
        val rom = _rom.value
        val info = romFileProcessorFactory.getFileRomProcessorForDocument(rom.uri)?.getRomInfo(rom)
        val identity = info?.let { RomIdentity(it.gameCode, it.revision, rom.retroAchievementsHash) }
        thorIdentity = identity
        if (identity == null) {
            _thorProfile.value = null
            return
        }
        val preferences = profilePreferencesRepository.read(identity.stableKey())
        val resolved = profileLaunchPlanner.resolve(
            identity = identity,
            currentSlot = rom.config.gbaSlotConfig,
            userCheats = emptyList(),
            enhancementsEnabled = !settingsRepository.isThorDSSafeModeEnabled(),
            requestedRaMode = preferences.requestedRaMode,
            requestedArm9Percent = preferences.requestedArm9Percent,
            profilePreferences = preferences,
        )
        _thorProfile.value = ThorProfileUiModel(
            profileId = resolved.plan.profileId,
            match = resolved.plan.match.name,
            integrity = resolved.plan.profileIntegrity.name,
            arm9Percent = resolved.plan.effectiveArm9Percent,
            retroAchievementsMode = resolved.plan.effectiveRaMode.name,
            widescreenMode = resolved.effectiveWidescreenMode.name,
            requestedRaMode = preferences.requestedRaMode.name,
            enhancedRequested = preferences.selectedProfileId == "sm64ds.eu.thor-enhanced",
        )
    }

    private fun updateThorProfilePreferences(
        update: (me.magnum.melonds.domain.model.enhancement.ProfilePreferences) -> me.magnum.melonds.domain.model.enhancement.ProfilePreferences,
    ) {
        val identity = thorIdentity ?: return
        val key = identity.stableKey()
        profilePreferencesRepository.write(key, update(profilePreferencesRepository.read(key)))
        viewModelScope.launch { resolveThorProfile() }
    }

    suspend fun getRomIcon(rom: Rom): RomIcon {
        val romIconBitmap = romIconProvider.getRomIcon(rom)
        val iconFiltering = settingsRepository.getRomIconFiltering()
        return RomIcon(romIconBitmap, iconFiltering)
    }

    private fun saveRomConfig(newConfig: RomConfig) {
        if (newConfig.gbaSlotConfig is RomGbaSlotConfig.GbaRom) {
            newConfig.gbaSlotConfig.romPath?.let { uriPermissionManager.persistFilePermissions(it, Permission.READ) }
            newConfig.gbaSlotConfig.savePath?.let { uriPermissionManager.persistFilePermissions(it, Permission.READ_WRITE) }
        }
        romsRepository.updateRomConfig(_rom.value, newConfig)
    }

    private fun isFilteringSupported(renderer: VideoRenderer, filtering: VideoFiltering): Boolean {
        return when (renderer) {
            VideoRenderer.VULKAN -> filtering.isSupportedByVulkan()
            else -> filtering.isSupportedByOpenGlSurface()
        }
    }

    private fun isDefaultConfig(rom: Rom): Boolean {
        val defaultConfig = if (rom.isDsiWareTitle) {
            RomConfig.forDsiWareTitle()
        } else {
            RomConfig.default()
        }
        return rom.config == defaultConfig
    }
}
