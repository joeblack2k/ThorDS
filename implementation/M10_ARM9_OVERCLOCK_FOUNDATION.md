# M10 — ARM9-overclockfundering

## Doel

Een veilige end-to-end configuratie en meetbasis leveren, en alleen indien bewezen een effectieve >100%-coremodus inschakelen.

## Vereiste input

Core pin; timing research; M9 policy; performance harness.

## Werk

1. Voeg config/model/parcel/JNI.
2. Capability enum.
3. Telemetry bij 100%.
4. Schrijf ARM9 scheduler ADR.
5. Implementeer featureflagged timingstrategie.
6. Values 100/125/150/175/200.
7. Relaunch requirement.
8. Save-state ratio metadata.
9. Hardcore force 100.
10. Stress tests op SM64DS en generieke smoke.
11. Bij onveilig: >100 UI blokkeren maar plumbing behouden.

## Tests

- config roundtrip;
- invalid values;
- 100%-equivalence;
- wall clock;
- audio;
- RTC;
- JIT/interpreter where available;
- save state mismatch;
- thermal/performance;
- generic DS smoke.

## Bewijs

```text
docs/project/adr/ADR-arm9-overclock.md
docs/evidence/m10/config-roundtrip.txt
docs/evidence/m10/telemetry-100.csv
docs/evidence/m10/telemetry-ratios.csv
docs/evidence/m10/timing-comparison.json
docs/evidence/m10/capability-status.md
```

## Exitgate

Minimum groen:
- complete plumbing;
- 100% equivalent;
- policy/save-state correct.

>100 wordt alleen `EXPERIMENTAL/VALIDATED` bij timingbewijs; anders blijft effectief 100.

## Richtcommit

```text
core: add guarded ARM9 overclock foundation
```
