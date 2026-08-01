# HUD arrows and camera sound root cause

## Permanent camera buttons

`HUD::RenderCameraButtons()` renders left/right camera sprites on both sides of the lower screen. This is original DS touch ergonomics.

The European overlay function begins at:

```text
0x020FC04C
```

AM64DS uses a guarded patch at:

```text
0x020FC0C0
expected E19100B0
replacement EA000070
```

This is intended to skip touch-camera rendering.

## Bouncing/tutorial arrows

`Stage::RenderBouncingArrows()` is a separate path. It can draw edge arrows around:

```text
x = 0x0C
x = 0xF4
```

Suppressing `RenderCameraButtons()` alone may therefore leave tutorial arrows visible.

## Required diagnosis

Instrument:

- call count of `HUD::RenderCameraButtons`;
- call count of `Stage::RenderBouncingArrows`;
- effective instruction at the AM64DS patch site after overlay load;
- camera input bits;
- recenter bit;
- sound-ID path.

## Correct fix

Enhanced:

- skip camera buttons;
- skip camera-specific bouncing tutorial arrows;
- leave non-camera HUD/tutorial behavior intact;
- remove the ordinary-yaw sound trigger.

Original:

- no changes.
