# ARM — cadence and counter patch skeleton

This is symbolic. Luna must use the actual EU disassembly and live registers.

## Developer cadence clamp

```asm
    .syntax unified
    .arm

    .equ CADENCE_ADDR, 0x0208EE44

force_cadence_one:
    ldr     r0, =CADENCE_ADDR
    mov     r1, #1
    str     r1, [r0]
    bx      lr
```

A direct AR data write is preferred for the first probe.

## Game-side semantic counter fallback

```asm
    .syntax unified
    .arm

    .equ COUNTER_ADDR, 0xDEADBEEF   @ prove and replace

semantic_behavior_hook:
    stmdb   sp!, {{r0-r3, lr}}
    ldr     r0, =COUNTER_ADDR
    ldr     r1, [r0]
    add     r1, r1, #1
    str     r1, [r0]
    ldmia   sp!, {{r0-r3, lr}}

    @ replay exact overwritten instructions here

    ldr     pc, =RETURN_ADDR
```

## Product timing hook

Do not write this until the consumer inventory is complete.

Possible responsibilities:

```text
- enforce effective cadence 1 for ordinary gameplay
- preserve already-60 scenes
- maintain a parity/fractional phase
- expose a diagnostic counter
- execute corrected fixed-step paths
```

## Code-cave rule

Do not assume `0x023E8000` is free merely because older PAL cheats use it.
Prove memory ownership in the exact EU runtime and every tested scene.
