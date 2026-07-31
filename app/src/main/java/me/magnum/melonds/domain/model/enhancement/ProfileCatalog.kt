package me.magnum.melonds.domain.model.enhancement

import kotlinx.serialization.json.Json

class ProfileCatalog private constructor(
    val catalogVersion: Int,
    val profiles: List<EnhancementProfile>,
) {
    val originalProfile: EnhancementProfile = profiles.single { it.fallback }

    fun find(id: String): EnhancementProfile? = profiles.firstOrNull { it.id == id }

    fun match(identity: RomIdentity): Pair<EnhancementProfile?, ProfileMatch> {
        val codeMatches = profiles.filter { !it.fallback && it.game?.gameCode == identity.gameCode }
        if (codeMatches.isEmpty()) return null to ProfileMatch.NO_MATCH
        val revisionMatches = codeMatches.filter { it.game?.revision == identity.revision }
        if (revisionMatches.isEmpty()) return null to ProfileMatch.MATCH_GAME_UNSUPPORTED_REVISION
        val exact = revisionMatches.firstOrNull { identity.retroAchievementsHash in it.game!!.raHashes }
        return if (exact != null) exact to ProfileMatch.MATCH_EXACT else null to ProfileMatch.MATCH_GAME_UNKNOWN_HASH
    }

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun parse(serialized: String): ProfileCatalog {
            val document = json.decodeFromString<EnhancementCatalogDocument>(serialized)
            return from(document)
        }

        fun from(document: EnhancementCatalogDocument): ProfileCatalog {
            require(document.catalogVersion > 0) { "Catalog version must be positive" }
            require(document.profiles.map { it.id }.distinct().size == document.profiles.size) { "Duplicate profile id" }
            require(document.profiles.count { it.fallback } == 1) { "Catalog needs exactly one fallback profile" }
            document.profiles.forEach(::validate)
            return ProfileCatalog(document.catalogVersion, document.profiles.sortedBy { it.id })
        }

        private fun validate(profile: EnhancementProfile) {
            require(profile.schemaVersion == 1) { "Unsupported schema version for ${profile.id}" }
            require(profile.id.isNotBlank() && profile.profileVersion > 0) { "Invalid profile identity" }
            if (profile.fallback) {
                require(profile.game == null && profile.enhancements.isEmpty()) { "Fallback profile must not patch games" }
                return
            }
            val game = requireNotNull(profile.game) { "Profile ${profile.id} needs an exact game identity" }
            require(game.system == "NDS" && game.gameCode.length == 4 && game.revision >= 0) { "Invalid game identity for ${profile.id}" }
            require(game.raHashes.isNotEmpty() && game.raHashes.all { it.matches(Regex("[0-9a-fA-F]{32}")) }) { "Invalid RA hash for ${profile.id}" }
            require(profile.enhancements.map { it.id }.distinct().size == profile.enhancements.size) { "Duplicate enhancement id in ${profile.id}" }
            val known = profile.enhancements.map { it.id }.toSet()
            profile.enhancements.forEach { enhancement ->
                require(enhancement.id.isNotBlank() && enhancement.provenance.isNotBlank()) { "Missing enhancement provenance" }
                require(enhancement.requires.all { it in known }) { "Unknown dependency in ${enhancement.id}" }
                enhancement.runtimeCode?.let(::validateRuntimeCode)
            }
            require(!hasCycle(profile.enhancements.associate { it.id to it.requires })) { "Dependency cycle in ${profile.id}" }
        }

        private fun validateRuntimeCode(code: RuntimeActionReplayCode) {
            require(code.id.isNotBlank() && code.codeSha256.matches(Regex("[0-9a-fA-F]{64}"))) { "Invalid Action Replay checksum" }
            require(code.codeWords.isNotEmpty() && code.codeWords.all { it.matches(Regex("[0-9a-fA-F]{8}\\s+[0-9a-fA-F]{8}")) }) {
                "Malformed Action Replay words"
            }
            require(code.expectedOriginalWords.all { it.matches(Regex("[0-9a-fA-F]{8}\\s+[0-9a-fA-F]{8}")) }) {
                "Malformed Action Replay checks"
            }
        }

        private fun hasCycle(graph: Map<String, Set<String>>): Boolean {
            val visiting = mutableSetOf<String>()
            val visited = mutableSetOf<String>()
            fun visit(id: String): Boolean {
                if (id in visiting) return true
                if (!visited.add(id)) return false
                visiting += id
                val cycle = graph[id].orEmpty().any(::visit)
                visiting -= id
                return cycle
            }
            return graph.keys.any(::visit)
        }
    }
}
