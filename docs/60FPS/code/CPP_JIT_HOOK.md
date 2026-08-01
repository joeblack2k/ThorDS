# C++ — correct JIT/interpreter hooks

## JIT

In `ARMv5::Execute<CPUExecuteMode::JIT>()`:

```cpp
u32 instrAddr = R[15] - ((CPSR & 0x20) ? 2 : 4);

[[unlikely]] if (NDS.Sm64dsMonitor.IsEnabled())
    NDS.Sm64dsMonitor.ObserveArm9Pc(instrAddr);

if ((instrAddr < FastBlockLookupStart
        || instrAddr >= (FastBlockLookupStart + FastBlockLookupSize))
    && !NDS.JIT.SetupExecutableRegion(
        0,
        instrAddr,
        FastBlockLookup,
        FastBlockLookupStart,
        FastBlockLookupSize))
{
    // existing error path
}

JitBlockEntry block = NDS.JIT.LookUpBlock(
    0,
    FastBlockLookup,
    instrAddr - FastBlockLookupStart,
    instrAddr);

if (block)
    ARM_Dispatch(this, block);
else
    NDS.JIT.CompileBlock(this);
```

This observes the actual block dispatched by the CPU loop.

## Interpreter

Before executing the current instruction:

```cpp
const u32 instrAddr = R[15] - ((CPSR & 0x20) ? 2 : 4);
[[unlikely]] if (NDS.Sm64dsMonitor.IsEnabled())
    NDS.Sm64dsMonitor.ObserveArm9Pc(instrAddr);
```

Place it before pipeline advancement changes the visible PC.

## Compile membership diagnostic

Inside `ARMJIT::CompileBlock`, after `instrs[i].Addr` is set:

```cpp
if (NDS.Sm64dsMonitor.IsEnabled()
    && IsSm64dsSemanticTarget(instrs[i].Addr))
{
    Platform::Log(
        Platform::LogLevel::Debug,
        "SM64DS target inside JIT block block=%08X target=%08X index=%d",
        blockAddr,
        instrs[i].Addr,
        i);
}
```

Do not count this as execution. It only diagnoses a target that is not a block
start.
