# True Widescreen contract

## Meaning

```text
TRUE_WIDESCREEN_3D_UI_SAFE
```

requires:

- exact game-side 16:9 camera/clipper patch;
- full-width world 3D;
- centered 4:3 UI-safe sampling;
- correct culling in new horizontal regions;
- lower screen 4:3;
- 4:3 fallback for unsafe scenes.

## Geometry on 1920×1080

```text
world rect: 0,0,1920,1080
UI rect:    240,0,1440,1080
```

## Product modes

```text
NATIVE_4_3
TRUE_WIDESCREEN_3D_UI_SAFE
DIAGNOSTIC_ANAMORPHIC   developer only
```

A product mode is resolved from exact profile, renderer and device capabilities. Do not carry the old `developerWidescreenProbe` boolean as the user-facing architecture.

## Scene policy

```text
WORLD_3D_SAFE
WORLD_3D_CAPTURE_SAFE
MENU_2D
TRANSITION
AMBIGUOUS
```

Use hysteresis. Ambiguous/menu defaults to centered 4:3. Transition may hold the last safe mode only for a bounded, tested number of frames.

## 3D HUD exception

If a HUD element exists inside the 3D image, patch its game-side draw/projection or fall back. Do not hide distortion with a mask.
