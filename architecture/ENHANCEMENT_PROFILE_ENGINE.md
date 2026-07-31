# Enhancement Profile Engine

## Verantwoordelijkheid

De engine vertaalt een exacte ROM en apparaatcontext naar een veilige, verklaarbare sessieconfiguratie.

Niet verantwoordelijk voor:

- ROMbibliotheekscanning zelf;
- algemene user-cheatbewerking;
- RA-serverlogica;
- direct rendererwerk.

## Componenten

```text
ProfileCatalog
ProfileParser
RomIdentityMatcher
CapabilityProbe
ProfileResolver
PatchResolver
PolicyResolver
SessionPlanBuilder
ProfileStateRepository
ProvenanceRepository
```

## Catalogus

V0.1 bevat embedded read-only profiles:

```text
original.generic
sm64ds.eu.thor-enhanced
```

User overrides staan afzonderlijk:

```text
ProfileUserPreferences
```

De ingebouwde profiledefinitie wordt nooit in-place gemuteerd.

## Resolution

Input:

- `RomIdentity`;
- device capabilities;
- renderer capabilities;
- current settings;
- RA requested mode;
- user enhancement choices.

Output:

```text
ResolvedSessionPlan
```

## Exact match

Scores mogen helpen een variant te vinden, maar een patch wordt alleen geactiveerd bij volledige vereiste match.

```text
MATCH_EXACT
MATCH_GAME_UNSUPPORTED_REVISION
MATCH_GAME_UNKNOWN_HASH
NO_MATCH
```

Bij niet-exact:

- toon profilecard als beschikbaar maar incompatibel;
- activeer geen code;
- bied Original/General mode.

## Dependencies

Voorbeeld:

```text
analogControls requires SLOT2_ANALOG
trueWidescreen requires VULKAN_STRUCTURED_COMPOSITOR
trueWidescreen requires SM64DS_EU_ASPECT_PATCH
arm9Overclock requires ARM9_OC_CORE_SUPPORT
fps60 requires ARM9_OC_VALIDATED
```

## Conflicten

```text
hardcore conflicts with any enhancement
userGbaRom conflicts with slot2Analog
openGl conflicts with trueWidescreenVulkan
rewind/loadState conflicts with hardcore
```

De resolver kiest niet stilzwijgend. Hij levert:

```text
enabled
disabled
reason
requiredRelaunch
```

## Patchvolgorde

1. source identity;
2. optionele cache delta patches;
3. runtime game identity opnieuw controleren;
4. curated runtime codes;
5. user cheats;
6. emulator feature overrides;
7. RA effective mode;
8. start.

Voor v0.1 gebruikt SM64DS geen cachepatch.

## State

Sla op per stabiele ROMkey, niet filename:

```text
sourceRaHash + gameCode + revision
```

Userkeuzes:

- selected profile;
- toggle states;
- controller curve;
- RA requested mode;
- renderer override;
- OC percent.

Nooit opslaan:

- ROMbytes;
- RA password;
- patchbinary zonder provenance.

## Versies/migraties

Ieder profiel:

```text
schemaVersion
profileVersion
catalogVersion
```

Bij een patchupdate:

- behoud usertoggle als id gelijk;
- forceer re-resolve;
- invalideer runtime/cachehash;
- toon changelog wanneer gedrag relevant verandert.

## Diagnostiek

Een “Session details” scherm toont:

- ROM identity;
- profile id/version;
- active patches;
- code hashes;
- renderer mode;
- display roles;
- RA effective mode;
- OC status;
- reasons for disabled features.

Dit is essentieel voor bugreports.
