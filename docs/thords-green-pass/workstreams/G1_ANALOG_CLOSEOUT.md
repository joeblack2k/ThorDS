# G1 — Analog closeout

## Existing implementation

The exact EU profile, AM64DS code, Slot-2 addon, radial mapping and right-stick hysteresis already exist.

## Missing proof

The old checklist leaves walk/run/sneak, swim/fly/slide, right-stick gameplay response and reconnect unwitnessed.

## Required fix

Do not rely solely on a human statement. Extend the debug harness to exercise the actual post-Android input path in deterministic steps:

- left-stick angle sweep at 16 or more directions;
- magnitudes near 0.25, 0.50, 0.75 and 1.00;
- neutral return and deadzone jitter;
- simultaneous physical-D-pad state plus right-stick camera state;
- reconnect/recreate the controller input pipeline;
- Original/Enhanced relaunch.

Use exact emulator-frame stepping or timestamped runtime traces and a known controllable checkpoint. Demonstrate game motion/camera response, not only values accepted by JNI.

## Pass

- continuous response at multiple magnitudes;
- no 8-way-only snapping introduced by the frontend;
- no stuck camera direction;
- D-pad and camera source ownership correct;
- Original has no stale patch/addon/overlay;
- app relaunch and controller pipeline recreation pass;
- existing general controller tests stay green.

## Suggested commit

```text
input: close SM64DS analog end-to-end acceptance
```
