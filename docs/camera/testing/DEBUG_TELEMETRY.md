# Debug telemetry

## Debug command

Add a bounded debug command such as:

```text
RUN_SMOOTH_CAMERA_SWEEP
```

It must use the same InputProcessor path as real controller events.

## Per-update schema

```json
{
  "frame": 0,
  "rawX": 0.0,
  "rawY": 0.0,
  "processedX": 0.0,
  "processedY": 0.0,
  "magic": 21315,
  "version": 1,
  "flags": 3,
  "yawUnitsPerTick": 1001,
  "recenterSequence": 0,
  "cameraState": "normal-orbit",
  "currentYaw": 0,
  "baseYaw": 0,
  "yawOffset": 0,
  "appliedDelta": 0,
  "legacyHeld": 0,
  "legacyPressed": 0,
  "cameraButtonDraws": 0,
  "bouncingArrowDraws": 0,
  "cameraSoundTriggers": 0
}
```

## Public evidence

Aggregate results and redact runtime addresses where unnecessary. Never include a ROM path, device serial or capture.
