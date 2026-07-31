# Europese SM64DS 16:9-patch afleiden

## Doel

Een runtimepatch genereren die de 3D-projectie en relevante clipping van de Europese ROM naar 16:9 zet.

## Bekende waarden

SM64DS gebruikt Fix12:

```text
4:3  = round((4/3) × 4096) ≈ 0x1555
16:9 = round((16/9) × 4096) ≈ 0x1C72
```

De decomp benoemt een `Clipper.aspectRatio` en initialiseert deze met `0x1555`.

## Amerikaanse referentie — niet shippen

De Reddit-post gebruikt voor een Amerikaanse 1.1-achtige variant:

```text
9200D030 00001555
1200D030 00001C72
D2000000 00000000

9210C490 00001555
1210C490 00001C72
D2000000 00000000

920BB93C 00001555
120BB93C 00001C72
D2000000 00000000
```

Deze adressen zijn uitsluitend:

- bewijs dat meerdere sites nodig zijn;
- referentie voor functievergelijking;
- geen Europese patch.

## Derivatieprocedure

### 1. Decomp pin

```bash
git clone https://github.com/tangosdev/sm64ds-decomp.git tools/research/sm64ds-decomp
cd tools/research/sm64ds-decomp
git checkout 2d38fe9b825199deec408240849b64b91c965d85
```

ROM/toolchainbestanden blijven gitignored.

### 2. Semantic sites

Zoek en classificeer:

- `Clipper` constructor;
- `Initialise3dGraphics`;
- view/camera object initialization;
- overlay-specific perspective setup;
- cutscene camera;
- star select/title paths;
- alle `0x1555` references.

### 3. Runtime mapping

Voor iedere gekozen site:

```text
symbol/function
ROM section/overlay
load address
runtime address
original 16/32-bit word
desired conditional write
scene coverage
```

### 4. Conditional code

Gebruik Action Replay check-before-write:

```text
if original value == 0x1555
    write 0x1C72
```

Voor complexere instruction changes:

- check original instruction;
- write exact replacement;
- fail closed.

### 5. Culling

Test dat objecten aan nieuwe zijkanten worden getekend. Als projectie breed is maar culling 4:3 blijft:

- identificeer frustum/cullingaspect;
- voeg separate patch;
- versioneer.

### 6. HUD

De aspectcode is niet verantwoordelijk voor 2D-correctie. Dat gebeurt in Vulkan. Alleen HUDelementen die in 3D blijken te zitten krijgen game-side correctie.

## Output

Productprofile krijgt:

```text
sm64ds.eu.aspect-16x9.v1
```

Met:

- canonical code;
- code SHA-256;
- original word evidence;
- decomp symbolmap;
- supported scenes;
- known unsafe scenes;
- provenance.

## Failure

Als een precondition faalt:

- geen gedeeltelijke patch;
- True Widescreen uit;
- Native 4:3;
- diagnostics noemt site/address mismatch.
