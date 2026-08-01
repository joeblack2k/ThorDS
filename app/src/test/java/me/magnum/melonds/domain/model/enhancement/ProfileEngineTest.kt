package me.magnum.melonds.domain.model.enhancement

import me.magnum.melonds.domain.model.Cheat
import me.magnum.melonds.domain.model.RomInfo
import me.magnum.melonds.domain.model.rom.config.RomGbaSlotConfig
import me.magnum.melonds.domain.model.retroachievements.RetroAchievementsEffectiveMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest
import java.util.zip.CRC32

class ProfileEngineTest {
    private val exactIdentity = RomIdentity("TEST", 1, "0123456789abcdef0123456789abcdef")
    private val userCheat = Cheat(9, 1, "User", null, "00000000 00000000", true)

    @Test
    fun parserAcceptsUnknownFieldsAndRequiresOneFallback() {
        val catalog = ProfileCatalog.parse(
            """{"catalogVersion":1,"unknown":true,"profiles":[
                {"schemaVersion":1,"id":"original.generic","profileVersion":1,"displayName":"Original","fallback":true}
            ]}""",
        )
        assertEquals("original.generic", catalog.originalProfile.id)
    }

    @Test
    fun embeddedCatalogAssetParsesAsTheProductionFallback() {
        val asset = File("src/main/assets/enhancement-profiles.json").readText()
        val catalog = ProfileCatalog.parse(asset)
        assertEquals("original.generic", catalog.originalProfile.id)
        assertEquals(1, catalog.catalogVersion)
    }

    @Test
    fun sm64dsProfilesRequireTheExactEuropeIdentityAndContainNoM6Code() {
        val catalog = ProfileCatalog.parse(File("src/main/assets/enhancement-profiles.json").readText())
        val sm64ds = RomIdentity("ASMP", 0, "ba3c4052e00c5cc31df5d5534c39de1b")
        assertEquals(ProfileMatch.MATCH_EXACT, catalog.match(sm64ds).second)
        assertEquals(ProfileMatch.MATCH_GAME_UNSUPPORTED_REVISION, catalog.match(sm64ds.copy(revision = 1)).second)
        assertEquals(ProfileMatch.MATCH_GAME_UNKNOWN_HASH, catalog.match(sm64ds.copy(retroAchievementsHash = "f".repeat(32))).second)

        val resolver = ProfileResolver(catalog)
        val device = DeviceProfileContext(
            setOf(
                EnhancementCapability.NDS_EMULATION,
                EnhancementCapability.ACTION_REPLAY,
                EnhancementCapability.SLOT2_ANALOG,
                EnhancementCapability.VULKAN_STRUCTURED_COMPOSITOR,
            ),
        )
        val enhanced = resolver.resolve(sm64ds, device, ProfilePreferences(selectedProfileId = "sm64ds.eu.thor-enhanced"), emptyList())
        val original = resolver.resolve(sm64ds, device, ProfilePreferences(selectedProfileId = "original.sm64ds.eu"), emptyList())
        assertEquals("sm64ds.eu.thor-enhanced", enhanced.profileId)
        assertEquals("original.sm64ds.eu", original.profileId)
        assertEquals(ProfileIntegrity.ENHANCED, enhanced.profileIntegrity)
        assertEquals(ProfileIntegrity.ORIGINAL, original.profileIntegrity)
        val analog = enhanced.curatedRuntimeCodes.single()
        assertEquals("sm64ds.eu.am64ds-analog.v1", analog.id)
        assertEquals(analog.codeSha256, sha256(analog.codeWords.joinToString("\n") + "\n"))
        assertTrue(original.curatedRuntimeCodes.isEmpty())
    }

    @Test
    fun launchPlannerOnlyEnablesAnalogForTheExactEnhancedIdentity() {
        val catalog = ProfileCatalog.parse(File("src/main/assets/enhancement-profiles.json").readText())
        val planner = ProfileLaunchPlanner(catalog)
        val exactInfo = RomInfo("ASMP", 0u, "SM64DS", "SM64DS", revision = 0)
        val identity = RomIdentity(exactInfo.gameCode, exactInfo.revision, "ba3c4052e00c5cc31df5d5534c39de1b")
        val exact = planner.resolve(identity, RomGbaSlotConfig.None, listOf(userCheat), requestedRaMode = ProfileRaMode.CASUAL)
        assertTrue(exact.useSlot2Analog)
        assertEquals(listOf("ThorDS: sm64ds.eu.am64ds-analog.v1", "User"), RuntimeActionReplayComposer.compose(exact.plan).map { it.name })

        val mismatch = planner.resolve(identity.copy(revision = 1), RomGbaSlotConfig.None, listOf(userCheat), requestedRaMode = ProfileRaMode.CASUAL)
        assertFalse(mismatch.useSlot2Analog)
        assertEquals(listOf("User"), RuntimeActionReplayComposer.compose(mismatch.plan).map { it.name })

        val protectedSlot = planner.resolve(
            identity,
            RomGbaSlotConfig.GbaRom(null, null),
            emptyList(),
            requestedRaMode = ProfileRaMode.CASUAL,
        )
        assertFalse(protectedSlot.useSlot2Analog)
        assertTrue(RuntimeActionReplayComposer.compose(protectedSlot.plan).isEmpty())

        val safeMode = planner.resolve(
            identity,
            RomGbaSlotConfig.None,
            emptyList(),
            enhancementsEnabled = false,
            requestedRaMode = ProfileRaMode.CASUAL,
        )
        assertFalse(safeMode.useSlot2Analog)
        assertEquals("original.sm64ds.eu", safeMode.plan.profileId)
        assertTrue(RuntimeActionReplayComposer.compose(safeMode.plan).isEmpty())
    }

    @Test
    fun launchPlannerBindsPolicyToTheCompleteProfilePlan() {
        val catalog = ProfileCatalog.parse(File("src/main/assets/enhancement-profiles.json").readText())
        val planner = ProfileLaunchPlanner(catalog)
        val identity = RomIdentity("ASMP", 0, "ba3c4052e00c5cc31df5d5534c39de1b")

        val casual = planner.resolve(
            identity = identity,
            currentSlot = RomGbaSlotConfig.None,
            userCheats = listOf(userCheat),
            requestedRaMode = ProfileRaMode.CASUAL,
            requestedArm9Percent = 125,
        )
        assertEquals(ProfileIntegrity.ENHANCED, casual.plan.profileIntegrity)
        assertEquals(ProfileRaMode.CASUAL, casual.plan.requestedRaMode)
        assertEquals(125, casual.plan.requestedArm9Percent)
        assertEquals(100, casual.plan.effectiveArm9Percent)
        assertEquals(Arm9OverclockCapability.PLUMBING_ONLY, casual.plan.arm9OverclockCapability)
        assertEquals(RetroAchievementsEffectiveMode.CASUAL, casual.retroAchievementsPolicy.effectiveMode)
        assertTrue(casual.plan.enhancements.any { it.id == "analog" && it.enabled })
        assertTrue(casual.retroAchievementsPolicy.runtimeFeaturePermissions.allowEnhancements)

        val off = planner.resolve(
            identity = identity,
            currentSlot = RomGbaSlotConfig.None,
            userCheats = listOf(userCheat),
            requestedRaMode = ProfileRaMode.OFF,
        )
        assertEquals(RetroAchievementsEffectiveMode.OFF, off.retroAchievementsPolicy.effectiveMode)
        assertTrue(off.plan.enhancements.any { it.id == "analog" && it.enabled })

        val blockedHardcore = planner.resolve(
            identity = identity,
            currentSlot = RomGbaSlotConfig.None,
            userCheats = emptyList(),
            requestedRaMode = ProfileRaMode.HARDCORE,
        )
        assertEquals(ProfileIntegrity.ENHANCED, blockedHardcore.plan.profileIntegrity)
        assertEquals(ProfileRaMode.HARDCORE, blockedHardcore.plan.requestedRaMode)
        assertEquals(RetroAchievementsEffectiveMode.BLOCKED, blockedHardcore.retroAchievementsPolicy.effectiveMode)
        assertTrue("enhanced_profile" in blockedHardcore.retroAchievementsPolicy.reasonCodeValues)
        assertTrue("requested_mode_unavailable" in blockedHardcore.retroAchievementsPolicy.reasonCodeValues)

        val cleanHardcore = planner.resolve(
            identity = identity,
            currentSlot = RomGbaSlotConfig.None,
            userCheats = emptyList(),
            enhancementsEnabled = false,
            requestedRaMode = ProfileRaMode.HARDCORE,
            requestedArm9Percent = 125,
        )
        assertEquals(ProfileIntegrity.ORIGINAL, cleanHardcore.plan.profileIntegrity)
        assertEquals(ProfileRaMode.HARDCORE, cleanHardcore.plan.requestedRaMode)
        assertEquals(125, cleanHardcore.plan.requestedArm9Percent)
        assertEquals(100, cleanHardcore.plan.effectiveArm9Percent)
        assertEquals(Arm9OverclockCapability.PLUMBING_ONLY, cleanHardcore.plan.arm9OverclockCapability)
        assertEquals(RetroAchievementsEffectiveMode.HARDCORE, cleanHardcore.retroAchievementsPolicy.effectiveMode)
        assertFalse(cleanHardcore.retroAchievementsPolicy.runtimeFeaturePermissions.allowEnhancements)
        assertTrue("arm9_percent_not_100" !in cleanHardcore.retroAchievementsPolicy.reasonCodeValues)

        val autoloadBlocked = planner.resolve(
            identity = identity,
            currentSlot = RomGbaSlotConfig.None,
            userCheats = emptyList(),
            enhancementsEnabled = false,
            requestedRaMode = ProfileRaMode.HARDCORE,
            saveStateResumeEnabled = true,
        )
        assertEquals(RetroAchievementsEffectiveMode.BLOCKED, autoloadBlocked.retroAchievementsPolicy.effectiveMode)
        assertEquals(
            listOf("save_state_resume_enabled"),
            autoloadBlocked.retroAchievementsPolicy.reasonCodeValues,
        )
    }

    @Test
    fun planHashIncludesRequestedModeAndIntegrity() {
        val resolver = ProfileResolver(ProfileCatalog.from(EnhancementCatalogDocument(1, listOf(original(), enhanced()))))
        val originalCasual = resolver.resolve(
            exactIdentity,
            DeviceProfileContext(emptySet()),
            ProfilePreferences(selectedProfileId = "original.generic", requestedRaMode = ProfileRaMode.CASUAL),
            emptyList(),
        )
        val originalHardcore = resolver.resolve(
            exactIdentity,
            DeviceProfileContext(emptySet()),
            ProfilePreferences(selectedProfileId = "original.generic", requestedRaMode = ProfileRaMode.HARDCORE),
            emptyList(),
        )
        val enhancedCasual = resolver.resolve(
            exactIdentity,
            DeviceProfileContext(setOf(EnhancementCapability.SLOT2_ANALOG)),
            ProfilePreferences(selectedProfileId = "test.enhanced", requestedRaMode = ProfileRaMode.CASUAL),
            emptyList(),
        )
        val originalRequested125 = resolver.resolve(
            exactIdentity,
            DeviceProfileContext(emptySet()),
            ProfilePreferences(
                selectedProfileId = "original.generic",
                requestedArm9Percent = 125,
            ),
            emptyList(),
        )
        val originalValidated125 = resolver.resolve(
            exactIdentity,
            DeviceProfileContext(emptySet(), Arm9OverclockCapability.VALIDATED),
            ProfilePreferences(
                selectedProfileId = "original.generic",
                requestedArm9Percent = 125,
            ),
            emptyList(),
        )

        assertNotEquals(originalCasual.planHash, originalHardcore.planHash)
        assertNotEquals(originalCasual.planHash, enhancedCasual.planHash)
        assertNotEquals(originalCasual.planHash, originalRequested125.planHash)
        assertNotEquals(originalRequested125.planHash, originalValidated125.planHash)
        assertEquals(125, originalRequested125.requestedArm9Percent)
        assertEquals(100, originalRequested125.effectiveArm9Percent)
        assertEquals(Arm9OverclockCapability.PLUMBING_ONLY, originalRequested125.arm9OverclockCapability)
        assertEquals(125, originalValidated125.effectiveArm9Percent)
        assertTrue(originalHardcore.diagnostics().any { it == "requested_ra=HARDCORE" })
        assertTrue(enhancedCasual.diagnostics().any { it == "integrity=ENHANCED" })
        assertTrue(originalValidated125.diagnostics().any { it == "requested_arm9_percent=125" })
        assertTrue(originalValidated125.diagnostics().any { it == "effective_arm9_percent=125" })
        assertTrue(originalValidated125.diagnostics().any { it == "arm9_capability=VALIDATED" })
    }

    @Test
    fun developerWidescreenProbeEnablesOnlyTheExactGuardedEuropeAspectCode() {
        val catalog = ProfileCatalog.parse(File("src/main/assets/enhancement-profiles.json").readText())
        val planner = ProfileLaunchPlanner(catalog)
        val identity = RomIdentity("ASMP", 0, "ba3c4052e00c5cc31df5d5534c39de1b")

        val normal = planner.resolve(identity, RomGbaSlotConfig.None, emptyList(), requestedRaMode = ProfileRaMode.CASUAL)
        assertTrue(normal.plan.curatedRuntimeCodes.none { it.id == "sm64ds.eu.aspect-16x9.dev.v1" })

        val probe = planner.resolve(
            identity = identity,
            currentSlot = RomGbaSlotConfig.None,
            userCheats = emptyList(),
            developerWidescreenProbe = true,
            requestedRaMode = ProfileRaMode.CASUAL,
        )
        val aspect = probe.plan.curatedRuntimeCodes.single { it.id == "sm64ds.eu.aspect-16x9.dev.v1" }
        assertEquals(
            listOf(
                "0200D03C 00001555",
                "0200F64C 00001555",
                "02015774 00001555",
                "020C025C 00001555",
            ),
            aspect.expectedOriginalWords,
        )
        assertEquals("28445a89a887a556b4a0564e21f8ca579eeab437471bff1b38c681efd6a3bbc6", sha256(aspect.codeWords.joinToString("\n") + "\n"))
    }

    @Test
    fun matcherRequiresExactCodeRevisionAndHash() {
        val catalog = ProfileCatalog.from(EnhancementCatalogDocument(1, listOf(original(), enhanced())))
        assertEquals(ProfileMatch.MATCH_EXACT, catalog.match(exactIdentity).second)
        assertEquals(ProfileMatch.MATCH_GAME_UNSUPPORTED_REVISION, catalog.match(exactIdentity.copy(revision = 2)).second)
        assertEquals(ProfileMatch.MATCH_GAME_UNKNOWN_HASH, catalog.match(exactIdentity.copy(retroAchievementsHash = "f".repeat(32))).second)
        assertEquals(ProfileMatch.NO_MATCH, catalog.match(exactIdentity.copy(gameCode = "NOPE")).second)
    }

    @Test
    fun catalogRejectsCyclesAndMalformedRuntimeCodes() {
        val cyclic = enhanced(
            enhancements = listOf(
                enhancement("a", requires = setOf("b")),
                enhancement("b", requires = setOf("a")),
            ),
        )
        assertFails { ProfileCatalog.from(EnhancementCatalogDocument(1, listOf(original(), cyclic))) }
        val malformed = enhanced(enhancements = listOf(enhancement("bad", words = listOf("not a code"))))
        assertFails { ProfileCatalog.from(EnhancementCatalogDocument(1, listOf(original(), malformed))) }
    }

    @Test
    fun resolverKeepsCuratedCodesAndUserCheatsSeparate() {
        val resolver = ProfileResolver(ProfileCatalog.from(EnhancementCatalogDocument(1, listOf(original(), enhanced()))))
        val plan = resolver.resolve(
            exactIdentity,
            DeviceProfileContext(setOf(EnhancementCapability.SLOT2_ANALOG)),
            ProfilePreferences(selectedProfileId = "test.enhanced", requestedRaMode = ProfileRaMode.CASUAL),
            listOf(userCheat),
        )
        assertEquals("test.enhanced", plan.profileId)
        assertEquals(listOf("analog"), plan.curatedRuntimeCodes.map { it.id })
        assertEquals(listOf(userCheat), plan.userCheats)
        assertTrue(plan.enhancements.single { it.id == "analog" }.enabled)
        assertEquals(listOf("ThorDS: analog", "User"), RuntimeActionReplayComposer.compose(plan).map { it.name })
    }

    @Test
    fun resolverExplainsMissingCapabilitiesAndKeepsHardcoreExplicit() {
        val resolver = ProfileResolver(ProfileCatalog.from(EnhancementCatalogDocument(1, listOf(original(), enhanced()))))
        val unavailable = resolver.resolve(exactIdentity, DeviceProfileContext(emptySet()), ProfilePreferences(selectedProfileId = "test.enhanced"), emptyList())
        assertFalse(unavailable.enhancements.single().enabled)
        assertTrue(unavailable.enhancements.single().reason!!.startsWith("missing_capability:"))

        val hardcoreResolver = ProfileResolver(
            ProfileCatalog.from(
                EnhancementCatalogDocument(
                    1,
                    listOf(original(), enhanced().copy(allowedRaModes = ProfileRaMode.entries.toSet())),
                ),
            ),
        )
        val hardcore = hardcoreResolver.resolve(
            exactIdentity,
            DeviceProfileContext(setOf(EnhancementCapability.SLOT2_ANALOG)),
            ProfilePreferences(selectedProfileId = "test.enhanced", requestedRaMode = ProfileRaMode.HARDCORE),
            emptyList(),
        )
        assertEquals(ProfileRaMode.HARDCORE, hardcore.effectiveRaMode)
        assertFalse(hardcore.enhancements.single().enabled)
        assertEquals("conflict:RA_HARDCORE", hardcore.enhancements.single().reason)
    }

    @Test
    fun resolverDisablesBothSidesOfAnEnhancementConflict() {
        val conflictProfile = enhanced(
            enhancements = listOf(
                enhancement("analog", conflicts = setOf("widescreen")),
                enhancement("widescreen", conflicts = setOf("analog")),
            ),
        )
        val resolver = ProfileResolver(ProfileCatalog.from(EnhancementCatalogDocument(1, listOf(original(), conflictProfile))))
        val plan = resolver.resolve(exactIdentity, DeviceProfileContext(setOf(EnhancementCapability.SLOT2_ANALOG)), ProfilePreferences("test.enhanced"), emptyList())
        assertTrue(plan.enhancements.all { !it.enabled })
        assertTrue(plan.enhancements.all { it.reason!!.startsWith("conflict:") })
    }

    @Test
    fun fallbackIsSafeAndPlanHashIsDeterministic() {
        val resolver = ProfileResolver(ProfileCatalog.from(EnhancementCatalogDocument(1, listOf(original(), enhanced()))))
        val first = resolver.resolve(exactIdentity.copy(gameCode = "NOPE"), DeviceProfileContext(emptySet()), ProfilePreferences(), listOf(userCheat))
        val second = resolver.resolve(exactIdentity.copy(gameCode = "NOPE"), DeviceProfileContext(emptySet()), ProfilePreferences(), listOf(userCheat))
        assertEquals("original.generic", first.profileId)
        assertEquals(ProfileMatch.NO_MATCH, first.match)
        assertTrue(first.curatedRuntimeCodes.isEmpty())
        assertEquals(first.planHash, second.planHash)
        assertEquals("nope:1:${exactIdentity.retroAchievementsHash}", exactIdentity.copy(gameCode = "NOPE").stableKey())
        assertTrue(first.diagnostics().any { it == "user_cheats=1" })
    }

    @Test
    fun exactProfileNeedsAnExplicitSelectionAndOriginalCanAlwaysBeSelected() {
        val resolver = ProfileResolver(ProfileCatalog.from(EnhancementCatalogDocument(1, listOf(original(), enhanced()))))
        val defaultPlan = resolver.resolve(exactIdentity, DeviceProfileContext(setOf(EnhancementCapability.SLOT2_ANALOG)), ProfilePreferences(), emptyList())
        val originalPlan = resolver.resolve(
            exactIdentity,
            DeviceProfileContext(setOf(EnhancementCapability.SLOT2_ANALOG)),
            ProfilePreferences(selectedProfileId = "original.generic"),
            emptyList(),
        )
        assertEquals("original.generic", defaultPlan.profileId)
        assertEquals("original.generic", originalPlan.profileId)
        assertTrue(originalPlan.curatedRuntimeCodes.isEmpty())
    }

    @Test
    fun corruptPreferenceRowsFallBackToDefaults() {
        assertEquals(ProfilePreferences(), ProfilePreferencesCodec.decode("{bad"))
        assertEquals(
            100,
            ProfilePreferencesCodec.decode("""{"selectedProfileId":"original.generic","requestedRaMode":"CASUAL"}""").requestedArm9Percent,
        )
        val preferences = ProfilePreferences("test.enhanced", mapOf("analog" to false), ProfileRaMode.OFF)
        assertEquals(preferences, ProfilePreferencesCodec.decode(ProfilePreferencesCodec.encode(preferences)))
    }

    @Test
    fun ipsAndBpsApplySyntheticPatchesAndRejectWrongSource() {
        assertEquals("aZc", IpsPatchApplier.apply("abc".encodeToByteArray(), ipsReplace(1, 'Z'.code.toByte())).decodeToString())
        val bps = bpsTargetRead("abc".encodeToByteArray(), "axc".encodeToByteArray())
        assertEquals("axc", BpsPatchApplier.apply("abc".encodeToByteArray(), bps).decodeToString())
        assertFails { BpsPatchApplier.apply("abd".encodeToByteArray(), bps) }
        assertEquals("aaaa", BpsPatchApplier.apply("a".encodeToByteArray(), bpsTargetCopy()).decodeToString())
    }

    private fun original() = EnhancementProfile(1, "original.generic", 1, "Original", fallback = true)

    private fun enhanced(enhancements: List<EnhancementDefinition> = listOf(enhancement("analog"))) = EnhancementProfile(
        schemaVersion = 1,
        id = "test.enhanced",
        profileVersion = 1,
        displayName = "Test Enhanced",
        integrity = ProfileIntegrity.ENHANCED,
        game = ProfileGameIdentity("NDS", "TEST", 1, setOf(exactIdentity.retroAchievementsHash)),
        enhancements = enhancements,
        allowedRaModes = setOf(ProfileRaMode.OFF, ProfileRaMode.CASUAL),
    )

    private fun enhancement(
        id: String,
        requires: Set<String> = emptySet(),
        conflicts: Set<String> = setOf("RA_HARDCORE"),
        words: List<String> = listOf("00000000 00000000"),
    ) = EnhancementDefinition(
        id = id,
        displayName = id,
        defaultEnabled = true,
        kind = EnhancementKind.ACTION_REPLAY,
        requiredCapabilities = setOf(EnhancementCapability.SLOT2_ANALOG),
        requires = requires,
        conflicts = conflicts,
        provenance = "synthetic test",
        runtimeCode = RuntimeActionReplayCode(id, words, "a".repeat(64)),
    )

    private fun ipsReplace(offset: Int, replacement: Byte): ByteArray {
        return "PATCH".encodeToByteArray() + byteArrayOf(0, 0, offset.toByte(), 0, 1, replacement) + "EOF".encodeToByteArray()
    }

    private fun bpsTargetRead(source: ByteArray, target: ByteArray): ByteArray {
        val data = mutableListOf<Byte>()
        data += "BPS1".encodeToByteArray().toList()
        data += bpsNumber(source.size.toLong())
        data += bpsNumber(target.size.toLong())
        data += bpsNumber(0)
        data += bpsNumber(0) // source read, one byte
        data += bpsNumber(1) // target read, one byte
        data += target[1]
        data += bpsNumber(0) // source read, one byte
        data += littleEndianCrc(source)
        data += littleEndianCrc(target)
        data += littleEndianCrc(data.toByteArray())
        return data.toByteArray()
    }

    private fun bpsTargetCopy(): ByteArray {
        val source = "a".encodeToByteArray()
        val target = "aaaa".encodeToByteArray()
        val data = mutableListOf<Byte>()
        data += "BPS1".encodeToByteArray().toList()
        data += bpsNumber(source.size.toLong())
        data += bpsNumber(target.size.toLong())
        data += bpsNumber(0)
        data += bpsNumber(1) // target read, one byte
        data += 'a'.code.toByte()
        data += bpsNumber(11) // target copy, three bytes
        data += bpsNumber(0) // relative target offset remains zero
        data += littleEndianCrc(source)
        data += littleEndianCrc(target)
        data += littleEndianCrc(data.toByteArray())
        return data.toByteArray()
    }

    private fun bpsNumber(input: Long): List<Byte> {
        var value = input
        val result = mutableListOf<Byte>()
        while (true) {
            val next = (value and 0x7f).toInt()
            value = value shr 7
            if (value == 0L) return result.apply { add((next or 0x80).toByte()) }
            result += next.toByte()
            value--
        }
    }

    private fun littleEndianCrc(value: ByteArray): List<Byte> {
        val crc = CRC32().also { it.update(value) }.value
        return List(4) { index -> (crc shr (index * 8)).toByte() }
    }

    private fun assertFails(block: () -> Unit) {
        try {
            block()
            throw AssertionError("Expected failure")
        } catch (_: IllegalArgumentException) {
        }
    }

    private fun sha256(value: String): String {
        return MessageDigest.getInstance("SHA-256").digest(value.encodeToByteArray()).joinToString("") { "%02x".format(it) }
    }
}
