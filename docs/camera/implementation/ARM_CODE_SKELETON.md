# ARM code skeleton

This is symbolic. Luna must replace placeholders only after an EU disassembly and register-liveness proof.

```asm
    .syntax unified
    .arm

    .equ SLOT2_CAM_BASE,      0x09000200
    .equ SLOT2_YAW_Q12,       0x00
    .equ SLOT2_YAW_PER_TICK,  0x04
    .equ SLOT2_RECENTER_SEQ,  0x06
    .equ SLOT2_MAGIC,         0x08
    .equ SLOT2_VERSION,       0x0A
    .equ SLOT2_FLAGS,         0x0C

    .equ PROTOCOL_MAGIC,      0x5343
    .equ PROTOCOL_VERSION,    1
    .equ FLAG_ENABLED,        1

    .equ CAM_FLAGS,           0x154
    .equ CAM_YAW_OFFSET,      0x184
    .equ CAM_RECENTER_TARGET, 0x19E
    .equ CAM_RECENTER_ACTIVE, 0x1A0
    .equ CAM_PLAYER,          0x110
    .equ PLAYER_YAW,          0x8E

smooth_camera_entry:
    @ rCam is a placeholder. Determine the real live camera register.
    ldr     rProto, =SLOT2_CAM_BASE

    ldrh    rTmp, [rProto, #SLOT2_MAGIC]
    ldr     rExpected, =PROTOCOL_MAGIC
    cmp     rTmp, rExpected
    bne     legacy_camera

    ldrh    rTmp, [rProto, #SLOT2_VERSION]
    cmp     rTmp, #PROTOCOL_VERSION
    bne     legacy_camera

    ldrh    rFlags, [rProto, #SLOT2_FLAGS]
    tst     rFlags, #FLAG_ENABLED
    beq     legacy_camera

    @ Apply additional normal-state/script gates here.

    ldrsh   rInput, [rProto, #SLOT2_YAW_Q12]
    cmp     rInput, #0
    beq     legacy_camera

    ldrh    rScale, [rProto, #SLOT2_YAW_PER_TICK]
    smulbb  rDelta, rInput, rScale
    mov     rDelta, rDelta, asr #12

    ldrsh   rYaw, [rCam, #CAM_YAW_OFFSET]
    add     rYaw, rYaw, rDelta
    strh    rYaw, [rCam, #CAM_YAW_OFFSET]

    ldr     rTmp, [rCam, #CAM_FLAGS]
    bic     rTmp, rTmp, #0x60
    str     rTmp, [rCam, #CAM_FLAGS]

    b       after_legacy_camera_block

legacy_camera:
    @ Execute or branch to the exact preserved original block.
    b       original_legacy_camera_block

after_legacy_camera_block:
    @ Resume immediately before the original yaw ApproachLinear call.
```

## Recenter

The sequence path may share the same replacement block or a small entry hook.

Required semantics:

```text
if currentSequence != lastSequence:
    lastSequence = currentSequence
    camera.recenterTarget = player.yaw + 0x8000
    camera.recenterActive = 1
    optionally call func_02012790(0x1A)
```

Preserve all live registers and condition flags required by the original continuation.

## Do not assume

- that the camera pointer is in `r0`;
- that literals fit immediate encodings;
- that a source block maps one-to-one to the current compiler output;
- that a zero RAM range is safe.
