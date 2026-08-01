# Performance telemetry contract

## Common sample

Every benchmark sample should include:

```text
timestamp/frame id
scene/checkpoint
profile and active enhancements
requested/effective ARM9 ratio
presented FPS
unique game updates/s
frame deadline misses
CPU/GPU timing where available
audio underruns
classifier mode/fallback count
thermal status
```

## Comparisons

Use the same checkpoint, deterministic input and capture duration for:

- native 4:3 versus True Widescreen;
- 100% versus each exposed OC ratio;
- 30fps versus 60fps mode;
- individual features versus combined profile.

## Evidence output

Commit aggregate CSV/JSON with no ROM/save/account/device serial data. Raw traces and captures may remain private/local.
