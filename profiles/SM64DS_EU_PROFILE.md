# Profiel: Super Mario 64 DS — Thor Enhanced

## Identiteit

```text
profile id:       sm64ds.eu.thor-enhanced
profile version:  1
system:           Nintendo DS
game code:        ASMP
revision:         0 (verifiëren)
RA game ID:       9983
RA system hash:   ba3c4052e00c5cc31df5d5534c39de1b
save group:       sm64ds-eu-retail
```

## Defaults

| Enhancement | Default | Status v0.1 |
|---|---:|---|
| Slot-2 analog | aan | vereist |
| Right-stick camera | aan | vereist |
| True Widescreen | developer probe | M7 diagnostic; production blocked |
| Vulkan | voorkeur | vereist voor True WS |
| High internal resolution | geteste Thorwaarde | vereist |
| ARM9 overclock | 100% | foundation |
| 60fps | uit/verborgen | volgende doel |
| RA | user global/first prompt | user choice |
| Hardcore | niet in Enhanced | geblokkeerd |

## Required capabilities

```text
NDS_EMULATION
THOR_DUAL_DISPLAY or generic dual layout
SLOT2_ANALOG
ACTION_REPLAY
VULKAN_STRUCTURED_COMPOSITOR for True WS
RA_INTEGRATION optional
```

## Runtimecodes

1. `sm64ds.eu.am64ds-analog.v1`
2. `sm64ds.eu.aspect-16x9.dev.v1` (default uit; alleen expliciete
   debuggable M7-probe)
3. eventuele bewezen HUD/cullingcorrecties als afzonderlijke IDs.

Iedere code:

- conditional;
- exact EU;
- hash;
- provenance;
- toggle;
- conflict met Hardcore.

## Display

### Thor top

```text
DS source: top
mode: TRUE_WIDESCREEN_3D_UI_SAFE
world: full physical viewport
UI: centered 4:3 safe rect
```

### Thor bottom

```text
DS source: bottom
mode: FIT_4_3
touch: enabled
soft controls: hidden
```

## Input

- left analog → Slot-2 analog;
- right analog → D-pad camera;
- physical D-pad → camera alternative;
- touch → bottom screen;
- face/shoulder/start/select → semantic DS mapping;
- no per-profile library key based on filename.

## RA

### Off

No integration.

### Casual

Allowed with all v0.1 enhancements. UI shows active list.

### Hardcore

Not allowed in this profile. Offer switch to `original.sm64ds.eu` and full restart.

## Save

Runtime-only v0.1 shares standard `.sav` with Original.

Before first Enhanced launch:

- existing save backup;
- no format conversion;
- source/save paths logged redacted.

## Fallbacks

| Failure | Fallback |
|---|---|
| analog code precondition | digital controls, warning |
| Slot-2 unavailable | digital controls |
| Vulkan unavailable | Native 4:3 |
| aspect code mismatch | Native 4:3 |
| ambiguous compositor scene | scene-specific 4:3 |
| RA offline | offline queue/general offline |
| OC unsupported | 100% |
| second display missing | normal single-display layout |

## User-visible summary

```text
Thor Enhanced
Analog Controls • True Widescreen
RetroAchievements: Casual
Hardcore requires Original
```
