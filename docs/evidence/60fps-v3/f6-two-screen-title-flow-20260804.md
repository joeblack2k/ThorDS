# F6 Two-Screen Title Flow

Status: PASS for the display-flow witness only

## Runtime

- Device: AYN Thor
- ROM: SM64DS EU ASMP revision 0
- ROM key: `asmp:0:ba3c4052e00c5cc31df5d5534c39de1b`
- Renderer: Vulkan
- Cadence probe: developer-only and enabled for the gameplay telemetry
- Product status: not validated

## Title inspection

The autonomous run inspected both physical displays before input:

1. The upper display showed the SM64DS title screen with `Touch To Start`.
2. The lower display showed the SM64DS touch menu with `Adventure`.
3. The lower display received the `Adventure` touch.
4. The lower display received the `File A` touch.
5. The live game scene started.
6. A direct `B` input was sent for the player motion check.

The title gate therefore checks both screens. It does not check only the upper
display.

## Semantic windows

After the game scene was active, the native semantic monitor reported:

| Field | Observed |
| --- | ---: |
| `mainLoop` | 60-61 |
| `cadenceRender` | 60-61 |
| `stageBehavior` | 60-61 |
| `stageRender` | 60-61 |
| `playerBehavior` | 60-61 |
| `actorPos` | 60-61 |
| `animationAdvance` | 300-305 |
| `particleUpdate` | 30-31 |
| `vblank` | 60-61 |

This is semantic execution telemetry. It is not a rendered-FPS claim and it
does not prove timing parity.

## Result

The two-screen title and save flow passed in an autonomous device run. The
developer cadence probe was disabled after the run. The final 60 FPS product
gate remains open.
