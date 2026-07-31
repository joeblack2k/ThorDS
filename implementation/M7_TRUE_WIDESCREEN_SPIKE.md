# M7 — True Widescreen technische spike

## Doel

Bewijzen dat rc5 op SM64DS 3D en 2D/HUD voldoende kan scheiden en de Europese game-side aspectpatch correct afleiden.

## Vereiste input

M5/M6; Vulkan source; decomp pin; local ROM; debug capture plan.

## Werk

1. Clone decomp researchpin gitignored.
2. Map alle relevante EU `0x1555` aspect sites.
3. Bouw conditional developer AR-code.
4. Voeg presentation mode en debug layer captures toe.
5. Capture title, file select, castle, BOB, pause, star select, cutscene.
6. Implementeer minimale dual-UV proof in Vulkan.
7. Meet HUD geometry.
8. Classificeer unsafe scenes.
9. Schrijf ADR met production design.
10. Houd feature developer-only.

## Tests

- patch preconditions;
- extra horizontal FOV;
- world object ratio;
- HUD circle ratio;
- layer capture consistency;
- scene classifier prototype;
- SPIR-V checks;
- no capture/previous-frame regression.

## Bewijs

```text
docs/research/sm64ds-widescreen-symbol-map.md
docs/evidence/m7/eu-aspect-original-words.txt
docs/evidence/m7/layer-captures/
docs/evidence/m7/geometry-measurements.json
docs/project/adr/ADR-true-widescreen.md
```

## Exitgate

Groen als:
- EU patch semantisch bewezen;
- BOB world 16:9;
- HUD apart samplebaar of concrete game-side fix bekend;
- fallbackstrategie dekt unsafe scenes.

Anders geen M8 “true” implementatieclaim.

## Richtcommit

```text
render: prove structured SM64DS true widescreen
```
