# M8 — True Widescreen productimplementatie

## Doel

De M7-spike omzetten in een stabiele, standaard ingeschakelde Vulkanfeature voor het exacte SM64DS-profiel.

## Vereiste input

Groene M7 ADR/captures; scene policy; performancebudget.

## Werk

1. Productiseer config/JNI/push constants.
2. Implementeer full world UV en UI-safe UV.
3. Implementeer underlay policies.
4. Implementeer scene classifier + hysterese.
5. Implementeer 4:3 fallback.
6. Integreer EU aspect/culling runtimecode.
7. Corrigeer eventuele 3D-HUD game-side.
8. Houd bottom 4:3.
9. Voeg safe mode.
10. Voeg synthetic golden tests.
11. Optimaliseer allocations/passes.
12. Verberg anamorphic diagnostic voor normale gebruiker.

## Tests

Volledige `testing/TRUE_WIDESCREEN_ACCEPTANCE_TESTS.md`, performancevergelijking, 30 minuten scene transitions, renderer fallback, sleep/resume.

## Bewijs

```text
docs/evidence/m8/scenes/
docs/evidence/m8/geometry.json
docs/evidence/m8/classifier-log.csv
docs/evidence/m8/performance.json
docs/evidence/m8/fallback-proof.md
```

## Exitgate

- alle verplichte scenes;
- 2D niet gerekt;
- world breed;
- bottom correct;
- fallback veilig;
- performance binnen budget;
- default ON alleen op capabilitymatch.

## Richtcommit

```text
render: ship SM64DS layer-aware true widescreen
```
