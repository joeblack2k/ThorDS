# M6 — Analoge besturing

## Doel

De Europese AM64DS runtimepatch en de bestaande Slot-2-accessory productmatig koppelen aan een betrouwbare Thor-controllerervaring.

## Vereiste input

M5 exact profile; `profiles/SM64DS_ANALOG_AR_CODE.md`; rc5 Slot-2 paths.

## Werk

1. Embed code als curated runtime patch.
2. Check/normalize AR words.
3. Activeer Slot-2 Analog via session plan.
4. Implementeer radial processor.
5. Implementeer right-stick camera hysteresis.
6. Voeg debug telemetry toe.
7. Houd user cheats apart.
8. Maak gameprofile override zonder library database te koppelen aan filename.
9. Test original fallback.
10. Voeg UI status/relaunch semantics.

## Tests

Automated pure input tests plus fysieke:
- center/cardinals/diagonals/circle/reversal;
- walk/run;
- swim/fly/slide;
- camera;
- reconnect;
- relaunch;
- other DS game unaffected;
- library still populated.

## Bewijs

```text
docs/evidence/m6/effective-ar-code-sha256.txt
docs/evidence/m6/slot2-analog.csv
docs/evidence/m6/controller-map.json
docs/evidence/m6/gameplay-checklist.md
docs/evidence/m6/library-relaunch-proof.txt
```

## Exitgate

- continuous movement;
- camera usable;
- no ROM mutation;
- no library regression;
- toggle off restores Original digital behavior.

## Richtcommit

```text
input: integrate SM64DS Europe Slot-2 analog profile
```
