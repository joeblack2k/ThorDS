# Patchpipeline

## Patchtypen

### Runtime Action Replay

Beste keuze voor v0.1:

- analog;
- aspect/culling;
- kleine game-side correcties.

Eigenschappen:

- source ROM blijft intact;
- codes conditioneel;
- makkelijk per sessie;
- RA source hash blijft beschikbaar;
- Hardcore conflict.

### BPS

Voorkeursdeltaformaat voor toekomstige complexe patches:

- source checksum;
- target checksum;
- patch checksum;
- robuustere foutdetectie dan IPS.

### IPS

Nodig voor bestaande communitypatches. Beperkingen:

- geen intrinsieke sterke source-identiteit;
- mogelijk onduidelijke truncation;
- daarom altijd externe hashes en filesizevoorwaarden.

## Pipelineobjecten

```text
PatchDefinition
PatchApplicability
PatchPrecondition
PatchResult
PatchEvidence
```

## Runtimecodeflow

```text
resolve profile
→ validate exact ROM identity
→ parse AR words
→ validate check-before-write presence where required
→ combine curated codes in deterministic order
→ detect overlapping writes/conflicts
→ append permitted user cheats separately
→ send effective list through JNI
```

## Overlapdetectie

Voor statische direct-writecodes:

- decodeer addresses/widths;
- detecteer overlapping writes;
- profile declares intentional override order;
- onbekende dynamic code overlap wordt als risico gemarkeerd.

## Atomic cachepatching

```text
source ROM read-only
→ temp cache file
→ apply BPS/IPS
→ fsync
→ compute target SHA-256
→ atomic rename
```

Cachekey:

```text
sourceFullSha256/profileId/profileVersion/patchSetSha256
```

## Geen in-place

Open source ROM nooit writeable. De importer vraagt alleen read permission via SAF.

## Cache lifecycle

- invalidate on profileversion;
- invalidate on patchhash;
- LRU/sizebudget;
- handmatige clear;
- geen cloudbackup;
- geen export in normale UI;
- no-media marker waar relevant.

## Patchprovenance

De UI kan per enhancement tonen:

```text
Analog Controls
Source: AM64DS by LRFLEW
Type: runtime code
Variant: Europe
Profile version: 1
```

## Failures

- identity mismatch → patch niet toepassen;
- code precondition mismatch → enhancement fail, Original fallback;
- target checksum mismatch → temp verwijderen;
- insufficient storage → originele ROM starten;
- patch parser error → profile invalid;
- conflict → start blokkeren of expliciete keuze, nooit willekeurig kiezen.

## Testfixtures

Gebruik synthetische bytearrays voor:

- IPS records;
- BPS success/failure;
- source mismatch;
- truncation;
- overlapping writes;
- atomic cleanup.

Geen echte ROMbytes in unit tests.
