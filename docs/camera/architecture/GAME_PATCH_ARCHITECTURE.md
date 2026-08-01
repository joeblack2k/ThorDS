# Game patch architecture

## Target

The target is the recovered European normal orbit routine:

```text
func_0200bb28 @ 0x0200BB28
```

## Desired semantic replacement

Legacy behavior:

```c
if (left) {
    yawOffset -= 0x2000;
} else if (right) {
    yawOffset += 0x2000;
}
repeatTimer = 0x14;
```

Enhanced smooth behavior:

```c
if (smoothProtocolValid() && smoothAllowed() && yawInputQ12 != 0) {
    int delta = (yawInputQ12 * yawUnitsPerTick) >> 12;
    yawOffset = (s16)(yawOffset + delta);
    cameraFlags &= ~0x60; // do not retain digital turn lockout
} else {
    runOriginalDigitalCameraBlock();
}
```

The original code after this block must still:

- approach current yaw toward base yaw + offset;
- calculate orbit vector;
- position the camera;
- process height;
- process collision and line-of-sight;
- clear temporary camera flags.

## Patch placement decision

Preferred order:

1. **In-place replacement** of the full legacy digital-yaw block if it has enough verified instructions.
2. **Guarded trampoline** to a verified region if in-place code cannot preserve fallback/gating.
3. Never use per-frame external camera-position writes.

## Guarding

Every entry patch must verify exact original words.

The patch must also check protocol magic, version and flags every time it runs. Invalid state falls through to original behavior.

## Relaunch

Switching between Original and Enhanced requires a full ROM restart so main/overlay code is reloaded cleanly.
