# F5 125 Percent Reuse-Launch Gameplay

Status: PASS for the bounded autonomous runtime gate

## Runtime

- Device: AYN Thor
- ROM: SM64DS EU ASMP revision 0
- ROM key: `asmp:0:ba3c4052e00c5cc31df5d5534c39de1b`
- Renderer: Vulkan
- ARM9 request: `125%`
- Cadence mode: developer-only probe

The active identical ROM was reused without opening the emulator confirmation
dialog. Cadence and ARM9 settings then completed their own relaunches.

The lower-screen input flow sent:

1. `Adventure`;
2. `File A`.

## Stable gameplay windows

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

ARM9 telemetry reported:

```text
percent=125
remainder=75
baseCycles=1404345047
scaledCycles=1122778343
sysTimestamp=1178639760
arm9Timestamp=2357279520
arm9Target=2357279520
arm7Timestamp=1178639760
frameCount=2104
```

No FATAL exception or ANR was observed. The developer cadence probe was
disabled after the run.

## Limits

This is a bounded runtime result. It does not prove timing parity, audio
parity, all stress scenes, or the final product patch.
