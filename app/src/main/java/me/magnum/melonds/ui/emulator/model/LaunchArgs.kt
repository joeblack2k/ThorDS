package me.magnum.melonds.ui.emulator.model

import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import me.magnum.melonds.domain.model.ConsoleType
import me.magnum.melonds.domain.model.rom.Rom
import me.magnum.melonds.extensions.parcelable
import me.magnum.melonds.parcelables.RomParcelable
import me.magnum.melonds.ui.emulator.EmulatorActivity

sealed class LaunchArgs {
    enum class RomLaunchMode { ORIGINAL, ENHANCED }

    data class RomObject(val rom: Rom, val mode: RomLaunchMode = RomLaunchMode.ORIGINAL) : LaunchArgs()
    data class RomUri(val uri: Uri, val mode: RomLaunchMode = RomLaunchMode.ORIGINAL) : LaunchArgs()
    data class RomPath(val path: String, val mode: RomLaunchMode = RomLaunchMode.ORIGINAL) : LaunchArgs()
    data class Firmware(val consoleType: ConsoleType) : LaunchArgs()

    companion object {
        fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): LaunchArgs? {
            return if (savedStateHandle.get<Boolean>(EmulatorActivity.KEY_BOOT_FIRMWARE_ONLY) == true) {
                val consoleTypeParameter = savedStateHandle.get<Int>(EmulatorActivity.KEY_BOOT_FIRMWARE_CONSOLE)
                if (consoleTypeParameter != null) {
                    val firmwareConsoleType = ConsoleType.entries[consoleTypeParameter]
                    Firmware(firmwareConsoleType)
                } else {
                    null
                }
            } else {
                val romParcelable = savedStateHandle.get<RomParcelable>(EmulatorActivity.KEY_ROM)
                val mode = savedStateHandle.get<String>(EmulatorActivity.KEY_ROM_LAUNCH_MODE)
                    ?.let { runCatching { RomLaunchMode.valueOf(it) }.getOrNull() }
                    ?: RomLaunchMode.ORIGINAL
                if (romParcelable != null) {
                    RomObject(romParcelable.rom, mode)
                } else {
                    val uri = when (val uriEntry = savedStateHandle.get<Any>(EmulatorActivity.KEY_URI)) {
                        is String -> uriEntry.toUri()
                        is Uri -> uriEntry
                        else -> null
                    }

                    if (uri != null) {
                        RomUri(uri, mode)
                    } else {
                        val path = savedStateHandle.get<String>(EmulatorActivity.KEY_PATH)
                        if (path != null) {
                            RomPath(path, mode)
                        } else {
                            null
                        }
                    }
                }
            }
        }

        fun fromIntent(intent: Intent): LaunchArgs? {
            val extras = intent.extras
            val bootFirmwareOnly = extras?.getBoolean(EmulatorActivity.KEY_BOOT_FIRMWARE_ONLY) ?: false

            return if (bootFirmwareOnly) {
                val consoleTypeParameter = extras.getInt(EmulatorActivity.KEY_BOOT_FIRMWARE_CONSOLE, -1)
                if (consoleTypeParameter == -1) {
                    null
                } else {
                    val firmwareConsoleType = ConsoleType.entries[consoleTypeParameter]
                    Firmware(firmwareConsoleType)
                }
            } else {
                val romParcelable = extras?.parcelable<RomParcelable>(EmulatorActivity.KEY_ROM)
                val mode = extras?.getString(EmulatorActivity.KEY_ROM_LAUNCH_MODE)
                    ?.let { runCatching { RomLaunchMode.valueOf(it) }.getOrNull() }
                    ?: RomLaunchMode.ORIGINAL

                when {
                    romParcelable?.rom != null -> RomObject(romParcelable.rom, mode)
                    intent.data != null -> RomUri(intent.data!!, mode)
                    extras?.containsKey(EmulatorActivity.KEY_PATH) == true -> {
                        val romPath = extras.getString(EmulatorActivity.KEY_PATH)!!
                        RomPath(romPath, mode)
                    }
                    extras?.containsKey(EmulatorActivity.KEY_URI) == true -> {
                        @Suppress("DEPRECATION")
                        val uri = when (val uriEntry = extras.get(EmulatorActivity.KEY_URI)) {
                            is String -> uriEntry.toUri()
                            is Uri -> uriEntry
                            else -> null
                        }

                        uri?.let { RomUri(it, mode) }
                    }
                    else -> null
                }
            }
        }
    }
}
