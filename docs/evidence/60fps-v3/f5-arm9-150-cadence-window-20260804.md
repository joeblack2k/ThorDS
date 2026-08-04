# F5 ARM9 150 Percent Cadence Window

Status: PASS for the bounded 150 percent headroom checkpoint

## Runtime

- Device: AYN Thor
- ROM: exact EU ASMP revision 0
- RA hash: `ba3c4052e00c5cc31df5d5534c39de1b`
- Renderer: Vulkan
- ARM9 requested/effective ratio: `150%`
- ARM7: normal
- cadence probe: developer-only

## Observed windows

Repeated one-second windows reported:

```text
uniqueUpdates=59-61
emulatorFrames=60-61
cadence=1
```

The run remained active during the window. No `FATAL EXCEPTION` or `ANR in`
entry was found in the collected log.

ARM9 telemetry at the end of the window:

```json
{
  "percent": 150,
  "remainder": 100,
  "baseCycles": 658796611,
  "scaledCycles": 438956131,
  "sysTimestamp": 517560489,
  "arm9Timestamp": 1035120980,
  "arm9Target": 1035121106,
  "arm7Timestamp": 517560489,
  "frameCount": 923
}
```

The ARM7 and ARM9 timestamps remained aligned for this checkpoint.

## Limits

This proves a bounded 150% ARM9 headroom and cadence checkpoint. It does not
prove the source-level 60 FPS product patch, timing parity, audio parity, full
stress coverage, or final ARM9 ratio selection.
