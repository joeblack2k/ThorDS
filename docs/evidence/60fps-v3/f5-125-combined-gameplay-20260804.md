# F5 125 Percent Combined Gameplay

Status: PASS for the bounded smoke gate

## Runtime

- Device: AYN Thor
- ROM: SM64DS EU ASMP revision 0
- ROM key: `asmp:0:ba3c4052e00c5cc31df5d5534c39de1b`
- Renderer: Vulkan
- ARM9 request: `125%`
- Cadence mode: developer-only probe enabled for this test

The ROM was launched first. The cadence probe was then enabled and completed
its relaunch. The ARM9 request was applied after that relaunch and completed a
second relaunch. Both commands returned `relaunched=1`.

The autonomous lower-screen flow then used:

1. `Adventure`;
2. `File A`;
3. direct `B` input.

## Live gameplay telemetry

Stable one-second windows reported:

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

ARM9 telemetry at the same live run reported:

```text
percent=125
remainder=50
baseCycles=1076918408
scaledCycles=861090694
sysTimestamp=876697354
arm9Timestamp=1753394709
arm9Target=1753394674
arm7Timestamp=876697354
frameCount=1565
```

No FATAL exception or ANR was observed. The developer cadence probe was
disabled after the run.

## Limits

This proves a stable combined 125% and 60 Hz semantic smoke run. It does not
prove ten-minute timing parity, all stress scenes, audio parity, or the final
product patch. The final ARM9 ratio remains unselected until the broader
matrix passes.
