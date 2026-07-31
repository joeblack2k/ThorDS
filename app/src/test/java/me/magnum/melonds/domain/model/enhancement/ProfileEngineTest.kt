package me.magnum.melonds.domain.model.enhancement

import me.magnum.melonds.domain.model.Cheat
import me.magnum.melonds.domain.model.RomInfo
import me.magnum.melonds.domain.model.rom.config.RomGbaSlotConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
        val exact = planner.resolve(identity, RomGbaSlotConfig.None, listOf(userCheat))
        assertTrue(exact.useSlot2Analog)
        assertEquals(listOf("ThorDS: sm64ds.eu.am64ds-analog.v1", "User"), RuntimeActionReplayComposer.compose(exact.plan).map { it.name })

        val mismatch = planner.resolve(identity.copy(revision = 1), RomGbaSlotConfig.None, listOf(userCheat))
        assertFalse(mismatch.useSlot2Analog)
        assertEquals(listOf("User"), RuntimeActionReplayComposer.compose(mismatch.plan).map { it.name })

        val protectedSlot = planner.resolve(identity, RomGbaSlotConfig.GbaRom(null, null), emptyList())
        assertFalse(protectedSlot.useSlot2Analog)
        assertTrue(RuntimeActionReplayComposer.compose(protectedSlot.plan).isEmpty())

        val safeMode = planner.resolve(identity, RomGbaSlotConfig.None, emptyList(), enhancementsEnabled = false)
        assertFalse(safeMode.useSlot2Analog)
        assertEquals("original.sm64ds.eu", safeMode.plan.profileId)
        assertTrue(RuntimeActionReplayComposer.compose(safeMode.plan).isEmpty())
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
