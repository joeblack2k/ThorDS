# Project status

Project: ThorDS Enhanced
Date: 2026-08-01
Agent: Sol xHigh
Branch: thords/enhancement-platform-v1
Origin: https://github.com/joeblack2k/ThorDS
Base: MelonDualDS 0.7.0.rc5
APK: app/build/outputs/apk/gitHubProd/debug/app-gitHub-prod-debug.apk
Thor connected: yes
ROM identity: ASMP / revision 0 / RA ba3c4052e00c5cc31df5d5534c39de1b

## Summary

- Current workstream: M13 timing validation and SM64DS Smooth Orbit Camera v1
- Current gate: semantic 60fps/product validation remains open; the generic
  camera runtime hook is now hardware-validated
- ~~Original/safe-mode software rendering as a play path~~ — explicitly out
  of scope for ThorDS Enhanced product validation. The product path is Vulkan.
- Overall status: PARTIAL
- Latest verified result: M7 is `PASS`. W-01/W-03 use named Castle Garden
  geometry across exact frame sequences; W-04/W-05 prove top UI-safe geometry;
  W-06 proves the physical lower 4:3/touch path; W-20 combines the green
  world-pause-world trace with a 384-frame key-door cinematic/scene transition.
  That transition had 192 distinct game hashes, no black/alpha/rect failures,
  no stale hash reappearance and `62.6346%` final-primary endpoint change.
- Separate physical witness: the exact APK produced an upright `1920x1080`
  machine capture and renderer FPS telemetry, but the owner's earlier
  observation of an inverted physical top panel and approximately 20fps play
  is not overruled by automation. M6 swim/fly/slide and usable-play witness
  remain open and must be rechecked with the owner.
- M11 now has a bounded read-only session-status slice: a ROM launch latches
  profile integrity, effective ARM9 percentage and effective
  RetroAchievements mode, and the pause menu displays that snapshot without
  recomputing policy. The real Thor pause menu showed `Enhanced`, `100%` and
  `Off` after a fresh ROM launch and process restart. Full M11 remains open
  because the firmware physical branch and broader release gates are not
  complete. Evidence: `docs/evidence/m11/session-status-pause.txt`.
- M9 is now `PASS`: the owner verified a real RetroAchievements unlock during
  normal gameplay on the Thor with the enhanced product active. Policy,
  launch-gating and physical Casual submission evidence are recorded in
  `docs/evidence/m9/`.

## Milestone status

| M | Status | Commit | Evidence | Notes |
|---|---|---|---|---|
| M0 | PASS | 0ad4589 / f758855 | docs/evidence/m0/ | rc5 pin, submodules, ROM protection and synthetic identity test verified |
| M1 | PASS | 5c41182 | docs/evidence/m1/ | build/install/ASMP boot/10-minute smoke green; two-panel behavior is the M2 scope |
| M2 | PASS | 812765e / e4cb944 | docs/evidence/m2/ | physical roles, exact DS touch grid, controller and lifecycle pass; private captures excluded from public history |
| M3 | PASS | m3-product-identity | docs/evidence/m3/ | public source, identity, updater isolation, defaults, safe mode and offline notices verified |
| M4 | PASS | 04ed45bd | docs/evidence/m4/ | catalog v1, exact resolver, safe Original fallback, curated/user separation and synthetic delta patches |
| M5 | PASS | profile: add exact Super Mario 64 DS Europe profile | docs/evidence/m5/ | exact ASMP/revision/hash profiles; runtime code deferred to M6 |
| M6 | PARTIAL | input: harden and trace Slot-2 analog lifecycle | docs/evidence/m6/ | deterministic post-Android sweep, gameplay response, ownership, lifecycle neutralization, pipeline recreation and Original/Enhanced relaunch pass; swim/fly/slide and usable physical play remain |
| M7 | PASS | 84ae990d + current G2 closeout | docs/evidence/m7/ | W-01, W-02, W-03, W-04, W-05, W-06 and W-20 pass with deterministic Castle Garden geometry, physical lower-display and continuous transition evidence |
| M8 | PASS | 30fdad53 | docs/evidence/m8/ | exact-profile True Widescreen product path, layer-aware fallback, safe-mode/relaunch behavior, build and Thor smoke validated; no per-frame 16:9/4:3 flicker observed after the session-lock correction |
| M9 | PASS | b9893fe4 | docs/evidence/m9/ | policy foundation, fail-closed launch gate and real Thor RetroAchievements unlock validated; inherited legacy-history bounds remain documented |
| M10 | PARTIAL | ad3a5179 | docs/evidence/m10/ | guarded native/JNI config, scheduler snapshots, persisted preference fail-closed check, incompatible-ratio savestate guard, 100% telemetry and stability are green; 64-bit scaled-cycle truncation fixed; equivalence, drift and formal over-100 runtime gates remain open |
| M11 | PARTIAL | 760f3c2b | docs/evidence/m11/ | ROM-details resolved ThorDS profile status, per-ROM Original/Enhanced and RA controls, physical Hardcore recovery branches and staged-edit process recreation pass; ~~safe-mode acceptance~~ is out of scope; full physical details-flow acceptance remains open |
| M12 | BLOCKED | docs: record M12 release preflight matrix | docs/evidence/m12/ | inventory only; release remains blocked by incomplete product and device gates |
| M13 | PARTIAL | arm9 profile/state-load gate | docs/evidence/m13/ | F1 monitor live; ARM9 125% relaunch and state 8 load pass; gameplay cadence and exact 60fps remain open |

## Feature status

- Analog: PARTIAL
- True Widescreen: PARTIAL
- RA Casual: PASS
- RA Hardcore gate: PARTIAL
- ARM9 OC: EXPERIMENTAL / 125% relaunch and compatible state load pass; timing validation remains open
- 60fps: REQUIRED / IMPLEMENTATION AND VALIDATION OPEN
- ~~Safe mode / software renderer gameplay~~: OUT OF SCOPE / Vulkan is the
  supported ThorDS Enhanced product renderer
- SM64DS Smooth Orbit Camera v1: PARTIAL / frontend mapping, R3 sequence,
  JNI bridge, transient Slot-2 protocol and exact-profile EU generic camera
  hook are implemented and Thor-validated; HUD/tutorial-arrow suppression,
  sound-path proof and the broader physical acceptance matrix remain open.
- F4 generic camera hook is validated on the generic `0x0208715C` camera mode:
  61 unique updates, 9,907 protocol reads and a non-zero game-side yaw offset,
  with no crash or ANR. Evidence:
  `docs/evidence/m13/f4-generic-camera-hook.json`.
- M13 research now records the EU game-side VBlank cadence hook, the unresolved
  scene vtable dispatch and the timer/animation dependency boundary. No
  unverified 60fps code is shipped; implementation and full timing validation
  remain open. Evidence:
  `docs/evidence/m13/timing/core-hook-audit.txt`,
  `docs/research/sm64ds-60fps-decomp-map.md`,
  `docs/evidence/m13/timing/eu-overlay-original-hashes.txt`,
  `docs/evidence/m13/timing/game-loop-counter-instrumentation.txt`.
- The cadence scanner now separates 192 scheduler, render/OAM, scene-update,
  boot/init, timer, message/HUD and scene-specific consumers with no generic
  `unresolved-function` findings. F2 remains open for semantic/manual review
  of those classified consumers; it is not claimed PASS.
- The 18 source-level cadence writes were reviewed as initialization or
  scene/overlay-transition assignments and none was promoted to an F4 patch
  manifest. Binary/runtime consumer matching remains required.
- F2 source review now maps `func_020199A4` at `0x020199A4` as the primary
  cadence scheduler boundary, with timer, Stage behavior, HUD and OAM
  consumers downstream. Runtime entry-count correlation against a 30 FPS
  baseline is the next F2 experiment.
- Route-A runtime telemetry now confirms the active scene path through equal
  Stage Behavior, Stage Render and VBlank counts (`376/376/376`) on the green
  checkpoint. `MainLoopSlot1` was zero in this state and is not used as sole
  proof. Evidence:
  `docs/evidence/m13/f2-semantic-runtime-route.json`.
- F3 now has a hidden exact-profile `60fps-dev-cadence` Action Replay definition
  with default-off and relaunch-required semantics. It is experimental only;
  no product or 60fps validation claim is made.
- F3 wiring is now Thor-verified: the debug-only preference action survives
  launch planning and adds the fourth curated runtime code; the probe was
  disabled again after the check. Evidence:
  `docs/evidence/m13/f3-cadence-probe-activation.json`.
- F5 now records a bounded 60-second timing window with the green generic
  camera hook active: 48 of 49 observed windows reported 60/61 updates/sec,
  all cadence values were `1`, and no crash or ANR occurred. The debug probe
  was disabled again after measurement. This does not close the semantic
  60fps product gate. Evidence:
  `docs/evidence/m13/f5-60fps-stability-window-camera-green.json`.
- The existing native one-second game-loop sampler is now exposed through the
  debug-only `DUMP_SM64DS_GAME_LOOP` action. Title-screen validation returned
  `valid=false`, correctly preventing a menu from becoming gameplay evidence.
- M13 now also contains a timed, read-only sampler for the mapped EU main-loop
  counter. It reports one-second `uniqueUpdates` windows for runtime
  validation; this is not yet 60fps acceptance evidence.
- Thor GUI: PARTIAL
- Stability: NOT_STARTED

## Next concrete action

Continue F1/F2 with the loaded Vulkan state: capture gameplay semantic counters
and classify cadence consumers. 60fps remains required; this ARM9 gate is only
an experimental timing prerequisite, not 60fps acceptance. ~~Do not spend
product-gate time on Original/safe-mode software rendering.~~
M8 and M9 are complete for the current product gate; do not restart either
validation workstream. Over-100% ARM9 behavior remains disabled until timing
evidence proves it safe. Remaining independent gaps, including M6 physical
movement coverage and the later M11-M12 release gates, remain explicitly
tracked above.
## M13 / F1 semantic execution telemetry

- Core monitor is implemented at the ARM9 JIT block-dispatch boundary and the
  interpreter instruction boundary.
- The debug-only enable/dump bridge is built and installed on the AYN Thor.
- Title-screen run: VBlank handler delta `67` in approximately one second;
  semantic gameplay targets remained zero.
- This is valid monitor-liveness evidence, not 60 FPS gameplay evidence.
- Evidence: `docs/evidence/m13/f1-thor-semantic-run.json`.
- F1 gameplay validation and F2 cadence-consumer classification remain open.
