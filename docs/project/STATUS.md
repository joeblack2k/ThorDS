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

- Current workstream: G1 / M6 analog closeout
- Current gate: deterministic post-Android analog and physical gameplay proof
- Overall status: PARTIAL
- Latest verified result: G0 reconciled the live checkout with the Green Pass
  dossier. `HEAD` exactly matches the documented `a2aa88e5` baseline,
  `origin/main` and public default `main` match that commit, `upstream` remains
  the pinned MelonDualDS source, all submodules are initialized, and the core
  commit is reachable from its public upstream branch. The 40-file dossier
  manifest is intact. The local ASMP ROM remains ignored, identity-matched and
  unchanged. The connected AYN Thor exposes the expected top and lower
  physical displays. Android rotation metadata does not close the owner's
  reported 180-degree top-screen rendering defect, which remains an open
  physical product gate.
- Active blocker: M6 has implemented exact-profile Slot-2 analog and camera
  mapping, but still lacks deterministic post-Android sweeps plus witnessed
  walk/run/sneak, swim/fly/slide, right-stick, reconnect and Original/Enhanced
  relaunch behavior.
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
| M6 | PARTIAL | 883d00cf | docs/evidence/m6/ | exact profile, runtime payload, Slot-2 activation and safe-mode fallback verified; deterministic post-Android and physical gameplay scenarios remain |
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

Close G1 first. Extend the existing debug harness through the actual
post-Android analog path, run deterministic direction/magnitude/deadzone,
D-pad-plus-camera, pipeline recreation and Original/Enhanced relaunch checks,
then witness the required physical gameplay scenarios from the saved
checkpoint. Do not claim G1 from JNI-value acceptance alone. Once G1 is
`PASS_VALIDATED`, continue directly with G2's renderer-internal Castle Garden
measurement layer. The reported top-screen 180-degree defect and low observed
performance remain explicit physical gates throughout both workstreams.
