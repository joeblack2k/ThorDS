# Enhanced Cadence Policy Device Check

Status: PARTIAL

## Runtime

- Exact ROM: SM64DS EU ASMP revision 0
- RA hash: `ba3c4052e00c5cc31df5d5534c39de1b`
- Device: AYN Thor
- Profile: normal Enhanced
- Developer cadence probe: disabled

The new debug APK was installed before the test. The autonomous two-screen
flow reached Yoshi castle gameplay:

1. inspect the upper title display;
2. inspect the lower title display;
3. touch `Adventure` on the lower display;
4. select File A on the lower display;
5. inspect upper live gameplay;
6. inspect the lower castle map.

## Semantic baseline

Live one-second windows reported:

| Field | Observed |
|---|---:|
| `cadence` | `2` |
| `uniqueUpdates` | `30-31` |
| `emulatorFrames` | `60-61` |
| `stageBehavior` | `30-31` |
| `stageRender` | `30-31` |
| `playerBehavior` | `30-31` |
| `actorPos` | `60-62` |
| `animationAdvance` | `150-155` |
| `vblank` | `60-61` |

## Decision

This is the expected normal Enhanced baseline. The result proves that
`60fps-dev-cadence` is no longer forced by normal Enhanced mode.

It does not prove the final 60 FPS product. The developer cadence probe remains
separate, disabled by default, and semantically unaccepted.
