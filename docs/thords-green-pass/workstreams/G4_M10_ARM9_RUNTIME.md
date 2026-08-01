# G4 — M10 actual ARM9 overclock runtime

## Goal

Move from `PLUMBING_ONLY` to a genuine per-ratio native capability.

## Architecture first

Inspect the exact pinned core scheduler and write `docs/project/adr/ADR-arm9-overclock.md` before changing timing. Compare at least:

- rationally scaling ARM9 cycle cost/work budget while scheduler time remains DS-normal;
- granting additional ARM9 execution within each scheduler interval while ARM7/GPU/SPU/events keep normal time.

Choose the smallest strategy that preserves event ordering and deterministic debt accounting. Do not use fast-forward, global timestamp scaling or audio speed changes.

## Core publication

Core changes require a public reachable core fork and gitlink. Follow `contracts/CORE_FORK_POLICY.md`.

## Required path

1. explicit core ratio API and telemetry API;
2. config/model/parcel/JNI/native bridge;
3. reset-time application only;
4. rational accumulator/debt;
5. JIT/interpreter and cache handling;
6. 100% equivalence;
7. test 125%, then 150%, 175%, 200% in order;
8. expose only passing ratios;
9. save-state ratio metadata and mismatch guard;
10. Hardcore and safe mode force 100%;
11. generic DS smoke.

## Green definition

The feature is green when:

- 100% equivalence passes;
- at least 125% produces measurably more ARM9 work in normal DS wall time;
- 10-minute drift ≤0.1%;
- audio/RTC/ARM7/IPC/GPU remain correct;
- ratio survives clean relaunch;
- incompatible state load is blocked safely.

Higher ratios are optional per-ratio capabilities, not required lies.

## Suggested commits

```text
core: add rational ARM9 overclock runtime
core: validate guarded ARM9 ratios on Thor
```
