# M3 — Productrebrand en veilige defaults

## Doel

Een afzonderlijk installeerbare ThorDS-build maken zonder upstream updater- of packageconflict, met safe-modefundering.

## Vereiste input

M1/M2 groen; product-idbesluit; licentieaudit.

## Werk

1. Wijzig applicationId en appnaam.
2. Voeg product/upstream/coreversie aan About toe.
3. Schakel updater veilig uit of routeer alleen naar eigen kanaal.
4. Voeg product source/notices links toe.
5. Voeg Thor device capabilityprobe toe.
6. Zet soft controls op Thor default uit zonder useroverride te overschrijven.
7. Voeg safe-mode startflag/preference toe.
8. Behoud niet-Thor defaults.
9. Voeg migrationtests voor package/config toe.

## Tests

- installeer naast upstream MelonDualDS;
- beide apps starten;
- no shared-data collision;
- updater kan geen upstream APK installeren;
- safe mode start Native 4:3;
- non-Thor unit/device fallback.

## Bewijs

```text
docs/evidence/m3/package-list.txt
docs/evidence/m3/about-screen.png
docs/evidence/m3/updater-policy.md
docs/evidence/m3/safe-mode.txt
```

## Exitgate

- unieke package;
- juiste branding;
- licenties zichtbaar;
- safe mode;
- upstream app blijft intact.

## Richtcommit

```text
app: establish ThorDS Enhanced product identity
```
