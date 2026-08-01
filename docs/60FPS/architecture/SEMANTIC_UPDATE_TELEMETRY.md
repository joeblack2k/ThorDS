# Semantic update telemetry

## Why existing counters are insufficient

`data_020A0DB0` increments once per main loop, but a scene that already uses
cadence 1 produces ~60 samples in Original. Therefore the signal must be paired
with scene identity and behavior/render counters.

## Required counters

One-second windows:

```text
wallNs
ndsFrames
vblanks
mainLoopIterations
slot1Callbacks
cadenceRenderCallbacks
lagCallbacks
stageBehavior
stageRender
entryBehavior
entryRender
cadenceValue
arm9Percent
arm9Targets/cycles
presentedFrames
uniqueGameStateHashes
```

## Counter lifecycle

Reset the measurement window on:

- ROM boot;
- profile relaunch;
- scene counter decrease/reset;
- state load;
- pause/unpause boundary;
- invalid/unknown identity.

Do not treat a descending game counter as unsigned wrap without evidence.

## Scene labels

Every sample must include a stable checkpoint label supplied by the debug
harness, not inferred from a screenshot.

## Performance

Telemetry is exact-profile and debug/validation gated. The hot-path check must
be a cheap unlikely branch.
