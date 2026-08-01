# Acceptance matrix

| ID | Feature | Required pass condition | Minimum evidence |
|---|---|---|---|
| G0-01 | Baseline | current HEAD is baseline descendant; clean/reconciled worktree; device connected | status snapshot, commands, no reset |
| A-01 | Analog path | exact Enhanced launch activates one curated AM64DS code and Slot-2 analog | plan diagnostics + device log |
| A-02 | Magnitude | at least four nonzero magnitudes and 16 directions produce continuous processed vectors | machine-readable sweep |
| A-03 | Game response | controlled analog sweep changes player motion continuously without 8-way-only behavior | exact-step capture/trace |
| A-04 | Camera | right stick produces press/release camera response with no stuck direction and no physical D-pad conflict | input/event trace + gameplay evidence |
| A-05 | Original | Original launch has no curated code, no Slot-2 analog and no stale camera overlay | relaunch trace |
| W-01 | Object aspect | known calibration/reference geometry differs by at most 2% between 4:3 and 16:9 | deterministic bbox/ratio JSON |
| W-02 | FOV | 16:9 exposes extra left/right world, not crop | aligned reference metrics |
| W-03 | Culling | one identified side-region landmark remains visible and animates across exact consecutive frames | tracked bbox/center series |
| W-04 | HUD circle | power meter or calibration circle ratio error ≤2% | mask/bbox metrics |
| W-05 | Glyphs | selected glyph aspect difference ≤2% from 4:3 reference | OCR-free glyph mask metrics |
| W-06 | Bottom | physical lower final rect, orientation and aspect within 2%; touch remains exact | SurfaceFlinger capture metrics + touch grid |
| W-20 | Transitions | continuous internal capture shows no stretch/black/stale flash through world↔pause and painting/star transitions | renderer-timestamped frame series |
| W-30 | Product mode | exact profile enables True Widescreen through normal UI; developer extra not required | launch plan + UI + device |
| W-31 | Fallback | unsafe 2D/ambiguous scenes use centered 4:3 without classifier flicker | classifier log and final captures |
| O-00 | 100% equivalence | baseline timing, saves, audio and update cadence match within budget | telemetry comparison |
| O-01 | >100 effective | at least 125% changes measured ARM9 work while wall clock remains normal | native telemetry CSV |
| O-02 | Timing | 10-minute elapsed drift ≤0.1% for every exposed ratio | wall-clock/timer report |
| O-03 | Audio/IPC | no pitch change, underrun, crackle, ARM7/IPC hang | audio/telemetry log |
| O-04 | Save state | ratio metadata persists; incompatible load rejects safely | automated/device test |
| U-01 | Game card | exact EU ROM displays resolved enhancement controls and compatibility status | UI test/device capture kept private + text evidence |
| U-02 | Plan truth | requested and effective states differ visibly when blocked/unsupported | unit/UI test |
| U-03 | Play/relaunch | Play launches the exact resolved plan; relaunch-required changes are not half-applied | device flow |
| R-01 | RA Off | no RA bootstrap or submission | mock/device log |
| R-02 | RA Casual Enhanced | set loads from original identity with enhancements active | runtime/device evidence |
| R-03 | Hardcore conflict | Enhanced Hardcore never starts silently; Original+restart path works | policy/UI/device test |
| R-04 | Hardcore Original | cheats, curated codes, OC, rewind/load state blocked as required | session matrix |
| R-05 | User-Agent/secrets | own stable User-Agent; no auth material in logs/evidence | request test + secret scan |
| F-01 | Unique updates | 60 distinct game updates in one real second, not duplicated presentation | internal game-update counter trace |
| F-02 | Real time | 10-minute game/wall drift ≤0.1%; timers/physics normal | timing report |
| F-03 | Stress | Chain Chomp, mountain, boss/effects/water do not slow down | per-scene telemetry |
| F-04 | Combination | Analog + True WS + validated OC + 60fps + RA Casual works | combined device run |
| S-01 | Soak | 60 minutes combined features without crash, ANR, stale display or audio failure | soak report |
| S-02 | Lifecycle | save/relaunch, home/resume and sleep/wake restore both screens | device report |
| S-03 | Publication | no prohibited path/secret; commits and core submodule are publicly reachable | scans + remote refs |
| S-04 | Build | full unit, SPIR-V and debug APK build green | build log + APK SHA-256 |

## Green vocabulary

Use only:

```text
PASS
PASS_EXPERIMENTAL_<ratio>
PASS_VALIDATED
PARTIAL
BLOCKED
NOT_STARTED
```

Do not use a green label with an explanatory footnote that retracts the claim.
