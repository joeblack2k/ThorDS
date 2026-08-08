package me.magnum.melonds.common

import me.magnum.melonds.domain.model.VideoRenderer

object ThorDeviceCapabilities {
    fun isThor(manufacturer: String, model: String): Boolean {
        return manufacturer.equals("AYN", ignoreCase = true) && model.equals("AYN Thor", ignoreCase = true)
    }

    fun usesFixedLandscape(manufacturer: String, model: String): Boolean = isThor(manufacturer, model)

    fun supportsTrueWidescreen(
        manufacturer: String,
        model: String,
        renderer: VideoRenderer,
    ): Boolean {
        return isThor(manufacturer, model) && renderer == VideoRenderer.VULKAN
    }
}
