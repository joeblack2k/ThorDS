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
- Latest verified result: the G1 code/harness slice passed on the exact
  GitHubProdDebug APK. The production Android route handled 64/64 samples
  across 16 directions and four magnitudes, the radial deadzone boundary,
  D-pad-plus-camera ownership, neutral return and a live input-pipeline
  recreation. Same-checkpoint Castle Garden trials measured game response for
  low, mid and full movement plus the right-stick camera. Original relaunched
  with zero curated codes/addon/camera; Enhanced relaunched with exactly one
  curated analog code plus addon and camera.
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
| M7 | PARTIAL | 54410dc0 | docs/evidence/m7/ | EU aspect literals semantically mapped; guarded developer AR code, newline-safe parser, capture-safe primary-only probe, final-primary fallback and dual-UV presentation, title fallback plus intro and Castle Garden classifier matrices verified; save-state-assisted Castle Garden route, direct-path checkpoint restore, native-versus-probe world/FOV comparison, measured game projection response, Thor-default world plus packed-UI rotation, readable pause overlay, 10-second `FPS: 60` movement smoke, corrected 180-sample live bursts, a 120-frame fixed Castle Garden movement burst, a clean 30-frame exact-step final-primary side sequence, a measured native `packedBottomPrimary` raster, a direct final-primary world-pause-world transition and a short opaque/non-black transition soak recorded; W-20 is PARTIAL with longer no-flash/soak coverage open; W-01 is PARTIAL after paired non-black final-primary captures and a 0.39% diagnostic object-mask ratio delta, while formal round/square and controlled-camera evidence remain open; W-03 is PARTIAL because side geometry moves coherently in the exact primary sequence but no single identifiable side landmark is tracked wholly within one side ROI; W-06 is PARTIAL because native bottom geometry is measured but final secondary-display placement remains open; exact HUD/glyph geometry remains open |
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

Continue with G2's renderer-internal Castle Garden measurement layer while G1's
physical swim/fly/slide witness is blocked. Add bounded presenter metadata and
an offline analyzer for W-01/W-03/W-04/W-05/W-06/W-20, then use the result to
repair the reported top-screen 180-degree defect and distinguish game cadence
from presenter cadence. Do not collect another host-polled screenshot burst.
