# Risk register

| Risk | Consequence | Control |
|---|---|---|
| Evidence treadmill | weeks of partial screenshots without product code | two-attempt rule; add instrumentation |
| Wrong physical display ID | capture proves another surface | query SurfaceFlinger and window ownership each run |
| AYN anti-retention overlay | invalid full-screen sample | detect/dismiss and discard affected captures |
| Developer probe leaks to product | normal user receives unstable mode | explicit product enum/capability and release tests |
| 2D classified as world | stretched HUD/menu | conservative classifier, hysteresis and 4:3 fallback |
| Core local-only commit | public superproject cannot clone/build | public core fork and reachable gitlink |
| OC alters wall clock | fast game/audio/timer drift | normal scheduler time, rational ARM9 work budget, telemetry |
| OC breaks JIT/state | crashes or nondeterminism | reset-only ratio change, cache/state tests |
| 60fps duplicates frames | false 60fps claim | internal game-update counter, not presenter FPS |
| 60fps doubles physics | unplayable game | decomp mapping and wall-clock/physics tests |
| Runtime AR modifies RA hash flow | set not recognized or policy confusion | identify original ROM before runtime code; no hash spoof |
| Casual/Hardcore confusion | invalid sessions or user deception | fail-closed policy and effective-state UI |
| UI writes unsupported state | requested setting appears active when not | render resolved plan, not raw toggle |
| Private evidence committed | privacy/legal problem | gitignored raw evidence and pre-push scans |
| General DS regressions | fork becomes game-only accidentally | unknown-ROM, non-Thor and generic DS smoke |
