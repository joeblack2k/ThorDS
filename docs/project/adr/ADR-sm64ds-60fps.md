# ADR: SM64DS 60fps implementation route

Date: 2026-08-01
Status: Accepted for M13 research

## Decision

Use a guarded, exact-ROM runtime patch as the first implementation route.
Prefer the existing Action Replay path because it already applies curated
codes at the ARM7 VBlank boundary without changing the pinned emulator core.

The 60fps mode remains hidden or experimental until the complete M13 matrix is
green. A presenter rate, `NDS::RunFrame()` count, or VBlank count alone is not
gameplay proof.

## Evidence boundary

The current repository does not contain a proven European `ASMP` 60fps code,
patched test copy, or binary diff. No code words or guessed addresses are
added to the profile until a legal local reference is compared against the
unchanged ROM and mapped to the decomp audit.

## Timing boundaries

The measurement harness must report these independently:

- presenter frames: Android/Vulkan presentation;
- emulated DS frames: `NDS::RunFrame()`;
- hardware VBlank: `GPU::StartScanline()` at `VCount == 192`;
- ARM7 VBlank delivery: `ARM::TriggerIRQ()`;
- semantic SM64DS updates: a proven game-side counter or equivalent memory
  marker;
- wall-clock, game timer, physics/animation cadence and audio continuity.

## Consequences

- No frontend-only 60fps toggle is implemented.
- No community binary or patched ROM is committed or distributed.
- If the binary/decomp map proves that Action Replay cannot express the
  required changes, make a separate public `melonDS-android-lib` core commit
  under `CORE_FORK_POLICY.md` before changing `NDS.cpp`, `GPU.cpp`, `ARM.cpp`
  or `AREngine.cpp`.
- A validated implementation must pass the exact EU profile, normal timing,
  stress-scene, save/relaunch, RetroAchievements Casual and 60-minute
  stability gates.

