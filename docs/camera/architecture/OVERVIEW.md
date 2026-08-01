# Smooth camera architecture

```text
AYN Thor right stick
        │
        ▼
SmoothCameraMapping (pure Kotlin)
        │ normalized X/Y
        ▼
InputProcessor profile-owned path
        │
        ▼
MelonEmulator.setSlot2CameraState(...)
        │ JNI
        ▼
MelonDSAndroid interface
        │ synchronized core call
        ▼
CartAnalog mode-2 register bank
        │ NDS reads 0x09000200
        ▼
SM64DS EU guarded runtime patch
        │ updates camera yaw target
        ▼
Original camera easing/collision/position solver
```

## Authority boundaries

Frontend owns:

- physical device and axis selection;
- deadzone, inversion and response curve;
- sensitivity selection;
- R3 edge sequence;
- lifecycle neutralization.

Core owns:

- stable emulated register representation;
- clamping and protocol constants;
- thread-safe state;
- no game-specific camera behavior.

Game patch owns:

- camera-state gating;
- per-update yaw integration;
- recenter semantics;
- retaining the original camera solver.

Renderer owns nothing about camera movement.
