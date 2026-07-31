package me.magnum.melonds.domain.model.enhancement

import kotlinx.serialization.Serializable
import me.magnum.melonds.domain.model.Cheat
import java.security.MessageDigest

@Serializable
data class EnhancementCatalogDocument(
    val catalogVersion: Int,
    val profiles: List<EnhancementProfile>,
)

@Serializable
data class EnhancementProfile(
    val schemaVersion: Int,
    val id: String,
    val profileVersion: Int,
    val displayName: String,
    val fallback: Boolean = false,
    val game: ProfileGameIdentity? = null,
    val enhancements: List<EnhancementDefinition> = emptyList(),
    val allowedRaModes: Set<ProfileRaMode> = setOf(ProfileRaMode.OFF, ProfileRaMode.CASUAL, ProfileRaMode.HARDCORE),
)

@Serializable
data class ProfileGameIdentity(
    val system: String,
    val gameCode: String,
    val revision: Int,
    val raHashes: Set<String>,
)

@Serializable
data class EnhancementDefinition(
    val id: String,
    val displayName: String,
    val defaultEnabled: Boolean,
    val kind: EnhancementKind,
    val requiredCapabilities: Set<EnhancementCapability> = emptySet(),
    val requires: Set<String> = emptySet(),
    val conflicts: Set<String> = emptySet(),
    val requiresRelaunch: Boolean = false,
    val experimental: Boolean = false,
    val provenance: String = "",
    val runtimeCode: RuntimeActionReplayCode? = null,
)

@Serializable
enum class EnhancementKind {
    ORIGINAL,
    ACTION_REPLAY,
    EMULATOR_FEATURE,
    COMPOSITE,
}

@Serializable
enum class EnhancementCapability {
    SLOT2_ANALOG,
    VULKAN,
    VULKAN_STRUCTURED_COMPOSITOR,
    THOR_DUAL_INTERNAL_DISPLAY,
    ARM9_OC_CORE_SUPPORT,
    RA_INTEGRATION,
}

@Serializable
enum class ProfileRaMode {
    OFF,
    CASUAL,
    HARDCORE,
}

@Serializable
data class RuntimeActionReplayCode(
    val id: String,
    val codeWords: List<String>,
    val codeSha256: String,
    val expectedOriginalWords: List<String> = emptyList(),
)

data class RomIdentity(
    val gameCode: String,
    val revision: Int,
    val retroAchievementsHash: String,
) {
    fun stableKey(): String = "${gameCode.lowercase()}:$revision:${retroAchievementsHash.lowercase()}"
}

@Serializable
data class ProfilePreferences(
    val selectedProfileId: String? = null,
    val enabledEnhancements: Map<String, Boolean> = emptyMap(),
    val requestedRaMode: ProfileRaMode = ProfileRaMode.CASUAL,
)

enum class ProfileMatch {
    MATCH_EXACT,
    MATCH_GAME_UNSUPPORTED_REVISION,
    MATCH_GAME_UNKNOWN_HASH,
    NO_MATCH,
}

data class ResolvedEnhancement(
    val id: String,
    val enabled: Boolean,
    val reason: String? = null,
    val requiresRelaunch: Boolean = false,
)

data class ResolvedSessionPlan(
    val profileId: String,
    val profileVersion: Int,
    val match: ProfileMatch,
    val curatedRuntimeCodes: List<RuntimeActionReplayCode>,
    val userCheats: List<Cheat>,
    val enhancements: List<ResolvedEnhancement>,
    val effectiveRaMode: ProfileRaMode,
) {
    val planHash: String by lazy {
        val canonical = buildString {
            append(profileId).append('|').append(profileVersion).append('|').append(match).append('|').append(effectiveRaMode)
            enhancements.sortedBy { it.id }.forEach {
                append('|').append(it.id).append(':').append(it.enabled).append(':').append(it.reason.orEmpty())
            }
            curatedRuntimeCodes.sortedBy { it.id }.forEach { append('|').append(it.id).append(':').append(it.codeSha256) }
            userCheats.sortedBy { it.id ?: Long.MIN_VALUE }.forEach { append('|').append(it.id).append(':').append(it.enabled) }
        }
        MessageDigest.getInstance("SHA-256").digest(canonical.toByteArray()).joinToString("") { "%02x".format(it) }
    }

    fun diagnostics(): List<String> = buildList {
        add("profile=$profileId@$profileVersion")
        add("match=$match")
        add("ra=$effectiveRaMode")
        enhancements.forEach { add("enhancement=${it.id}:${if (it.enabled) "enabled" else "disabled:${it.reason}"}") }
        curatedRuntimeCodes.forEach { add("curated_code=${it.id}:${it.codeSha256}") }
        add("user_cheats=${userCheats.size}")
        add("plan_sha256=$planHash")
    }
}
