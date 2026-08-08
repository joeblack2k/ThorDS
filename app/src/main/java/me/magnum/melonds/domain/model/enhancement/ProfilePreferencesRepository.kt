package me.magnum.melonds.domain.model.enhancement

import android.content.Context
import kotlinx.serialization.json.Json

val sm64dsExactIdentity = RomIdentity("ASMP", 0, "ba3c4052e00c5cc31df5d5534c39de1b")

fun defaultSm64dsEnhancedProfilePreferences(
    requestedRaMode: ProfileRaMode = ProfileRaMode.CASUAL,
): ProfilePreferences {
    return ProfilePreferences(
        selectedProfileId = "sm64ds.eu.thor-enhanced",
        enabledEnhancements = mapOf(
            "true-widescreen" to true,
            "60fps-dev-cadence" to true,
            "z-player-pose-interpolation" to false,
            "analog" to true,
            "right-stick-camera" to true,
        ),
        requestedRaMode = requestedRaMode,
        requestedArm9Percent = 150,
    )
}

fun ProfilePreferences.withSm64dsFrameRate(enabled: Boolean): ProfilePreferences {
    return copy(
        enabledEnhancements = enabledEnhancements + mapOf(
            "60fps-dev-cadence" to enabled,
            "z-player-pose-interpolation" to false,
        ),
    )
}

fun ProfilePreferences.withSm64dsLaunchSafety(identity: RomIdentity?): ProfilePreferences {
    if (identity != sm64dsExactIdentity) return this
    return copy(
        enabledEnhancements = enabledEnhancements + ("z-player-pose-interpolation" to false),
    )
}

interface ProfilePreferencesRepository {
    fun contains(romKey: String): Boolean
    fun read(romKey: String): ProfilePreferences
    fun write(romKey: String, preferences: ProfilePreferences)
}

class SharedPreferencesProfilePreferencesRepository(context: Context) : ProfilePreferencesRepository {
    private val preferences = context.getSharedPreferences("thords_profile_preferences", Context.MODE_PRIVATE)

    override fun contains(romKey: String): Boolean = preferences.contains(romKey)

    override fun read(romKey: String): ProfilePreferences {
        return ProfilePreferencesCodec.decode(preferences.getString(romKey, null))
    }

    override fun write(romKey: String, preferences: ProfilePreferences) {
        this.preferences.edit().putString(romKey, ProfilePreferencesCodec.encode(preferences)).apply()
    }
}

object ProfilePreferencesCodec {
    private val json = Json { ignoreUnknownKeys = true }

    fun decode(value: String?): ProfilePreferences = runCatching {
        if (value == null) ProfilePreferences() else json.decodeFromString<ProfilePreferences>(value)
    }.getOrDefault(ProfilePreferences())

    fun encode(value: ProfilePreferences): String = json.encodeToString(value)
}
