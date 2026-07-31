# Project status

Project: ThorDS Enhanced
Date: 2026-07-31
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
  build pass; the developer APK installs on the physical Thor and submits
  separate world-3D and plane-1/UI draws through the compositor fallback over
  a 16:9 top target without a crash
- Active blocker: a valid BOB scene matrix and HUD/transition measurements;
  Android input injection does not advance this game flow, so physical
  Thor-controller gameplay is required

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
| M7 | IN_PROGRESS | | docs/evidence/m7/ | EU aspect literals semantically mapped; guarded developer AR code and 16:9 world/4:3 UI-safe-area compositor geometry verified; no BOB/FOV/culling or M8 claim |
| M8 | NOT_STARTED | | | |
| M9 | NOT_STARTED | | | |
| M10 | NOT_STARTED | | | |
| M11 | NOT_STARTED | | | |
| M12 | NOT_STARTED | | | |
| M13 | NOT_STARTED | | | |

## Feature status

- Analog: IMPLEMENTED_PENDING_GAMEPLAY
- True Widescreen: SPIKE_IN_PROGRESS_NO_GO_FOR_M8
- RA Casual: NOT_STARTED
- RA Hardcore gate: NOT_STARTED
- ARM9 OC: NOT_STARTED
- 60fps: RESEARCH_ONLY
- Thor GUI: NOT_STARTED
- Stability: NOT_STARTED

## Next concrete action

Run the M7 developer probe through a valid local-ROM launch path, measure the
scene matrix without publishing private captures, and keep M8 blocked until
that evidence is green. The M6 gameplay checklist also remains pending.
