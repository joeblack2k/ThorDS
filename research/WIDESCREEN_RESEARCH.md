# Widescreenonderzoek

## Probleem

Een normale DS-game rendert 256×192, oftewel 4:3. Een game-side 16:9-aspectpatch comprimeert de bredere 3D-projectie in dat framebuffer. Wanneer het hele frame daarna naar 16:9 wordt gestretcht:

- de 3D-wereld ziet er correct uit;
- 2D-HUD en tekst worden te breed;
- bottom-screenassets kunnen eveneens vervormen.

Dat is anamorphic widescreen, geen True Widescreen.

## Beschikbare informatie in rc5

De Vulkanroute heeft afzonderlijk:

- live high-resolution 3D;
- previous-top 3D;
- previous-bottom 3D;
- packed top 2D/control;
- packed bottom 2D/control;
- capture-backed 3D;
- per-pixel structured metadata;
- per-lijn display/capturemetadata.

Belangrijke concepten uit de bestaande shader:

- 3D placeholder;
- 3D layer slot;
- structured 2D above plane;
- structured 2D-only;
- protected black;
- no-3D coverage;
- composition modes.

## Doeltransformaties

Voor een destinationpixel `(dx, dy)`:

### World UV

```text
worldU = dx / destinationWidth
worldV = dy / destinationHeight
```

Deze UV samplet de anamorf gecomprimeerde 3D-bron over de volledige 16:9-destination.

### UI UV

Bereken de 4:3-safe-area:

```text
safeWidth = min(destinationWidth, destinationHeight × 4/3)
safeLeft = (destinationWidth - safeWidth) / 2
```

Alleen binnen die rect:

```text
uiU = (dx - safeLeft) / safeWidth
uiV = dy / destinationHeight
```

De 2D-HUD wordt met `uiU/uiV` gesampled en behoudt aspect.

## Scèneklassen

1. **WORLD_3D_SAFE**
   - dominante 3D-slotdata;
   - 2D-overlay duidelijk identificeerbaar;
   - full-width world + safe UI.
2. **MENU_2D**
   - geen betekenisvolle 3D;
   - volledig 4:3.
3. **MIXED_CAPTURE_SAFE**
   - bestaande capture/previous-framepaden betrouwbaar;
   - full-width world toegestaan met geteste overlaypolicy.
4. **AMBIGUOUS**
   - metadata niet overtuigend;
   - 4:3 fallback.
5. **TRANSITION**
   - korte fade/swap;
   - behoud stabiele laatste veilige mode of 4:3.

## 2D-underlaypolicy

Niet alle 2D is HUD. Mogelijke underlays:

- achtergronden;
- fades;
- texturecaptures;
- menuvelden;
- sky/backgroundlagen.

Per profiel/scène:

```text
CENTER_4_3
STRETCH_UNDERLAY
EDGE_EXTEND
TILE_IF_SAFE
FALLBACK_4_3
```

Default voor onbekende 2D-underlay: `FALLBACK_4_3`.

## Aspectpatch

Game-side:

```text
4:3 Fix12  = 0x1555
16:9 Fix12 = 0x1C72
```

De productiecode voor Europa wordt lokaal afgeleid en conditioneel toegepast. De patch moet alle relevante camera-/clipperpaden dekken en side culling corrigeren.

## HUDcorrecties buiten compositor

Als een HUD-element deel van de 3D-bron blijkt te zijn, kan alleen UI-safe sampling het niet scheiden. Dan:

1. bewijs welk element;
2. zoek game-side drawfunctie in de decomp;
3. corrigeer diens projection/viewport/position;
4. versioneer die extra runtimepatch;
5. houd fallback beschikbaar.

## Meetbare acceptatie

- Een bekende cirkel in HUD heeft `|width/height - 1| <= 0.02`.
- Fontglyphbreedte/hoogte wijkt maximaal 2% af van 4:3-reference.
- Wereldobjecten hebben in 16:9 dezelfde lokale verhouding als 4:3.
- Horizontaal zichtveld neemt toe.
- Bottom-screenkaart blijft binnen 2%.
- Geen ontbrekende pixelregio groter dan afgesproken threshold.
- Scèneclassificatie flikkert niet.

## Niet voldoende

- alleen `0x1555→0x1C72`;
- full-frame stretch;
- een enkele Bob-omb Battlefield-screenshot;
- een handmatig custom layout;
- “het ziet er goed uit” zonder geometriechecks.
