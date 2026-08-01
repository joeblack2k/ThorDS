package me.magnum.melonds.domain.model.enhancement

import me.magnum.melonds.domain.model.Cheat

data class DeviceProfileContext(
    val capabilities: Set<EnhancementCapability>,
    val arm9OverclockCapability: Arm9OverclockCapability = Arm9OverclockCapability.PLUMBING_ONLY,
)

object CapabilityProbe {
    fun from(
        hasSlot2Analog: Boolean,
        hasVulkan: Boolean,
        hasStructuredVulkanCompositor: Boolean,
        isThorDualDisplay: Boolean,
        hasArm9OverclockSupport: Boolean,
        hasRetroAchievements: Boolean,
    ): DeviceProfileContext {
        return DeviceProfileContext(
            capabilities = buildSet {
                if (hasSlot2Analog) add(EnhancementCapability.SLOT2_ANALOG)
                if (hasVulkan) add(EnhancementCapability.VULKAN)
                if (hasStructuredVulkanCompositor) add(EnhancementCapability.VULKAN_STRUCTURED_COMPOSITOR)
                if (isThorDualDisplay) add(EnhancementCapability.THOR_DUAL_INTERNAL_DISPLAY)
                if (hasRetroAchievements) add(EnhancementCapability.RA_INTEGRATION)
            },
            arm9OverclockCapability = if (hasArm9OverclockSupport) {
                Arm9OverclockCapability.PLUMBING_ONLY
            } else {
                Arm9OverclockCapability.UNSUPPORTED
            },
        )
    }
}

class ProfileResolver(private val catalog: ProfileCatalog) {
    fun resolve(
        identity: RomIdentity?,
        device: DeviceProfileContext,
        preferences: ProfilePreferences,
        userCheats: List<Cheat>,
        safeMode: Boolean = false,
    ): ResolvedSessionPlan {
        val arm9Overclock = Arm9OverclockPolicy.resolve(
            requestedPercent = preferences.requestedArm9Percent,
            capability = device.arm9OverclockCapability,
            safeMode = safeMode,
            requestedRaMode = preferences.requestedRaMode,
        )
        val (matched, match) = identity?.let(catalog::match) ?: (null to ProfileMatch.NO_MATCH)
        val selected = preferences.selectedProfileId?.let(catalog::find)
        val profile = when {
            selected?.fallback == true -> selected
            selected != null && identity != null && catalog.matchesExactly(selected, identity) -> selected
            else -> catalog.originalProfile
        }
        val effectiveMatch = if (profile.fallback) match else ProfileMatch.MATCH_EXACT
        val definitions = profile.enhancements.associateBy { it.id }
        val initiallyResolved = definitions.keys.sorted().associateWith { id ->
            resolveEnhancement(id, definitions, device.capabilities, preferences, mutableSetOf())
        }
        val resolved = resolveConflicts(
            initial = initiallyResolved,
            definitions = definitions,
            requestedRaMode = preferences.requestedRaMode,
        )
        val enabled = resolved.values.filter { it.enabled }
        val effectiveRaMode = when {
            preferences.requestedRaMode !in profile.allowedRaModes -> ProfileRaMode.OFF
            else -> preferences.requestedRaMode
        }
        return ResolvedSessionPlan(
            profileId = profile.id,
            profileVersion = profile.profileVersion,
            match = effectiveMatch,
            profileIntegrity = profile.integrity,
            requestedRaMode = preferences.requestedRaMode,
            requestedArm9Percent = arm9Overclock.requestedPercent,
            effectiveArm9Percent = arm9Overclock.effectivePercent,
            arm9OverclockCapability = arm9Overclock.capability,
            curatedRuntimeCodes = enabled.mapNotNull { definitions.getValue(it.id).runtimeCode }.sortedBy { it.id },
            userCheats = userCheats.toList(),
            enhancements = resolved.values.sortedBy { it.id },
            effectiveRaMode = effectiveRaMode,
        )
    }

    private fun resolveEnhancement(
        id: String,
        definitions: Map<String, EnhancementDefinition>,
        capabilities: Set<EnhancementCapability>,
        preferences: ProfilePreferences,
        resolving: MutableSet<String>,
    ): ResolvedEnhancement {
        val definition = definitions.getValue(id)
        if (!(preferences.enabledEnhancements[id] ?: definition.defaultEnabled)) return ResolvedEnhancement(id, false, "disabled_by_user")
        if (!resolving.add(id)) return ResolvedEnhancement(id, false, "dependency_cycle")
        val missing = definition.requiredCapabilities - capabilities
        val dependency = definition.requires.sorted().map { resolveEnhancement(it, definitions, capabilities, preferences, resolving) }.firstOrNull { !it.enabled }
        resolving -= id
        return when {
            missing.isNotEmpty() -> ResolvedEnhancement(id, false, "missing_capability:${missing.sorted().joinToString(",")}")
            dependency != null -> ResolvedEnhancement(id, false, "dependency_disabled:${dependency.id}")
            else -> ResolvedEnhancement(id, true, requiresRelaunch = definition.requiresRelaunch)
        }
    }

    private fun resolveConflicts(
        initial: Map<String, ResolvedEnhancement>,
        definitions: Map<String, EnhancementDefinition>,
        requestedRaMode: ProfileRaMode,
    ): Map<String, ResolvedEnhancement> {
        val resolved = initial.toMutableMap()
        var changed: Boolean
        do {
            val enabled = resolved.filterValues { it.enabled }.keys
            val disabled = enabled.sorted().mapNotNull { id ->
                val conflict = if (requestedRaMode == ProfileRaMode.HARDCORE && "RA_HARDCORE" in definitions.getValue(id).conflicts) {
                    "RA_HARDCORE"
                } else enabled.sorted().firstOrNull { other ->
                    other != id && (other in definitions.getValue(id).conflicts || id in definitions.getValue(other).conflicts)
                }
                val dependency = definitions.getValue(id).requires.sorted().firstOrNull { it !in enabled }
                when {
                    conflict != null -> id to "conflict:$conflict"
                    dependency != null -> id to "dependency_disabled:$dependency"
                    else -> null
                }
            }
            disabled.forEach { (id, reason) -> resolved[id] = resolved.getValue(id).copy(enabled = false, reason = reason) }
            changed = disabled.isNotEmpty()
        } while (changed)
        return resolved
    }
}

class SessionPlanBuilder(catalog: ProfileCatalog) {
    private val resolver = ProfileResolver(catalog)

    fun build(
        identity: RomIdentity?,
        device: DeviceProfileContext,
        preferences: ProfilePreferences,
        userCheats: List<Cheat>,
        safeMode: Boolean = false,
    ): ResolvedSessionPlan = resolver.resolve(identity, device, preferences, userCheats, safeMode)
}
