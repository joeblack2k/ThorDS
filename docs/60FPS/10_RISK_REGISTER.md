# Risk register

| Risk | Symptom | Mitigation |
|---|---|---|
| Main-loop counter misread | 60 in Original and Enhanced | use scene-specific baselines + semantic callbacks |
| Wrong JIT hook | zero counters | observe actual dispatch, then game-side fallback |
| Cadence race | mode flips/reset | patch writes/initializers and log every change |
| Fixed-step logic | enemies too fast | full consumer inventory and parity replay |
| Insufficient ARM9 | slow motion | validate lowest sustaining OC ratio |
| Overclock as fast-forward | audio/time speedup | keep DS clock domains normal |
| Scene already 60 | doubled title/menu | scene/cadence gates |
| Code cave collision | crash/corruption | prove occupancy and exact guards |
| Overlay reload | patch disappears | overlay-aware guarded reapplication |
| Save-state mismatch | drift/crash | timing+OC metadata gate |
| RA policy drift | Hardcore enhanced | pre-bootstrap immutable policy |
| Performance telemetry overhead | false slowdown | exact-profile/debug gating |
| Community patch misinformation | wrong EU port | source-derived patch and corrected provenance |
| Private-data leak | ROM/state in Git | staged/history scans and private fixtures |
