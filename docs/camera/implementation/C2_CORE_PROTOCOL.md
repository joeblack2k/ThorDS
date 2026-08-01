# C2 — core protocol

## Core data

Suggested private state in `CartAnalog`:

```cpp
float CameraX = 0.0f;
float CameraY = 0.0f;
u16 CameraYawUnitsPerTick = 0;
u16 CameraRecenterSequence = 0;
u16 CameraFlags = 0;
```

## Setter

Prefer an explicit API over abusing float input IDs:

```cpp
bool GBACartSlot::SetAnalogCameraState(
    float x,
    float y,
    u16 yawUnitsPerTick,
    u16 flags,
    u16 recenterSequence
) noexcept;
```

It returns false when the inserted cart is not `CartAnalog`.

## Register reads

Mode 2 returns the protocol table.

## Synchronization

Use the same emulator lock/thread boundary used by existing `setSlot2AnalogInput`. Do not introduce a data race between Android input and the emulation thread.

## Reset

`CartAnalog::Reset()` clears all camera state.

## Savestates

Do not append camera fields to the existing savestate section in v1. The frontend/session bridge must neutralize transient state around state operations.

## Tests

- exact Q12 endpoints;
- clamp;
- magic/version;
- flags;
- sequence;
- mode 0/1 unchanged;
- reset;
- wrong cart fails.
