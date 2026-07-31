package me.magnum.melonds.ui.emulator.model

import androidx.annotation.Keep
import me.magnum.melonds.domain.model.Rect
import me.magnum.melonds.domain.model.VideoFiltering
import me.magnum.melonds.domain.model.layout.BackgroundMode

@Keep
data class VulkanPresentationConfig(
    val topScreenRect: Rect?,
    val bottomScreenRect: Rect?,
    val topAlpha: Float,
    val bottomAlpha: Float,
    val topOnTop: Boolean,
    val bottomOnTop: Boolean,
    val hybridTopScreenRect: Rect?,
    val hybridBottomScreenRect: Rect?,
    val hybridAlpha: Float,
    val hybridOnTop: Boolean,
    val backgroundMode: BackgroundMode,
    val videoFiltering: VideoFiltering,
    val retroShaderEnabled: Boolean,
    val retroShaderPresetPath: String?,
    val retroShaderSourceResolution: String,
    val retroShaderPassCount: Int,
    val retroShaderParameterOverrides: Map<String, Float>,
    val retroShaderClearHistory: Boolean,
    val developerWidescreenProbe: Boolean = false,
)
