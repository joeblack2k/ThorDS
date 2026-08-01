# Project status

Project: ThorDS Enhanced
Date: 2026-08-01
Agent: Luna Xhigh
Branch: thords/enhancement-platform-v1
Origin: https://github.com/joeblack2k/ThorDS
Base: MelonDualDS 0.7.0.rc5
APK: app/build/outputs/apk/gitHubProd/debug/app-gitHub-prod-debug.apk
Thor connected: yes
ROM identity: ASMP / revision 0 / RA ba3c4052e00c5cc31df5d5534c39de1b

## Summary

- Current milestone: M7
- Current gate: developer-only structured Vulkan widescreen spike
- Overall status: IN_PROGRESS
- Latest verified result: M7 SPIR-V synchronization and GitHub Prod debug
  build pass; the developer APK installs on the physical Thor, falls back
  through three measured title phases to a centered 1440x1080 4:3 top
  rectangle, and selects the dual-UV world-safe diagnostic route for a
  measured Peach-intro and controllable castle-grounds top-3D scene without a
  ThorDS crash or ANR. A final `screencap` confirms the 1920x1080 primary
  surface uses 240-pixel fallback sidebars and, at a structured 3D checkpoint,
  issues separate full-width-world and 4:3-safe-overlay draw modes; the
  Lakitu transition produced the expected dual-UV/fallback/dual-UV sequence,
  while live debug scene metadata remains capture-safe during title
  transitions. Temporary app-private save states now resume castle grounds
  and the castle-entrance approach without replaying the title or intro, and
  physical Touch Mode movement was observed after ThorDS foreground recovery.
  The primary Vulkan rotation is now the product default for the detected
  `AYN / AYN Thor` device, and the packed DS plane-1 path uses the matching
  native top-down sample orientation. A fresh physical Castle Garden
  checkpoint shows readable `CASTLE SECRET STARS` and `TOUCH TO SELECT` text
  on the primary surface while the world remains upright and the lower
  presentation remains unchanged. The normal native composite path was then
  checked separately: its centered 4:3 world and pause menu are also upright
  and readable, so the Thor rotation is no longer applied twice to an already
  oriented composite frame. A 10-second Slot-2 movement smoke still reports
  `FPS: 60` with no ThorDS crash or ANR, and a pause-to-world sample showed no
  wrong previous frame. A same-checkpoint native-versus-probe comparison also
  showed extra left/right castle-ground world content outside the native 4:3
  rectangle. The Castle Garden camera probe also proves that the game
  projection matrix changes from `x=0x20F8` to `x=0x18BA` with the y-scale
  unchanged when the live camera aspect changes from `0x1555` to `0x1C72`.
  This is a strong projection, presentation and scene-level performance
  result, not yet object-aspect, side-region culling, exact HUD/glyph/bottom
  metrics or sustained transition proof. A fresh 180-sample Castle Garden
  live metadata burst kept the world-safe classifier stable for all populated
  samples. The bounded debug-capture warmup fix was then rebuilt, installed and
  rerun: 180 unique contiguous frame ids (`20..199`) were captured without a
  classifier change, crash or ANR. This is still not a game transition proof.
- Active blocker: castle-grounds object-aspect, side-region culling, exact
  HUD/glyph/bottom geometry and transition measurements. The owner explicitly
  selected controllable castle grounds as the M7 representative scene;
  Bob-omb Battlefield is no longer an M7 exit gate.

## Milestone status

| M | Status | Commit | Evidence | Notes |
|---|---|---|---|---|
| M0 | PASS | 0ad4589 / f758855 | docs/evidence/m0/ | rc5 pin, submodules, ROM protection and synthetic identity test verified |
| M1 | PASS | 5c41182 | docs/evidence/m1/ | build/install/ASMP boot/10-minute smoke green; two-panel behavior is the M2 scope |
| M2 | PASS | 812765e / e4cb944 | docs/evidence/m2/ | physical roles, exact DS touch grid, controller and lifecycle pass; private captures excluded from public history |
| M3 | PASS | m3-product-identity | docs/evidence/m3/ | public source, identity, updater isolation, defaults, safe mode and offline notices verified |
| M4 | PASS | 04ed45bd | docs/evidence/m4/ | catalog v1, exact resolver, safe Original fallback, curated/user separation and synthetic delta patches |
| M5 | PASS | profile: add exact Super Mario 64 DS Europe profile | docs/evidence/m5/ | exact ASMP/revision/hash profiles; runtime code deferred to M6 |
| M6 | IN_PROGRESS | 883d00cf | docs/evidence/m6/ | exact profile, runtime payload, Slot-2 activation and safe-mode fallback verified; physical gameplay scenarios remain |
| M7 | IN_PROGRESS | 54410dc0 | docs/evidence/m7/ | EU aspect literals semantically mapped; guarded developer AR code, newline-safe parser, capture-safe primary-only probe, final-primary fallback and dual-UV presentation, title fallback plus intro and Castle Garden classifier matrices verified; save-state-assisted Castle Garden route, native-versus-probe world/FOV comparison, measured game projection response, Thor-default world plus packed-UI rotation, readable pause overlay, 10-second `FPS: 60` movement smoke and corrected 180-sample live bursts recorded; W-20 is PARTIAL with no observed pause transition and repeated adjacent frames; W-01 is BLOCKED by black renderer layers and unproven checkpoint restore; side-region culling and exact HUD/glyph/bottom geometry remain open |
| M8 | NOT_STARTED | | | |
| M9 | IN_PROGRESS | policy-foundation + M9.2 launch-policy wiring | docs/evidence/m9/ | Android-free policy foundation, unsupported-mode fail-closed handling and pre-bootstrap launch gate verified; native/network/offline validation and full M9 acceptance remain open; publication is PASS_WITH_KNOWN_LEGACY_HISTORY |
| M10 | NOT_STARTED | | | |
| M11 | NOT_STARTED | | | |
| M12 | NOT_STARTED | | | |
| M13 | NOT_STARTED | | | |

## Feature status

- Analog: IMPLEMENTED_PENDING_GAMEPLAY
- True Widescreen: SPIKE_IN_PROGRESS_NO_GO_FOR_M8
- RA Casual: POLICY_WIRED_PRE_BOOTSTRAP
- RA Hardcore gate: POLICY_WIRED_PRE_BOOTSTRAP
- ARM9 OC: NOT_STARTED
- 60fps: RESEARCH_ONLY
- Thor GUI: NOT_STARTED
- Stability: NOT_STARTED

## Next concrete action

Keep the remaining M7 Castle Garden measurements on the critical path:
object aspect, new-side-region culling, exact HUD/glyph/bottom geometry and
longer transition stability. The M9.2 policy gate is now wired into launch
planning after the existing session-integrity contract was mapped. Unsupported
requested RA modes and launch-decision exceptions fail closed before
emulator/native bootstrap; keep native/network/offline and physical RA
acceptance separate from this bounded evidence. The live classifier now has
unique contiguous frames, but that is not itself a game transition proof.
W-20 Castle Garden world-to-pause-to-world is PARTIAL: the live cadence is
stable, but no pause transition was observed and repeated adjacent frames
remain. W-01 object aspect is BLOCKED for this checkpoint because both
renderer layers were black and the state restore was not proven. The next
useful M7 action is to obtain a valid Castle Garden renderer checkpoint before
retrying W-01; keep M8 and full M9 acceptance blocked until remaining evidence
is green.
The M6 gameplay checklist also remains pending.
