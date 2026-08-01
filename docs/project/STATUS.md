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

- Current workstream: G2 / M7 deterministic Castle Garden proof closeout complete
- Current gate: bounded G2 validation, commit and publication
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

Run the bounded G2 publication gate, commit and push M7, then begin G3/M8:
replace developer-only widescreen semantics with the exact-profile product
mode defined by `ADR-true-widescreen.md`. Keep the owner's physical
top-orientation and playable-framerate witness separate from captured
compositor output.
