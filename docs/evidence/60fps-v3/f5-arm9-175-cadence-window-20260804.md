# F5 ARM9 175 Percent Cadence Window

Status: PASS for the bounded 175 percent headroom checkpoint

## Runtime

- Device: AYN Thor
- ROM: exact EU ASMP revision 0
- RA hash: `ba3c4052e00c5cc31df5d5534c39de1b`
- Renderer: Vulkan
- ARM9 requested/effective ratio: `175%`
- ARM7: normal
- cadence probe: developer-only

## Observed windows

After the ratio relaunch, repeated one-second windows reported:

```text
uniqueUpdates=60-61
emulatorFrames=60-61
cadence=1
```

One transient window reported `31` updates during the relaunch boundary. The
following stable windows returned to `60-61` updates/s. The device remained
active. No `FATAL EXCEPTION` or `ANR in` entry was found.

ARM9 telemetry at the end of the window:

```json
{
  "percent": 175,
  "remainder": 25,
  "baseCycles": 521407490,
  "scaledCycles": 297874392,
  "sysTimestamp": 359903424,
  "arm9Timestamp": 719806848,
  "arm9Target": 719806848,
  "arm7Timestamp": 359903428,
  "frameCount": 642
}
```

The ARM7 and ARM9 timestamps remained aligned for this checkpoint.

## Limits

This proves a bounded 175% ARM9 headroom and cadence checkpoint. It does not
prove the source-level 60 FPS product patch, timing parity, audio parity, full
stress coverage, or final ARM9 ratio selection.
