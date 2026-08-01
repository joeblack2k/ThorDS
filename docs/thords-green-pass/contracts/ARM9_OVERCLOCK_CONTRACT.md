# ARM9 overclock contract

## Semantics

ARM9 overclock means additional ARM9 execution capacity while normal Nintendo DS scheduler time, VBlank, ARM7, GPU, SPU, RTC and audio time remain unchanged.

It is not:

- fast-forward;
- global clock multiplication;
- frame limiter bypass;
- presenter pacing change.

## API

Prefer a small explicit core API:

```cpp
SetArm9OverclockRatio(numerator, denominator)
GetArm9OverclockTelemetry()
```

Use integer/rational math with fractional debt. Apply at instance creation/reset, not halfway through a session.

## Capability

```text
UNSUPPORTED
PLUMBING_ONLY
EXPERIMENTAL_125
EXPERIMENTAL_125_150
VALIDATED_<ratios>
```

UI lists only passing ratios.

## Telemetry

At minimum:

- requested/effective ratio;
- ARM9 work/cycles/instructions;
- scheduler target and debt;
- ARM7 delta;
- GPU/GXFIFO stalls;
- frame deadline misses;
- audio underruns;
- wall-clock/game-clock drift.

## State and policy

- Hardcore and safe mode force 100%;
- ratio change requires relaunch;
- save state records ratio metadata;
- mismatch rejects safely;
- SRAM remains compatible;
- 100% path remains equivalent with instrumentation enabled.
