# Vulkan layer-compositorontwerp

## Bestaande shaderinformatie

Rc5 heeft in compositor/presenter:

```text
image3dInput
image3dPreviousTopInput
image3dPreviousBottomInput
TopPackedBuffer
BottomPackedBuffer
Capture3dBuffer
```

Packed control encodeert onder meer:

- compositiemode;
- 3D slot;
- 2D above;
- protected black;
- no 3D coverage;
- display/capture line metadata.

Voeg aan deze architectuur toe; bouw geen tweede losse renderer.

## Nieuwe configuratie

Breid runtime presentationconfig uit met bijvoorbeeld:

```text
presentationMode
worldRect
uiSafeRect
underlayPolicy
sceneClass
debugLayer
```

Kotlin/Parcelable/native push constants moeten consistent en getest zijn.

## Fragment-/computeconcept

Pseudo:

```glsl
worldCoord = mapDestinationToWorldSource(dst);
uiValid, uiCoord = mapDestinationToUiSource(dst, safeRect);

worldPixel = composeWorldAt(worldCoord);

if (scene == MENU_2D) {
    out = uiValid ? composeOriginalAt(uiCoord) : background;
} else if (scene is safe world) {
    underlay = composeUnderlay(worldCoord or policyCoord);
    world = compose3D(worldCoord, underlay);
    overlay = uiValid ? composeUiOverlay(uiCoord) : transparent;
    out = blend(world, overlay);
} else {
    out = composeOriginal4x3(uiCoord);
}
```

## Cruciaal: twee source-X’s

De bestaande compositor gebruikt vaak één DS source X voor alle lagen. True Widescreen heeft nodig:

```text
worldSourceX
uiSourceX
```

Controlmetadata voor de UI wordt met `uiSourceX` gelezen; 3D met `worldSourceX`.

## Structured overlay

Gebruik alleen bewezen overlaypixels:

- `hasStructured2DAbovePlane`;
- zichtbare 2D-kleur;
- juiste brightness/effect;
- alpha/composition volgens bestaande code.

Niet simplificeren tot “kleur 0 = transparant”; DS-effects en protected black moeten blijven werken.

## Previous/capture 3D

Behoud:

- frame ownership;
- screen swap;
- capture source validity;
- temporal history;
- fences.

True Widescreen mag geen oude 3D-bron uit de verkeerde fysieke displayrol gebruiken.

## Filtering

- 3D volgt rendererfilter/scaling.
- 2D UI gebruikt aspect-correcte filter.
- Test nearest, linear/Quilez waar beschikbaar.
- Geen bilinear bleeding over 4:3-safe-area-rand.
- Debugmaskers altijd nearest.

## Scene statistics API

Expose van native naar Android of interne logs:

```text
scene class
confidence
3d coverage
structured overlay coverage
capture state
fallback reason
```

Gebruik rate-limited logging.

## Shaderbuild

Rc5 genereert embedded SPIR-V. Na shaderwijziging:

```bash
./gradlew checkVulkanSpirv
./gradlew regenerateVulkanSpirv
git diff --check
```

Commit bronshader en gegenereerde headers samen.

## Unit/golden tests

Omdat echte gameassets niet in CI mogen:

- synthetische 3D gradient;
- ronde HUDcirkel;
- glyphgrid;
- 2D-only menu;
- protected black;
- capture transition;
- swapped screens.

Golden assertions meten geometrie en pixelrollen, niet Nintendo-art.

## Performancebudget

- geen extra volledige 1920×1080 intermediate wanneer push-constant dual sampling voldoende is;
- maximaal één extra pass alleen als noodzakelijk;
- GPU frame P95 binnen `testing/PERFORMANCE_BENCHMARKS.md`;
- geen per-frame allocation;
- pipelines vooraf compileren.
