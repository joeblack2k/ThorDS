# C3 — JNI and session bridge

## Kotlin API

Suggested shape:

```kotlin
external fun setSlot2CameraState(
    yaw: Float,
    pitch: Float,
    yawUnitsPerTick: Int,
    flags: Int,
    recenterSequence: Int,
): Boolean
```

A Boolean result makes fail-closed behavior observable.

## JNI

Add a JNI method beside `setSlot2AnalogInput`. Validate integer ranges before narrowing to `u16`.

## Native interface

Reuse current NDS/cart ownership and synchronization.

## Session initialization

Before the game starts:

- Original/Safe Mode: send disabled neutral state.
- Enhanced Smooth Orbit: send enabled neutral state with effective configuration.

## State load

Required order:

```text
send neutral
load state
send effective neutral
wait for next physical event
```

## Teardown

Before cart ejection or emulator stop:

```text
flags = 0
x = 0
y = 0
```

## Diagnostics

Expose the requested/effective camera state in the existing session-status/debug system.
