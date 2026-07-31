package me.magnum.melonds.impl

import me.magnum.melonds.domain.model.RendererConfiguration
import me.magnum.melonds.domain.model.RetroArchShaderConfiguration
import me.magnum.melonds.domain.model.VideoFiltering
import me.magnum.melonds.domain.model.VideoRenderer
import me.magnum.melonds.domain.model.layout.LayoutConfiguration
import java.util.UUID

object ThorDSSafeModePolicy {
    fun renderer(safeMode: Boolean, requested: VideoRenderer): VideoRenderer {
        return if (safeMode) VideoRenderer.SOFTWARE else requested
    }

    fun filtering(safeMode: Boolean, requested: VideoFiltering): VideoFiltering {
        return if (safeMode) VideoFiltering.NONE else requested
    }

    fun cheatsEnabled(safeMode: Boolean, userEnabled: Boolean): Boolean {
        return !safeMode && userEnabled
    }

    fun layoutId(safeMode: Boolean, selectedLayoutId: UUID): UUID {
        return if (safeMode) LayoutConfiguration.DEFAULT_ID else selectedLayoutId
    }

    fun rendererConfiguration(
        configuration: RendererConfiguration,
        emptyRetroArchShaderConfiguration: RetroArchShaderConfiguration,
    ): RendererConfiguration {
        return configuration.copy(
            renderer = VideoRenderer.SOFTWARE,
            videoFiltering = VideoFiltering.NONE,
            threadedRendering = false,
            rendererDebugToolsEnabled = false,
            rendererDebugBgObjEnabled = false,
            rendererDebugLatchTraceEnabled = false,
            conservativeCoverageEnabled = false,
            debug3dClearMagenta = false,
            retroArchShader = emptyRetroArchShaderConfiguration,
        )
    }
}
