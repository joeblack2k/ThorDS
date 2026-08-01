# SM64DS EU camera hook liveness

## Identity

- Game code: `ASMP`
- Revision: `0`
- RetroAchievements system hash: `ba3c4052e00c5cc31df5d5534c39de1b`
- Semantic source: `tangosdev/sm64ds-decomp`
- Semantic source pin: `2307f06d9ce10e114fa00d2e9318d5161aaed311`
- Orbit routine: `func_0200bb28` at runtime address `0x0200BB28`
- Decomp function size: `0x39c`

## Probe

The public core fork has a debug-only ARM9 JIT block-entry counter for the
exact runtime address. The counter is exposed through the existing redacted
SM64DS telemetry JSON and one-second debug log.

The probe is a compare-and-count at the JIT block-entry boundary. It does not
alter registers, memory, timing, input, rendering or game behavior.

## Thor run

APK variant: GitHub Prod Debug. The connected Thor launched the exact ASMP
profile without crash or ANR. The observed checkpoint was the SM64DS title
screen, not Castle Garden or another gameplay scene.

Observed windows:

```text
uniqueUpdates=60/61
emulatorFrames=60/61
cadence=1
stageTimer=0
cameraBehaviorCalls=0
```

## Boundary

This is not proof that the orbit routine is dead. The run did not reach a
normal gameplay camera state. A gameplay checkpoint or save-state-based run is
required before using this counter to select or reject the runtime patch hook.
No ROM bytes, ROM path, screenshots or device identifiers are part of this
evidence.
