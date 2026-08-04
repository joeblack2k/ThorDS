# F4 Pose Execution Counter Disabled Check

Status: PASS for telemetry wiring and fail-closed behavior

## Runtime

- Device: AYN Thor
- ROM: exact EU ASMP revision 0
- Renderer: Vulkan
- pose interpolation payload: disabled
- semantic monitor: enabled

The rebuilt APK reported normal semantic monitor activity:

```text
mainLoop=60-61
cadenceRender=60-61
vblank=60-61
```

The independent pose entry counter reported:

```json
"playerPoseInterpolationExecutionCount": 0
```

The complete semantic dump also contained the new counter as the final array
entry. The monitor remained active and no crash or ANR was observed.

## Conclusion

The pose execution counter is wired to the generated payload entry at
`0x02004DF8` and remains zero when the payload is disabled. This does not
prove interpolation. It provides the required fail-closed measurement before
any developer-only payload activation.
