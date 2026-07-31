package me.magnum.melonds.common

object ThorDeviceCapabilities {
    fun isThor(manufacturer: String, model: String): Boolean {
        return manufacturer.equals("AYN", ignoreCase = true) && model.equals("AYN Thor", ignoreCase = true)
    }
}
