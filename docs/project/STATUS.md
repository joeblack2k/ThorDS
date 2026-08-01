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

- Current workstream: M10 ARM9 overclock foundation
- Current gate: measurement-only ARM9 runtime work; over-100% remains disabled
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
| M11 | PARTIAL | 760f3c2b | docs/evidence/m11/ | ROM-details resolved ThorDS profile status, per-ROM Original/Enhanced and RA controls, and pre-launch Enhanced+Hardcore recovery choices compile and smoke green; full physical details-flow acceptance remains open |
| M12 | BLOCKED | docs: record M12 release preflight matrix | docs/evidence/m12/ | inventory only; release remains blocked by incomplete product and device gates |
| M13 | NOT_STARTED | | | |

## Feature status

- Analog: PARTIAL
- True Widescreen: PARTIAL
- RA Casual: PASS
- RA Hardcore gate: PARTIAL
- ARM9 OC: PARTIAL
- 60fps: REQUIRED / NOT_STARTED
- M13 research now records the real emulation/VBlank/Action Replay hook
  boundaries. No unverified 60fps code is shipped; implementation and full
  timing validation remain open. Evidence:
  `docs/evidence/m13/timing/core-hook-audit.txt`.
- Thor GUI: PARTIAL
- Stability: NOT_STARTED

## Next concrete action

Continue with the M11 profile-controls slice while M10 remains measurement-only,
then implement and validate the now-required M13 60fps product mode.
M8 and M9 are complete for the current product gate; do not restart either
validation workstream. Over-100% ARM9 behavior remains disabled until timing
evidence proves it safe. Remaining independent gaps, including M6 physical
movement coverage and the later M11-M12 release gates, remain explicitly
tracked above.
