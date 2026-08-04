# F3 Live Device Rerun

Status: PARTIAL

## Exact runtime

- Device: AYN Thor
- ROM: SM64DS EU ASMP revision 0
- Profile: Enhanced
- Semantic monitor: enabled
- Player pose interpolation: disabled

## Two-screen flow

The autonomous flow was completed without user input:

1. The upper display showed the title screen.
2. The lower display received the `Adventure` touch.
3. The lower display showed the save menu.
4. File A was selected on the lower display.
5. A second lower-display touch opened the live game scene.
6. The upper display showed Yoshi in the castle grounds scene.
7. The lower display showed the castle map.

This confirms that title and save validation must inspect both touch displays.

## Semantic windows

The live one-second windows reported:

| Field | Observed range |
|---|---:|
| `mainLoop` | 60-61 |
| `cadenceRender` | 60-61 |
| `stageBehavior` | 60-61 |
| `stageRender` | 60-61 |
| `playerBehavior` | 60-61 |
| `actorPos` | 60-61 |
| `animationAdvance` | 299-305 |
| `particleUpdate` | 30-31 |
| `vblank` | 60-61 |

The windows were approximately one second long. This is direct semantic
execution telemetry in a live gameplay scene. It is not a display FPS claim.

## Direct player input

A direct debug `B` press was sent. The player telemetry remained live and
reported changing player animation frames, transform hashes and vertical
motion values. The input used one button only. No crouch or button sequence was
used.

The pose fields reported:

- `playerAnimationCurrFrameQ12`: changing values;
- `playerAnimationBaseFrame`: changing values;
- `playerAnimationNextFrame`: changing values;
- `playerAnimationOutputTransformHash`: equal to the base transform hash;
- `playerInterpolationExecutionCount`: `0`.

This proves live player motion and telemetry reachability. It does not prove
pose interpolation. The interpolation feature remains developer-only and
disabled.

## Runtime safety

The captured log contained no `FATAL EXCEPTION` and no `ANR in` entry.

## Acceptance

F3 has stronger live semantic evidence after the autonomous two-screen route.
F4 remains open because the timing patch, pose interpolation and full product
parity matrix are not accepted.
