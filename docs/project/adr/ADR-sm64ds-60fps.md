# ADR: SM64DS 60fps implementation route

Date: 2026-08-01
Last updated: 2026-08-02
Status: Accepted; implementation under M13 validation

## Decision

Use a guarded, exact-ROM runtime patch as the first implementation route.
Prefer the existing Action Replay path because it already applies curated
codes at the ARM7 VBlank boundary without changing the pinned emulator core.

The 60fps mode is a required M13 product and release gate. It must not be
reported as complete or enabled for release until the complete M13 matrix is
green, but it must remain an active implementation target rather than an
explicitly disabled product status. A presenter rate, `NDS::RunFrame()` count,
or VBlank count alone is not gameplay proof.

## Evidence boundary

The repository now contains the reviewed v6 exact-original guarded European
`ASMP` runtime patch for the game cadence plus player movement, player timers
and the Player-specific animation driver at `0x020BEDD4`, with continuation
`0x020BEDD8`. Its 248-byte player payload and 48-byte animation payload share
one fail-closed region with six guards (four player timestep hooks, the Player
animation driver and cadence original 2). The v6 Action Replay and profile
code SHA-256 are both
`4155b9ef2c9de2688f05ab06a9845cd69a20a49f1c919db8b9ae9c113426deea`.
Global `Animation::Advance` is not patched. Its public tooling emits only
Action Replay words and hashes; the local reference image remains ignored and
unpublished. The profile entry is still experimental and default-off until the
complete product matrix is green.

Same-checkpoint Thor evidence accepts the bounded player correction: horizontal
movement is 435.0 versus 457.5 (5.17%), timer decay is 61 versus 60 and Player
animation advance is identical. Two 60fps half-steps match one original
vertical step exactly while rising and within 0.000977 world units while
falling. The final hardware animation window records four enhanced updates
and frame delta 8192, equal to two original updates at 4096 each. The unsafe
disable-without-relaunch path was deleted; cadence preference changes relaunch.
This does not establish correct timing for particles, non-player actors,
cutscenes, audio continuity, broad gameplay behavior, RA Casual or 60-minute
stability.

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

- No frontend-only 60fps toggle is accepted as the implementation.
- No community binary or patched ROM is committed or distributed.
- If the binary/decomp map proves that Action Replay cannot express the
  required changes, make a separate public `melonDS-android-lib` core commit
  under `CORE_FORK_POLICY.md` before changing `NDS.cpp`, `GPU.cpp`, `ARM.cpp`
  or `AREngine.cpp`.
- A validated implementation must pass the exact EU profile, normal timing,
  stress-scene, save/relaunch, RetroAchievements Casual and 60-minute
  stability gates.
