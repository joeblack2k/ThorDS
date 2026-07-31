# M4 — Enhancement Profile Engine

## Doel

Een generieke, testbare resolver bouwen die ROMidentity, devicecapabilities, userkeuzes en RA-policy omzet in één immutable sessieplan.

## Vereiste input

Architectuurdocs voor profiles/patches/config; bestaande ROM config en cheat flow.

## Werk

1. Implementeer models/schema.
2. Embedded read-only catalogus.
3. Exact identity matcher.
4. Capabilityprobe.
5. dependency/conflict resolver.
6. profile preferences repository.
7. resolved session plan.
8. curated codes los van user cheats.
9. BPS/IPS interfaces en synthetische implementatietests.
10. diagnostics/session details.
11. safe Original fallback.
12. schema/catalog build validation.

## Tests

- parser;
- exact/mismatch variants;
- dependency cycles;
- conflict matrix;
- malformed AR;
- BPS/IPS synthetic;
- migration/corrupt row;
- Original fallback;
- deterministic plan/hash.

## Bewijs

```text
docs/evidence/m4/profile-catalog-dump.json
docs/evidence/m4/profile-tests.txt
docs/evidence/m4/session-plan-example-redacted.json
```

## Exitgate

- geen patch zonder exact match;
- Original altijd;
- curated/user scheiding;
- deterministic plan;
- all tests groen.

## Richtcommit

```text
profile: add deterministic enhancement profile engine
```
