# Actual ARM9 JIT block-entry monitor

## Correct insertion point

In `ARMv5::Execute<CPUExecuteMode::JIT>()`, the emulator calculates:

```cpp
u32 instrAddr = R[15] - ((CPSR & 0x20) ? 2 : 4);
JitBlockEntry block = NDS.JIT.LookUpBlock(...);
if (block)
    ARM_Dispatch(this, block);
```

Observe `instrAddr` immediately before block lookup/dispatch.

This is different from:

- `MonitorARM9Jump()`;
- JIT branch-generation helpers;
- indirect-branch stubs.

Those previous hooks did not see the active path on Thor.

## Interpreter parity

Before executing an interpreted instruction, observe its actual instruction
address. Count only exact target PCs.

## Block containing target

If a function target is not a block start:

1. inspect JIT compile `FetchedInstr.Addr`;
2. mark/log blocks that contain the target;
3. add a narrowly generated runtime callback at that instruction;
4. do not approximate by counting the containing block unless control flow
   proves the target always executes.

## Gate

The monitor must be enabled only by the exact ASMP validation session.
