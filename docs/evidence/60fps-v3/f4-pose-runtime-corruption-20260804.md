# F4 Pose Runtime Corruption

Date: 2026-08-04
Status: FAIL

## Device

- Package: `io.github.joeblack2k.thords.dev`
- Device: AYN Thor, serial `6b0af897`
- ROM: ASMP revision 0
- ROM key: `asmp:0:ba3c4052e00c5cc31df5d5534c39de1b`

## Procedure

1. Built the debug APK from the current feature branch.
2. Installed the APK on the device.
3. Confirmed normal Enhanced first launched with three curated codes:
   Analog, Smooth Orbit Camera, and True Widescreen.
4. Enabled the pose setting through the developer command.
5. Relaunched the exact ROM.
6. Captured both upper and lower displays.

## Result

The profile log showed four curated codes, including the developer-only pose
code. The live semantic monitor reported:

```text
uniqueUpdates=60
cadenceValue=1
playerPoseInterpolationExecutionCount=0
```

Both displays then showed black output and corrupted noise. No player pose
execution, fractional frame, or transform interpolation was proven.

The pose setting was disabled again. Normal Enhanced relaunched with three
curated codes and no pose code.

## Acceptance

F4 remains red. The pose enhancement must stay disabled and developer-only.
