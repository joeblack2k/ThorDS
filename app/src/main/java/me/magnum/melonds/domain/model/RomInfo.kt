package me.magnum.melonds.domain.model

/**
 * @param gameTitle The short ROM title as present in the ROM header
 * @param gameName The ROM name as displayed in the system
 */
data class RomInfo(
    val gameCode: String,
    val headerChecksum: UInt,
    val gameTitle: String,
    val gameName: String,
    val revision: Int = 0,
) {
    fun headerChecksumString(): String {
        return headerChecksum.toString(16).padStart(8, '0').uppercase()
    }
}
