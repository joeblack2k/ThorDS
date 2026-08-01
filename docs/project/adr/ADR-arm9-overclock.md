# ADR: Guarded ARM9 Overclock Runtime

Status: accepted for implementation; no ratio above 100% is validated yet.

## Context

The pinned melonDS core uses `ARM9Timestamp` as emulated DS time, not merely
as a local ARM9 instruction counter. `ARM9ClockShift` is part of the existing
DS/DSi clock model and is used by scheduler events, memory timings, DMA, GPU
timing and ARM7 catch-up. Scaling it to implement an Android "overclock"
would change event ordering and could become fast-forward.

## Options considered

### A. Scale ARM9 cycle costs or `ARM9ClockShift`

Rejected. This changes the meaning of the existing emulated timestamps and
would indirectly change ARM7, IRQ, DMA, GX FIFO, timers, GPU and audio
ordering. It cannot prove that only ARM9 receives extra work.

### B. Add an independent ARM9 execution budget

Chosen. Keep the existing timestamps and scheduler event boundaries as normal
DS time. Add an explicit rational ARM9 work budget and fractional debt. A
ratio above 100% may grant additional ARM9 execution slices only when the
normal scheduler interval has remaining ARM9 work budget and no architectural
synchronization boundary requires a stop.

The implementation must stop extra work before ARM9 side effects are executed
past the next event boundary. If the pinned execution path cannot provide that
boundary safely for both interpreter and JIT, the capability remains
`PLUMBING_ONLY` and the requested ratio resolves to effective 100%.

## Decision

- Public core API expresses a reset-time ratio as numerator/denominator and
  exposes read-only telemetry.
- Normal DS timestamps remain authoritative for ARM7, GPU, SPU, RTC, VBlank,
  timers, DMA and IPC.
- Ratio changes require session stop and relaunch; no live mutation.
- Save states include the effective ratio and debt compatibility metadata.
  Loading a state created with a different ratio is rejected without
  replacing the current state. SRAM remains independent and compatible.
- Hardcore, safe mode, unsupported core paths and scheduler anomalies force
  effective 100%.
- Only individually validated ratios become `EXPERIMENTAL` or `VALIDATED`
  capabilities. The initial target is 125%; 150%, 175% and 200% are not
  promised.

## Required evidence

Before enabling 125% on Thor, prove 100% equivalence, increased ARM9 work in
normal DS wall time, no more than 0.1% ten-minute time drift, correct ARM7/
IPC/GPU/DMA/timer/audio behavior, deterministic same-ratio save/load and
safe mismatch rejection. A renderer FPS counter is not sufficient evidence.
