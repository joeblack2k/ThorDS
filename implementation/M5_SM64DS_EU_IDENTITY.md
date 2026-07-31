# M5 — SM64DS EU identity en profiel

## Doel

Het eerste productieprofiel exact koppelen aan de lokale Europese SM64DS retail-ROM.

## Vereiste input

M4; lokale ROMidentity; RA game/hash; profile schema.

## Werk

1. Voeg `sm64ds.eu.thor-enhanced`.
2. Voeg `original.sm64ds.eu`.
3. Match ASMP, revision en RA-hash.
4. Voeg provenance records toe.
5. Definieer enhancementtoggles/capabilities.
6. Definieer savecompatibilitygroup.
7. Toon profile card in UI/diagnostics.
8. Onbekende ASMP revision krijgt geen codes.
9. Hash source vóór/na.

## Tests

- exact local match;
- één-byte revision mutation fixture → mismatch;
- RA hash mutation → mismatch;
- filename changes → zelfde match;
- duplicate ROM URI → stable identity;
- profile state persists.

## Bewijs

```text
docs/evidence/m5/sm64ds-profile-resolution.json
docs/evidence/m5/source-rom-pre.sha256
docs/evidence/m5/variant-mismatch-tests.txt
```

## Exitgate

- lokale EU-ROM kiest exact beide profielopties;
- Enhanced toont dependencies;
- nog geen patches onbedoeld actief;
- source unchanged.

## Richtcommit

```text
profile: add exact Super Mario 64 DS Europe profile
```
