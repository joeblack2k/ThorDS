package me.magnum.melonds.domain.model.enhancement

import android.content.Context
import kotlinx.serialization.json.Json

interface ProfilePreferencesRepository {
    fun read(romKey: String): ProfilePreferences
    fun write(romKey: String, preferences: ProfilePreferences)
}

class SharedPreferencesProfilePreferencesRepository(context: Context) : ProfilePreferencesRepository {
    private val preferences = context.getSharedPreferences("thords_profile_preferences", Context.MODE_PRIVATE)

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
