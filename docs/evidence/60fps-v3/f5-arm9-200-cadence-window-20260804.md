# F5 ARM9 200 Percent Cadence Window

Status: PASS for the bounded 200 percent headroom checkpoint

## Runtime

- Device: AYN Thor
- ROM: exact EU ASMP revision 0
- RA hash: `ba3c4052e00c5cc31df5d5534c39de1b`
- Renderer: Vulkan
- ARM9 requested/effective ratio: `200%`
- ARM7: normal
- cadence probe: developer-only

## Observed windows

After the ratio relaunch, the first windows included relaunch settling values of
`54`, `61`, `50`, and `40`. Stable gameplay windows then reported:

```text
uniqueUpdates=59-61
emulatorFrames=60-61
cadence=1
```

The device remained active. No `FATAL EXCEPTION` or `ANR in` entry was found.

ARM9 telemetry at the end of the window:

```json
{
  "percent": 200,
  "remainder": 100,
  "baseCycles": 542006239,
  "scaledCycles": 270910851,
  "sysTimestamp": 360101521,
  "arm9Timestamp": 720203042,
  "arm9Target": 720203028,
  "arm7Timestamp": 360101521,
  "frameCount": 642
}
```

The ARM7 and ARM9 timestamps remained aligned for this checkpoint.

## Limits

This proves a bounded 200% ARM9 headroom and cadence checkpoint. It does not
prove the source-level 60 FPS product patch, timing parity, audio parity, full
stress coverage, or final ARM9 ratio selection.
