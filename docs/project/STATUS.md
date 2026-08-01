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

- Current workstream: G2 / M7 deterministic Castle Garden proof closeout
- Current gate: renderer-internal geometry and physical display-role proof
- Overall status: PARTIAL
- Latest verified result: G2 now has a bounded presenter metadata trace and
  exact-step in-app PixelCopy for both physical surfaces. The corrected native
  single-frame primitive produced contiguous frame ids; the 192-record
  world-pause-world trace had no sequence gaps or frame regressions. W-06 is
  PASS with exact `256x192` to `1240x930` lower scaling, `0.0%` aspect error,
  rotation disabled and a green 3x3 touch grid. The current exact APK also
  produced an upright `1920x1080` top capture, but no owner-witnessed physical
  closure or stable 60fps claim is made.
- Active blocker: M6 still lacks witnessed swim/fly/slide and usable physical
  gameplay. The owner-observed 180-degree top-screen output and approximately
  20fps gameplay make that physical closeout impractical. G2 is independent
  source instrumentation needed to isolate the display and renderer defects.
- M11 now has a bounded read-only session-status slice: a ROM launch latches
  profile integrity, effective ARM9 percentage and effective
  RetroAchievements mode, and the pause menu displays that snapshot without
  recomputing policy. The real Thor pause menu showed `Enhanced`, `100%` and
  `Off` after a fresh ROM launch and process restart. Full M11 remains open
  because the firmware physical branch and broader release gates are not
  complete. Evidence: `docs/evidence/m11/session-status-pause.txt`.

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
| M7 | PARTIAL | 54410dc0 + current G2 slice | docs/evidence/m7/ | W-02 and W-06 pass. Bounded presenter and paired source/final PixelCopy instrumentation are green on Thor; world-pause-world internal trace passes. W-01 known controlled reference, W-03 named side landmark, top UI-safe W-04/W-05 and W-20 painting/star plus pixel black/stale classification remain open |
| M8 | NOT_STARTED | | | |
| M9 | PARTIAL | policy-foundation + M9.2 launch-policy wiring | docs/evidence/m9/ | Android-free policy foundation, unsupported-mode fail-closed handling and pre-bootstrap launch gate verified; native/network/offline validation and full M9 acceptance remain open; publication history has documented legacy bounds |
| M10 | PARTIAL | | docs/evidence/m10/ | ARM9 overclock policy foundation only; current plumbing resolves effective 100; no native/JNI/UI/runtime over-100 behavior |
| M11 | PARTIAL | feat: expose latched session status in pause menu | docs/evidence/m11/ | bounded read-only pause/session snapshot is tested and visible on Thor; firmware physical branch and full M11 acceptance remain open |
| M12 | BLOCKED | docs: record M12 release preflight matrix | docs/evidence/m12/ | inventory only; release remains blocked by incomplete product and device gates |
| M13 | NOT_STARTED | | | |

## Feature status

- Analog: PARTIAL
- True Widescreen: PARTIAL
- RA Casual: PARTIAL
- RA Hardcore gate: PARTIAL
- ARM9 OC: PARTIAL
- 60fps: NOT_STARTED
- Thor GUI: PARTIAL
- Stability: NOT_STARTED

## Next concrete action

Use the exact-step paired top-Surface sequence to close W-01 with a stable
reference and W-03 with one named Castle Garden side landmark. Capture the top
UI-safe plane for W-04/W-05, then run the same bounded surface path through one
painting/star transition for W-20. Keep the owner's physical top-orientation
and playable-framerate witness separate from captured compositor output.
