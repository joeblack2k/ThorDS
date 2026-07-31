# True Widescreen-acceptatietests

## Geometrie

### W-01 World aspect

Een bekend rond/kwadraat 3D-object behoudt verhouding tussen 4:3 en 16:9.

### W-02 Horizontal FOV

16:9 toont aantoonbaar extra wereld links/rechts, niet crop.

### W-03 Culling

Objecten in nieuwe side regions blijven zichtbaar en correct geanimeerd.

### W-04 HUD circle

Power meter of synthetische cirkel:

```text
abs(width/height - 1) <= 0.02
```

### W-05 Glyphs

Fontglyph aspectverschil tegen 4:3-reference ≤ 2%.

### W-06 Bottom

Map/UI bottom aspectverschil ≤ 2%.

## Scenes

Minimaal:

- logo/title;
- file select;
- castle grounds;
- castle lobby;
- BOB start;
- Chain Chomp;
- mountain;
- boss;
- cannon;
- star collect;
- pause;
- course select;
- water;
- slide;
- interior;
- fog/effects;
- cap/flying;
- Bowser;
- minigame.

Per scène:

- classification;
- layer capture;
- final;
- fallback;
- no flicker.

## Temporal

### W-20 Transition

World→pause→world, painting entry, star collect.

Pass:

- no one-frame stretch flash;
- no wrong previous frame;
- classifier stable.

### W-21 Screen swap

True WS follows DS top source, bottom remains correct.

### W-22 Sleep/resume

No stale frame or pipeline loss.

## Layer integrity

- 3D-only capture coherent;
- UI-only capture has expected HUD;
- protected black preserved;
- capture-backed 3D preserved;
- no holes;
- no duplicated overlay;
- brightness/effects correct.

## Renderer

- Vulkan True WS.
- OpenGL says unavailable and uses 4:3.
- Safe mode 4:3.
- Anamorphic developer mode is not labeled True.

## Performance

Within `implementation/PERFORMANCE_BUDGETS.md`.

## Failure policy

Een enkele unsafe scene mag tijdelijk 4:3 fallback gebruiken wanneer:

- transition is smooth;
- UI states this only in diagnostics;
- scene matrix records it.

Feature fails als normale gameplay voortdurend fallbackt of assets alsnog vervormen.
