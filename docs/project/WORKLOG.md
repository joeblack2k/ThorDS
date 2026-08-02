# Worklog

## 2026-08-01 - Vulkan product scope confirmed

- The ThorDS Enhanced product path is Vulkan on the AYN Thor.
- ~~Original/safe-mode software-renderer gameplay~~ is explicitly out of
  scope for product validation and daily play; no further acceptance work will
  be spent on that route.
- The next active gate remains M13 F2/F5: semantic gameplay cadence and the
  required 60fps acceptance evidence on the working Vulkan Enhanced state.

## 2026-08-01 - M13 F2 inventory status correction

- Re-ran the pinned EU cadence-consumer scanner against the current source
  tree: 192 findings, source-tree SHA-256
  `1a9a06cd51443f7f28fda8c35edbc276fc8a6074aaf2e9b49c4340e14d490499`.
- The generated inventory contains no generic `unknown` or
  `unresolved-function` findings.
- F2 remains open for semantic/manual review; classification output alone does
  not authorize a cadence patch.

## 2026-08-01 - M13 F2 cadence write disposition

- Reviewed all 18 source-level writes to `data_0208ee44`.
- The writes are boot/init, scene or overlay transition assignments, including
  minigame and audio transitions; none is a standalone fixed-step gameplay
  consumer.
- All 18 are retained for binary/runtime verification and none is promoted to
  an F4 patch manifest.
- Evidence: `docs/evidence/m13/cadence-write-review.md`.

## 2026-08-01 - M13 F2 primary semantic consumer mapping

- Mapped `func_020199A4` at `0x020199A4` as the primary cadence scheduler
  boundary: it reads `data_0208ee44` into `dt`, updates the scheduler
  countdown and invokes the downstream timer/update path.
- Mapped the principal downstream consumers at `0x02019AC4`, `0x020242C8`,
  `0x0202BBBC`, `0x020326AC` and `0x02034B40`.
- No cadence write or downstream read was promoted to an F4 patch. The next
  runtime experiment is an entry count at `0x020199A4` correlated with the
  existing semantic window and a known 30 FPS baseline.
- Evidence: `docs/evidence/m13/f2-semantic-consumer-review.md`.

## 2026-08-01 - M13 F2 Route-A semantic runtime route

- Enabled the existing profile/debug-gated Route-A semantic monitor on the
  green generic-camera checkpoint.
- Runtime counters were coherent for the active scene: Stage Behavior `376`,
  Stage Render `376` and VBlank `376`; no crash or ANR occurred.
- MainLoopSlot1 remained `0` in this state, so it is not treated as the sole
  semantic marker. The monitor was disabled again after the measurement.
- Evidence: `docs/evidence/m13/f2-semantic-runtime-route.json`.

## 2026-08-01 - M13 F5 60fps stability window with camera hook

### Result

- Enabled the exact-EU debug cadence probe for one bounded measurement and
  loaded the private green generic-camera checkpoint.
- Across 60 seconds there were 49 game-loop log windows; 48 complete windows
  reported 60 or 61 unique updates/sec. The single boundary window reported
  50 updates.
- All observed cadence values were `1`; no crash or ANR was observed.
- The debug cadence probe was disabled again and the profile relaunched with
  `curatedCodes=3`, `slot2Analog=1` and `camera=1`.

### Boundary

- This is a stronger timing-stability result with the camera hook active, not
  the complete semantic 60fps product acceptance gate. Physics, animation,
  audio and longer physical-play equivalence remain open.
- Evidence: `docs/evidence/m13/f5-60fps-stability-window-camera-green.json`.

## 2026-08-01 - M13 F4 generic camera hook runtime validation

### Result

- Replaced the unused normal-orbit entry hook at `0x0200BB28` with a guarded
  hook at the active generic camera update entry `0x02009E70`.
- Preserved the original ARM9 prologue and resumed at `0x02009E7C`.
- Loaded the private generic-camera checkpoint and held right-stick camera
  input at `0.75` for `2500 ms`.
- Runtime result: `PASS`; 61 unique updates, 9,907 protocol reads and a
  non-zero game-side `cameraYawOffset` of `25970`.
- No crash or ANR was observed. The green follow-up checkpoint remains private;
  its local filename is not published.
- Commit: `01cf152f fix: hook generic SM64DS camera update`.

### Boundary

- This validates the generic runtime hook path, not the complete physical
  camera acceptance matrix.
- HUD/tutorial-arrow suppression, ordinary-yaw sound proof, physical feel and
  the full M13 semantic 60fps product gate remain open.
- ROM bytes, savestates and device identifiers remain private and are not
  included in the repository.

## 2026-08-01 - Smooth Orbit Camera v1 exact EU patch slice

### Result

- Disassembled the exact locally owned EU ARM9 image with its real load base
  `0x02004000`; the normal orbit routine is `0x0200BB28`.
- Identified the legacy digital-yaw block at runtime
  `0x0200BCF0`-`0x0200BD4C`.
- Added an ARMv5TE guarded trampoline source and a deterministic Action Replay
  generator under `tools/thords/camera/`.
- The generator rejects a mismatched original hook word, a non-zero payload
  region, an oversized payload or an out-of-range branch. An independent
  verifier reconstructs the hook branch and payload alignment.
- Integrated the generated code only into the exact
  `sm64ds.eu.thor-enhanced` profile. Original remains without the patch.
- Profile tests, ARM assembly, patch generation, patch verification, debug APK
  build and Thor install completed successfully.

### Boundary

- The exact patch has not yet passed physical Enhanced gameplay validation.
- HUD/tutorial-arrow suppression, ordinary-yaw sound proof and the full camera
  trace remain open.
- The ROM and decompressed ARM9 image remain local-only inputs; generated public
  output contains code words and hashes, not ROM bytes.

## 2026-08-01 - Smooth Orbit Camera v1 frontend and Slot-2 bridge

### Result

- Imported and published the bounded camera dossier under `docs/camera/`.
- Added and unit-tested the pure radial-deadzone/response mapper for
  proportional horizontal right-stick yaw.
- Enhanced input now reserves `AXIS_Z`/`AXIS_RZ` for smooth camera state
  instead of synthesizing digital D-pad events.
- R3 uses a one-shot edge sequence; physical D-pad input remains available as
  the original digital fallback.
- Added the transient Slot-2 camera register bank at `0x09000200` in the
  public core fork, preserving mode 0/1 behavior and savestate format.
- Added Kotlin -> JNI -> native transport with reset/teardown neutralization.
- Exact EU identity was rechecked locally: `ASMP`, revision `0`, RA hash
  `ba3c4052e00c5cc31df5d5534c39de1b`.

### Boundary

- This is not yet a working in-game orbit camera. The exact EU runtime patch,
  camera-state gating, HUD/tutorial-arrow suppression, ordinary-yaw sound
  proof and physical Thor matrix remain open.
- The ROM remains ignored and was used only as local input. No ROM bytes,
  screenshots or device identifiers were added to source or evidence.
- Green checks: smooth mapper unit tests, GitHubProdDebug native build and
  Android-test Kotlin compilation.

## 2026-08-01 - 60fps requirement wording corrected

### Result

- ThorDS Enhanced 60fps is explicitly recorded as a required product mode and
  release gate.
- Project guidance no longer describes 60fps as explicitly disabled or merely
  optional.
- The `Original` profile remains a separate compatibility profile with its
  native timing behavior; this does not waive the Enhanced 60fps requirement.
- M13 implementation and semantic timing validation remain open. Existing
  loop-counter evidence is not promoted to a gameplay 60fps claim.

## 2026-08-01 - M11 pre-launch Hardcore recovery choice

### Result

- Selecting `Hardcore` while `Thor Enhanced` is selected now fails closed in
  the ROM-details UI before Play starts a session.
- The user receives two explicit choices: `Original + restart` or
  `Enhanced + Casual`.
- No preference is committed until the normal validated Play path, preserving
  the existing pending-edit behavior.
- Physical Thor validation initially found that the recovery dialog state was
  not rendered; the missing `SingleChoiceDialog` binding was fixed and the
  flow was repeated successfully.
- The repeated physical flow displayed both recovery choices, selected
  `Enhanced + Casual`, returned to the details screen and did not launch the
  emulator.
- A second physical run selected `Original + restart`; the details screen
  resolved to `Original / MATCH_EXACT`, showed Analog Controls `Off` and the
  relaunch-required toast, and did not launch the emulator.
- Process recreation was then tested with a correct persisted baseline:
  Original was committed through Play, Enhanced was staged without Play, the
  process was force-stopped, and the reopened details screen still showed
  Original. The staged Enhanced edit was discarded.
- GitHubProdDebug compilation, focused Profile/RA tests, APK build, Thor
  installation and launcher smoke passed.

### Boundary

- This closes both physical recovery-dialog branches and staged-edit process
  recreation, not the complete physical M11 matrix. Safe-mode recovery,
  process-recreation and both-display acceptance remain open.

## 2026-08-01 - M13 60fps timing-hook audit

### Result

- M13 is confirmed as a required product and release gate.
- The real boundaries were mapped: Android `runFrame`, emulated
  `NDS::RunFrame`, DS VBlank at `VCount == 192`, ARM7 VBlank delivery and
  the existing Action Replay hook.
- The current repository contains no proven European `ASMP` 60fps code or
  legal patched reference copy, so no unverified code words were added.
- The public community patch reference is for NTSC-U/USA revision 1.1 and
  cannot be copied into the European `ASMP` revision-0 profile.
- Provenance is recorded as the gamemasterplc video only; the community
  reference targets USA revision 1.1, has no source code or inspectable AR
  words, and is not used as EU production provenance.

### Boundary

- This is research evidence, not a 60fps implementation or validation pass.
- A legal original/patched comparison or source-equivalent mapping and a
  semantic game-update counter are still required.
- Evidence: `docs/evidence/m13/timing/core-hook-audit.txt`.

## 2026-08-01 - M13 EU game-side cadence map

### Result

- The pinned EU decomp maps `IRQ::VBlankHandler` at `0x0201a534`, the
  `data_0209d514` VBlank counter and `data_0208ee44` cadence threshold.
- The render/lag split is visible through `func_02019144` and
  `func_02019100`, with the active scene object dispatched through
  `data_0209d4a8`.
- The normal scene object resolves to `_ZTV5Stage`, with
  `Stage::Behavior` at `0x0202bbbc`.
- The follow-up dispatch audit separates IRQ graph slots 2/3 from semantic
  lifecycle slots 6/9; `dScEntry_c::Behavior` is `0x0211a2b8` and
  `dScEntry_c::Render` is `0x0211a26c`.
- Local ignored EU overlay extraction produced original-reference SHA-256
  digests and file offsets for the entry behavior/render/init functions;
  only metadata is committed.
- A read-only native/Kotlin getter now samples the mapped `data_020a0db0`
  game-loop counter without changing emulator state. GitHub Prod Debug native
  and Kotlin compilation plus APK packaging passed.
- Independent review confirms that animation, stage, actor-timer, message and
  HUD code consume the same cadence value.

### Boundary

- Scene/overlay initialization can restore the cadence value.
- The vtable methods, EU ROM offsets, original words and conditional runtime
  patch preconditions are not yet proven.
- This is a stronger M13 reverse-engineering basis, not a 60fps
  implementation or validation pass.
- Evidence: `docs/research/sm64ds-60fps-decomp-map.md`.
- Original-function metadata:
  `docs/evidence/m13/timing/eu-overlay-original-hashes.txt`.
- Counter instrumentation:
  `docs/evidence/m13/timing/game-loop-counter-instrumentation.txt`.

## 2026-08-01 - M13 timed semantic-counter sampler

### Result

- Added a read-only native/Kotlin sampler for the EU decomp-mapped
  `data_020a0db0` main-loop counter.
- The sampler runs after each completed `NDS::RunFrame()` and publishes
  one-second windows with `uniqueUpdates`, `emulatorFrames` and wall-clock
  duration.
- GitHub Prod Debug native/Kotlin build and APK packaging passed.
- A live Thor probe exposed a counter reset during launch/scene initialization;
  the sampler now discards descending-counter deltas instead of interpreting
  them as unsigned wraparound.
- A stable Castle Garden session then produced native windows around
  `60/61` loop updates per real second with `cadence=1`. This validates the
  measurement path, not the 60fps product gate; semantic timer/physics/audio
  and baseline comparison remain open.
- Repeating the same run under the exact `Original` profile produced the same
  `60/61` and `cadence=1` result. The counter therefore cannot serve as the
  semantic 60fps discriminator; the next M13 slice must instrument a gameplay
  timer/update consumer or derive the actual patch effect.
- Added the mapped `Stage::Behavior` timer candidate at `data_0209f304`;
  it was `0` in the Castle Garden checkpoint and is not being used as a green
  signal.

### Boundary

- This remains instrumentation only. It does not enable 60fps, change cadence,
  or establish 60fps acceptance until a live EU ASMP session proves unique
  semantic updates, normal timers/physics/audio, and stress behavior.

## 2026-08-01 - M10 native launch telemetry baseline

### Result

- The resolved ARM9 percentage now reaches the native emulator configuration
  at ROM launch; the safe default remains 100%.
- A physical Thor run of the enhanced SM64DS profile resumed successfully and
  reported native telemetry at 100% with zero remainder:
  `baseCycles=1854214500`, `scaledCycles=1852424410`.
- The filtered run showed no crash, SIGSEGV or ANR.

### Boundary

- This is a telemetry baseline, not an M10 green pass. 100% equivalence,
  long-run drift, audio/RTC/event correctness and effective >100% execution
  remain open. The capability stays `PLUMBING_ONLY`.
- Evidence: `docs/evidence/m10/telemetry-100.csv`.

## 2026-08-01 - M10 temporary 125% runtime probe

### Result

- A local, uncommitted debug override allowed one short 125% Thor probe.
- After pause/resume the native telemetry reported
  `percent=125`, `remainder=25`, `baseCycles=1507950794` and
  `scaledCycles=1205103924`.
- The observed screen reported 60 FPS and the filtered run showed no crash,
  SIGSEGV or ANR.

### Boundary

- This is exploratory evidence only: it is not a 10-minute drift,
  equivalence, audio/RTC/IPC/GPU or generic-DS acceptance result.
- The temporary capability/request override was removed immediately and was
  not committed. Product code remains `PLUMBING_ONLY` with effective 100%.
- Evidence: `docs/evidence/m10/telemetry-ratios.csv`.

## 2026-08-01 - M10 temporary 125% stability soak

### Result

- With a temporary, uncommitted `EXPERIMENTAL` capability override and the
  persisted 125% preference, the Thor launch reported effective 125%.
- Twenty 30-second checks completed over 10 minutes with the process alive
  throughout; filtered fatal, SIGSEGV and ANR counts remained zero.
- The temporary source override and device preference were restored to
  `PLUMBING_ONLY` and `100%` immediately after the run.

### Boundary

- This closes only a temporary 125% crash/stability slice. It does not prove
  equivalence, drift, audio/RTC/ARM7/IPC/GPU correctness or product readiness.

## 2026-08-01 - M10 incompatible-ratio savestate gate

### Result

- Saved a state under temporary effective 125% and then installed/launched the
  restored safe 100% product build.
- Loading the state returned `success=0`.
- Native log explicitly reported:
  `savestate: ARM9 overclock mismatch (current=100 state=125). cannot load.`

### Boundary

- The incompatible-ratio load guard is now physically green. The state stayed
  app-private and was not published. M10 equivalence, drift and subsystem
  correctness remain open.
- Evidence: `docs/evidence/m10/savestate-ratio-mismatch.txt`.

## 2026-08-01 - M10 scaled-cycle accounting correction

### Result

- Corrected `NDS::AdvanceARM9Timestamp` so the rational scaled-cycle
  numerator is kept as `u64` through division instead of being truncated to
  `u32` before it is added to the 64-bit timestamp counters.
- Rebuilt `GitHubProdDebug` successfully with `/tmp/thords-cargo`.
- Reinstalled the safe APK on the connected AYN Thor and explicitly restored
  the ASMP profile preference to 100%.
- Published core commit `3c54a9c8` and parent commit `ad3a5179`.

### Boundary

- This removes an accounting-width defect; it does not prove scheduler
  equivalence, timing drift, audio/RTC/ARM7/IPC/GPU behavior or a product
  over-100% runtime gate. M10 remains `PARTIAL`.

## 2026-08-01 - M10 scheduler telemetry snapshot

### Result

- Extended native ARM9 telemetry with `sysTimestamp`, `arm9Timestamp`,
  `arm9Target`, `arm7Timestamp` and `frameCount`.
- Added a debug-only `DUMP_ARM9_TELEMETRY` action for fixed-interval paired
  runs.
- On the Thor at effective 100%, a live snapshot reported
  `baseCycles=408614392` and `scaledCycles=408614392`,
  `sysTimestamp=270011580`, `arm9Timestamp=540023160`,
  `arm7Timestamp=270011580` and `frameCount=482`.

### Boundary

- This is the first usable scheduler snapshot, not yet a complete equivalence
  or 125% comparison. Paired same-state runs and subsystem counters remain.
- Evidence: `docs/evidence/m10/telemetry-100.csv`.

## 2026-08-01 - M10 paired scheduler snapshot

### Result

- Captured the same fixed-duration fresh-launch procedure at effective 100%
  and temporary effective 125% using the native snapshot action.
- 100%: `baseCycles=417601213`, `scaledCycles=417601213`,
  `sysTimestamp=286257090`, `frameCount=511`.
- 125%: `baseCycles=436132147`, `scaledCycles=348905717`,
  `sysTimestamp=282895950`, `frameCount=505`.
- The 125% `scaled/base` relationship is approximately 0.80, confirming the
  rational ratio path is active.

### Boundary

- The fresh launches were not identical save-state checkpoints and the base
  work increase was only approximately 4.4%, so this is exploratory evidence,
  not proof of 25% more ARM9 work or subsystem equivalence.
- Temporary capability and device preference were restored to
  `PLUMBING_ONLY` and 100%.
- Evidence: `docs/evidence/m10/paired-scheduler-snapshot.csv`.

## 2026-08-01 - M10 100% Thor stability slice

### Result

- Reinstalled the restored safe product build on the connected Thor.
- Ran the enhanced SM64DS profile for 10 minutes with 20 process checks.
- The process stayed alive throughout; final PID was `32257`.
- Filtered final logcat contained zero `FATAL EXCEPTION`, `SIGSEGV` and
  `ANR in` matches.

### Boundary

- This closes the 100% stability slice only. M10 remains partial until
  equivalence, drift, subsystem correctness and the formal >100% gates pass.
- Evidence: `docs/evidence/m10/stability-100.txt`.

## 2026-08-01 - M10 persisted ARM9 preference wiring

### Result

- ROM launch now reads the existing hash-bound `ProfilePreferences` record
  before planning the session and passes `requestedArm9Percent` into the
  profile engine.
- Missing or invalid stored preferences continue to resolve safely to 100%;
  the live capability remains `PLUMBING_ONLY`.
- The profile engine tests and GitHub Prod Release compile/test gate passed.

### Boundary

- This adds deterministic configuration plumbing only. It does not expose a
  user toggle or promote any ratio to `EXPERIMENTAL`/`VALIDATED`.

## 2026-08-01 - M10 debug preference roundtrip

### Result

- Added a debug-only `SET_ARM9_PERCENT` broadcast for repeatable per-ROM
  preference setup using the existing profile repository.
- On the Thor, stored `requestedArm9=125` for the SM64DS identity and relaunched
  the ROM. The launch log reported `requestedArm9=125`,
  `effectiveArm9=100`, `arm9Capability=PLUMBING_ONLY`, and native telemetry
  reported `percent=100`.
- Reset the device preference to 100 after the probe.

### Boundary

- This validates persistence and fail-closed capability resolution only. It
  does not promote the capability or claim a 125% product pass.

## 2026-08-01 - M9 RetroAchievements closeout

### Result

- M9 is `PASS` for the ThorDS product gate.
- The owner verified a real RetroAchievements achievement unlock during
  normal gameplay on the connected Thor with the enhanced product active.
- This closes the physical Casual unlock requirement in addition to the
  policy resolver, launch gate and fail-closed conflict tests.
- Hardcore eligibility remains policy-gated to the Original profile and is
  not claimed as a separate account-unlock test.

### Decision

- Do not reopen M9 for another Casual unlock.
- Next milestone: M10 ARM9 overclock foundation, measurement-first with
  over-100% behavior disabled until timing evidence proves it safe.

## 2026-08-01 - M8 productization closeout

### Result

- M8 is `PASS` for the current ThorDS product gate.
- True Widescreen is resolved through the exact SM64DS Europe enhancement
  profile and supported Thor/Vulkan capability path.
- The primary world uses the layer-aware 16:9 presentation while structured
  2D/UI content remains in the centered 4:3-safe area; the lower screen
  remains aspect-correct.
- Safe-mode, default preference and relaunch behavior were checked. The
  session-lock correction prevents live 16:9/4:3 oscillation during camera,
  text and transition frames.
- The connected Thor reached the SM64DS profile and resumed normal gameplay
  after the M8 build was installed. No ThorDS crash, FATAL, SIGSEGV or ANR
  was observed in the filtered checks.

### Validation

```text
build: :app:checkVulkanSpirv
       :app:testGitHubProdReleaseUnitTest
       :app:assembleGitHubProdDebug
result: PASS
physical Thor: APK install, launch, profile resolution and gameplay smoke PASS
filtered crash/ANR scan: PASS
```

The user's physical confirmation is the product acceptance witness for the
reported flicker and mirrored-output regressions. Temporary screenshots and
private save-state material remain outside the repository and are not evidence
artifacts.

### Decision

- M8 is closed. Do not repeat the M7 probe or classifier-only investigation.
- Next milestone: M9 RetroAchievements policy closeout.

## 2026-08-01 - G2/M7 deterministic proof closeout

### Root cause and bounded changes

- Fixed the final-primary Vulkan alpha composition. The swapchain blend state
  had replaced destination alpha with transparent UI fragments; destination
  alpha now uses `ONE_MINUS_SRC_ALPHA`, matching the color blend and preserving
  the already rendered world.
- Extended the existing debug-only exact-frame surface fixture with bounded
  analog input, warmup frames, summary-only long runs and three retained
  keyframes. Added an OR-only, paused-emulator Main RAM fixture for the exact
  European SM64DS initial-castle-key flag; it cannot write ROM or save files.
- Added dependency-free W-01/W-03/W-04/W-05 and W-20 analyzers with runnable
  synthetic self-tests.

### Deterministic acceptance

- W-01: PASS. The paired native/probe Yoshi green-head silhouette differs by
  0.6579% and 1.3158%, inside the 2% object-aspect limit.
- W-03: PASS. The named Castle Garden waterfall-mist landmark stays wholly in
  the right side ROI for ten exact frames, spans five distinct centers and
  moves 52.5 pixels.
- W-04/W-05: PASS. The pause star-count zero and instruction glyph preserve
  their paired native/probe ratios within 1.4926%.
- W-06: PASS. The 256x192 lower source, final secondary-display 4:3 rectangle,
  unrotated lower surface and touch-grid mapping are green.
- W-20: PASS. A Castle Garden key-door run captured 384 exact consecutive
  frames. Every final frame is opaque and non-black; 192 distinct final and
  source hashes show game-cadence motion with a maximum repeat run of three,
  no nonconsecutive stale-frame recurrence and valid 4:3 or 16:9 presentation
  rectangles. First-to-last changed-pixel ratios are 62.6346% final and
  61.4807% source.

### Build, device and publication gates

```text
command: CARGO=/tmp/thords-cargo ./gradlew --no-daemon
  :app:regenerateVulkanSpirv :app:checkVulkanSpirv
  :app:testGitHubProdReleaseUnitTest :app:assembleGitHubProdDebug
result: PASS; 145 actionable tasks, 19 executed, 126 up-to-date

GitHubProdDebug SHA-256:
315574549acb162e9ebeab8c8cbff230006a5e5e37dcf3ce1e95e04761819567

physical Thor install/launch: PASS
package: io.github.joeblack2k.thords.dev
coexistence: PASS with both MelonDualDS package variants
filtered ThorDS FATAL/ANR scan: PASS
```

- Repository ROM/save scans are green across tracked files and all reachable
  history. The single local ROM is ignored and unstaged; `melon.zip` remains
  untracked and excluded.
- Gitleaks scanned 1,008 reachable commits. The sole history finding is the
  documented public NIST AES test vector. Five local-source findings and the
  single expanded-APK finding are reviewed public third-party test or SDK
  verification constants, not credentials. The final staged scan is green.
- No owner physical comfort claim is inferred from machine captures. Upright
  top-panel orientation and sustained play feel remain an explicit owner check
  during M8 productization.

### Decision

- M7 is PASS and `ADR-true-widescreen.md` is accepted.
- Decision gate: `GO_FOR_M8_PRODUCTIZATION`.
- Next milestone: M8 True Widescreen product behavior, exact capability
  gating, requested-versus-effective status and conservative 4:3 fallback.

## 2026-07-31 - M7 in progress

### Findings

- Pinned the ignored `tangosdev/sm64ds-decomp` research checkout at
  `2d38fe9b825199deec408240849b64b91c965d85`.
- Semantically mapped the ASMP revision-0 main ARM9 and overlay-6 `0x1555`
  Fix12 aspect literals. The gameplay-camera literal is overlay-resident and
  cannot be treated as a persistent ROM-file offset.
- Confirmed that the Vulkan direct presenter already binds independent
  high-resolution 3D, packed planes and control buffers.

### Changes

- code: added an explicit debug-build plus intent-gated Vulkan diagnostic that
  draws 3D full-width and plane-1/UI inside a centered 4:3 top safe area.
- docs: added the symbol map, original-word evidence, zero-private-media
  capture policy and a no-go ADR.

### Validation

```text
command: CARGO=/tmp/thords-cargo ./gradlew --no-daemon
  :app:regenerateVulkanSpirv :app:checkVulkanSpirv
  :app:testGitHubProdReleaseUnitTest :app:assembleGitHubProdDebug
result: PASS; embedded SPIR-V synchronized, unit tests and APK build green

command: adb install -r app/build/outputs/apk/gitHubProd/debug/app-gitHub-prod-debug.apk
result: PASS; debug APK installed on the connected AYN Thor

physical developer probe: exact ASMP launch reached ROM-ready and initialized
Vulkan with the explicit developer probe extra. The presenter submitted
separate world-3D and plane-1/UI draws through the compositor fallback route
because the Thor uses two presenter surfaces. The debug-only primary-surface
probe widened the target to 1920x1080 and kept the UI-safe-area centered at
1440x1080. A local-only visual check was non-empty and stable, then deleted.
This is compositor geometry proof only, not a game-projection or BOB
acceptance pass.

Android input injection did not advance the title flow with the resolved
DS-A or Start mappings. Continue the M7 scene matrix with a physical
Thor-controller run.

The exact Europe aspect candidate is now a default-off, experimental runtime
Action Replay code. It is enabled only when the debuggable activity receives
the M7 probe extra, and each write has an `IF original 0x1555` guard. The
physical debug launch reported two curated codes and retained a stable
1920x1080 world / 1440x1080 UI-safe presentation without a crash.
```

### Safe fallback correction

- The developer probe is now primary-presentation-only; the lower external
  presentation keeps its normal aspect-correct configuration.
- The primary presenter now uses the current top structured-3D and capture-3D
  inputs as a conservative diagnostic classifier. A missing current top 3D
  source, or any capture-3D source, selects a centered 4:3 composite instead
  of the dual-UV world draw.
- Physical Thor debug launch: PASS. The measured title composition reported
  no current top structured 3D and no capture 3D, then selected
  `x=240,width=1440,height=1080` on the 1920x1080 top target. No ThorDS
  FATAL/ANR was observed.
- Validation: `git diff --check`, `:app:checkVulkanSpirv`,
  `:app:testGitHubProdReleaseUnitTest` and
  `:app:assembleGitHubProdDebug` all passed after the correction.
- Evidence: `docs/evidence/m7/fallback-probe.txt` and
  `docs/evidence/m7/geometry-measurements.json`.

### Title scene metadata

- The existing debug capture route was exercised with only
  `soft_packed_frame_meta_json`; no screenshot or ROM-derived pixel data was
  kept.
- The pre-touch title phase and two subsequent direct DS-touch phases each
  reported no current top structured 3D and no capture 3D. The primary
  presenter consistently selected `FALLBACK_4_3`.
- The bottom compositor mode changed across the touches, while the top source
  remained 2D-only. This is expected title-flow behavior and confirms that the
  classifier is stable across the measured transition.
- All transient ROM and metadata files were removed after aggregation.
- Evidence: `docs/evidence/m7/title-scene-matrix.json`.

### Debug capture harness

- A screen-only capture during the title-start transition exposed an ANR in the
  debug broadcast route. The trace showed the paused route waiting for a full
  Vulkan debug readback, even though it had requested only a screen frame.
- Safe screen and metadata-only requests now default to the existing dense
  live-burst route. Deeper layer, depth and attribute captures still require a
  paused snapshot.
- Physical Thor rerun: both screen-only and metadata-only captures completed
  with `liveBurst=1` and `freezeSnapshot=0`; no ThorDS ANR occurred.
- Validation: `:app:testGitHubProdReleaseUnitTest` and
  `:app:assembleGitHubProdDebug` passed.
- Evidence: `docs/evidence/m7/debug-capture-harness.txt`.

### Dominant structured-3D classifier

- A live local-only Adventure flow reached the File A Peach-intro scene by
  direct DS-touch injection. The top compositor reported a dominant structured
  3D slot across all 49,152 pixels, while the bottom remained 2D-only and no
  capture-3D source existed.
- The earlier visible-pixel predicate was too strict for this composition:
  the structured layer's 1,592 above-layer pixels were black, leaving its
  visible count at zero despite the top 3D slot being present. The diagnostic
  classifier now uses the dominant top structured-3D slot, while any
  capture-3D source still forces the 4:3 fallback.
- Physical Thor result: PASS. The presenter changed from
  `M7Probe: fallback` during the title flow to `M7Probe: dual-uv` during the
  intro. No ThorDS FATAL or ANR was observed.
- Validation: `:app:checkVulkanSpirv`,
  `:app:testGitHubProdReleaseUnitTest` and
  `:app:assembleGitHubProdDebug` passed before installation; the debug APK
  installed and ran on the physical Thor.
- Evidence: `docs/evidence/m7/intro-scene-matrix.json` and
  `docs/evidence/m7/geometry-measurements.json`. All temporary ROM copies,
  frame images, metadata and log output were deleted after aggregation.

### Direct DS-input harness

- Added a debug-only `TAP_INPUT` command for the existing DS system-button
  input path. It accepts only the existing system buttons and uses a bounded
  press duration; it does not change controller mappings, profiles or release
  behavior.
- Physical Thor debug validation logged the direct Start/A sequence on an
  active ASMP session without a ThorDS FATAL or ANR. The touch-only title path
  still did not yield a valid BOB checkpoint, so this is harness proof only.
- Evidence: `docs/evidence/m7/input-harness.txt`.

### Castle grounds scene route

- The debug route reproducibly advanced from title touch through Adventure,
  File A and the skippable intro into controllable castle grounds. The top
  compositor held the full structured 3D slot while the bottom stayed 2D-only,
  and the primary probe selected `M7Probe: dual-uv`.
- The Lakitu-camera transition emitted
  `dual-uv -> fallback -> dual-uv` without a ThorDS FATAL or ANR. This is
  classifier sequence evidence only; it does not prove a no-flash final-panel
  transition.
- D-pad automation did not produce a deterministic BOB-door route. No BOB,
  FOV, culling, HUD-ratio or final-panel measurement is claimed. This
  historical route failure does not define the M7 representative scene.
- Evidence: `docs/evidence/m7/castle-grounds-scene-matrix.json`. All
  temporary ROM copies, saves, frame images, metadata and log output were
  deleted after aggregation.

### Final primary-surface probe

- `adb exec-out screencap -p` captured the final primary surface at
  1920x1080. The title fallback occupied the centered 1440x1080 rectangle,
  leaving 240-pixel black sidebars on both sides.
- In a structured 3D checkpoint, the primary presenter issued draw mode 7 for
  the full-width world and draw mode 8 for the separately sampled 4:3-safe
  overlay. Final visual inspection placed the pause dialog and text inside the
  safe area.
- This is final-surface presentation evidence only. It does not establish the
  game projection, horizontal FOV, culling, castle-grounds geometry, HUD
  ratios or transition behavior needed to exit M7.
- Evidence: `docs/evidence/m7/final-primary-probe.txt`. All temporary ROM
  copies, saves, images, internal captures, metadata and logs were deleted
  after aggregation.

### Decision

- Save-state-assisted physical route: temporary app-private checkpoints
  resumed castle grounds and the castle-entrance approach without replaying
  the title, file selection or intro. Physical Touch Mode movement changed the
  scene after ThorDS was restored to the foreground.
- The separate `dev.picori.tmc.ra` test application reclaimed the foreground
  during one probe, so later touches were initially delivered elsewhere.
  Stopping that test application and restoring ThorDS was sufficient to
  restore the expected input path; this was a temporary device condition, not
  a product change.
- The user-visible top screen was reported as physically rotated 180 degrees
  relative to the internal capture orientation. No FOV, culling, HUD or
  transition measurement is claimed.
- Evidence: `docs/evidence/m7/save-state-physical-probe.txt`. Temporary ROM,
  save-state, capture and log files were removed after aggregation.

- Scope correction: per the repository owner’s explicit override, the M7
  representative scene is the controllable castle-grounds/castle-approach
  checkpoint. Bob-omb Battlefield and rabbit/key capture are no longer M7
  exit criteria; the historical BOB route notes above remain for traceability.
- Primary orientation probe: a launch-scoped debug-only
  `VULKAN_ROTATE_180` extra rotates only the primary Vulkan output quads. The
  same castle-grounds scene produced an inverted logical capture with
  `M7Probe: dual-uv` and `M7Probe: rotate180 surface=1 vertices=12`; the lower
  external presentation kept its ordinary configuration. This proves the
  app-side transform path, not the physical panel result.
- Thor default correction: because the connected device reports manufacturer
  `AYN` and model `AYN Thor`, the primary-only rotation is now enabled by the
  normal Vulkan product config, including release-capable builds. A fresh
  launch without `VULKAN_ROTATE_180` still emitted the rotation log on primary
  surface 1; the external presentation config remains false. The follow-up
  packed-UI correction and physical Castle Garden pause check are recorded
  below.
- Publication: commit
  `54410dc0934886fb6f4ddaba4f59ce3772a9eb1f` is pushed to `origin/main` on
  `https://github.com/joeblack2k/ThorDS`; the corresponding debug build,
  unit tests and physical Thor launch passed.
- Performance probe: the instrumented castle-grounds run reported about
  38-40 FPS and approximately 25.6 ms Vulkan instance CPU time, with presented
  frames and no acquire/submit failures. That diagnostic configuration is not
  a realtime acceptance pass.
- Performance follow-up: a target-local `-O2` option was added only to the
  GitHub debug `core` and Android frontend targets; the generated arm64 debug
  compile database confirms the flag alongside debug symbols. With renderer
  debug tools disabled, the same castle-grounds save state reported `FPS: 60`
  in two captures separated by eight seconds. This is a strong scene-level
  result, but sustained movement and transition behavior remains open.
- Native-versus-probe castle comparison: the native launch reported one
  curated runtime code and showed the world in the centered 1440x1080 4:3
  rectangle. The explicit M7 launch reported two curated codes, drew the world
  across 1920x1080 and exposed new castle-ground water, trees and terrain in
  both side regions. This is world/FOV evidence rather than a claim about
  object aspect or side-region culling.
- M7 is not green. The diagnostic still does not prove object aspect,
  new-side-region culling, exact HUD/glyph/bottom geometry, sustained
  transition behavior or final castle-grounds geometry.
- M8 remains blocked by `ADR-true-widescreen.md`.

### Castle Garden primary UI rotation correction - 2026-08-01

- The owner explicitly selected Castle Garden as the representative M7 scene;
  no Bob-omb route was used for this correction.
- The first Thor-default rotation fix correctly rotated the high-resolution
  world but left the packed DS plane-1 pause overlay unreadable. The root
  cause was draw mode 8 using the composite shader's inverted Y mapping while
  reading the packed top buffer directly.
- The bounded fix keeps the safe 4:3 UI quad unrotated and changes only draw
  mode 8 to sample the native top-down packed-buffer row mapping. The lower
  external presentation remains unchanged.
- Physical Castle Garden smoke after reinstall:
  - world upright and full-width;
  - `CASTLE SECRET STARS` and `TOUCH TO SELECT` readable;
  - 10-second Slot-2 movement held `FPS: 60`;
  - no filtered `FATAL EXCEPTION` or `ANR in`.
- Validation:
  `:app:regenerateVulkanSpirv`, `:app:checkVulkanSpirv`,
  `:app:testGitHubProdReleaseUnitTest` and
  `:app:assembleGitHubProdDebug`: PASS.
- M7 remains open for object aspect, new-side-region culling, exact
  HUD/glyph/bottom geometry and transition stability. M8 remains blocked.

### Castle Garden native composite orientation correction - 2026-08-01

- The physical Thor comparison exposed a second path in addition to the
  explicit dual-UV probe: the normal two-surface presentation uses
  `kDrawModeCompositeFrame` and already carries native top-down surface
  orientation.
- The primary Thor vertex transform was applying a second 180-degree
  geometry rotation to that composite frame. The bounded fix leaves both the
  native composite frame and the packed plane-1 overlay in their source
  orientation, while retaining the rotation for the high-resolution world
  quad.
- Rebuilt and reinstalled the same GitHubProdDebug variant:
  SPIR-V regeneration/check, unit tests and APK build all passed.
- Castle Garden physical checks after reinstall:
  - native composite world: upright, centered 4:3, `FPS: 60`;
  - native composite pause menu: `CASTLE SECRET STARS` and
    `TOUCH TO SELECT` readable;
  - dual-UV world and packed pause overlay: upright and readable;
  - 10-second Slot-2 movement: `FPS: 60`;
  - pause-to-world transition samples at 150 ms and 1 s: correct world frame;
  - filtered logcat: no `FATAL EXCEPTION` or `ANR in`.
- M7 remains open for measured object aspect, new-side-region culling, exact
  HUD/glyph/bottom geometry and longer transition coverage. M8 remains blocked.

### Castle Garden camera projection proof - 2026-08-01

- The owner-selected Castle Garden checkpoint is now the only representative
  scene for this M7 projection probe; Bob-omb Battlefield is not an exit gate.
- The JNI Action Replay parser now shares a strict whitespace-aware helper with
  a standalone native regression test. Newline-separated profile code is
  accepted, while malformed, empty and odd-word input is rejected.
- The static overlay-6 candidate is guarded at `0x020C025C`, but the Castle
  Garden runtime word at that shared address is not the overlay-6
  `0x00001555` literal. The static guard therefore fails closed.
- The guarded pointer-relative write follows the live camera pointer at
  `0x0209F318` and updates `camera + 0xF8` from `0x00001555` to
  `0x00001C72`.
- A temporary emulator-side measurement at the frame boundary compared the
  actual projection matrix load:
  - without the developer probe: `x=0x000020F8`, `y=0x00002BF5`;
  - with the developer probe: `x=0x000018BA`, `y=0x00002BF5`.
- The measured x-scale ratio is 0.75, matching the expected 4:3-to-16:9
  aspect adjustment. The temporary emulator logging was removed before
  integration; only the metadata-only result is retained.
- Validation so far: standalone parser test PASS, GitHub Prod debug build
  PASS, physical Castle Garden projection response PASS.
- M7 remains `PARTIAL_PASS_M7_GATES_OPEN`. New-side-region culling, exact HUD
  circle/glyph geometry, bottom-screen aspect and longer transition stability
  remain open. M8 remains blocked.
- Evidence: `docs/evidence/m7/castle-garden-camera-projection.txt`.

## 2026-07-31 - M0

Commit before: none; repository initialized in this task.

### Inspected

- files: project specification, rc5 source tree, ROM header and required ROM sections
- commands: git init/fetch/checkout, recursive submodule update, git pin assertions, ADB display inventory, ROM hashes
- device state: AYN Thor connected over ADB; display 0 is 1920x1080 and display 4 is 1240x1080
- sources: SapphireRhodonite/melonDS-android tag 0.7.0.rc5

### Findings

- confirmed: HEAD is 9b28076281545a1e08dccee0b3f925febb8933ac
- confirmed: ROM is ASMP, revision 0, exact RA system hash ba3c4052e00c5cc31df5d5534c39de1b
- confirmed: source ROM is ignored and absent from Git index/history
- risk: host lacks the rc5-required Java 21, Android API 36 and NDK 28

### Changes

- code: none
- tests: M0 pin and ignore assertions
- docs: source lock, status, worklog and M0 evidence
- config: local and durable ROM excludes

### Validation

```text
command: git submodule status --recursive
result: PASS; all four expected submodules checked out cleanly
artifact: docs/evidence/m0/submodules.txt

command: python ROM identity parser plus SHA-256
result: PASS; exact ASMP/EU/RA match
artifact: docs/evidence/m0/rom-identity-redacted.json
```

### Decision

- chosen: exact SapphireRhodonite rc5 tag and a new ThorDS branch
- alternatives: current main or another MelonDS Android fork
- rationale: source pins and Thor-specific baseline are explicit project requirements
- ADR: none; this follows the supplied specification

### Next

- next action: install missing M1 toolchain
- remaining gate: M0 commit, then M1 build/install baseline

### Safety

- ROM excluded: yes
- secret scan: no high-confidence secrets found before first commit
- save backup: not applicable; no emulator run yet

## 2026-07-31 - M0 completion

### Changes

- code: added `tools/thords/rom_identity.py`, a stdlib-only inspector that reads
  the same header, ARM9, ARM7 and banner sections as rc5 `RomProcessor`
- tests: synthetic parser self-test
- docs: M0 self-test evidence and status update

### Validation

```text
command: python3 tools/thords/rom_identity.py --self-test
result: PASS
artifact: docs/evidence/m0/rom-identity-self-test.txt
```

### Decision

- chosen: a standalone stdlib tool instead of a new application abstraction
- rationale: M0 needs reproducible local ROM inspection before Android app code changes

### Next

- next action: complete M1 runtime smoke on the physical Thor
- remaining gate: ten-minute run and evidence summary

## 2026-07-31 - M1

### Changes

- code: none; the product source remains exact rc5
- environment: installed the missing local Java 21, Android API 36, NDK 28,
  Rust and rustup prerequisites
- docs: captured build, APK, installation and Thor runtime evidence

### Validation

```text
command: ./gradlew --no-daemon :app:assembleGitHubProdDebug
result: PASS; BUILD SUCCESSFUL, 89 actionable tasks
artifact: docs/evidence/m1/build.log

command: adb install -r -d app/build/outputs/apk/gitHubProd/debug/app-gitHub-prod-debug.apk
result: PASS; separate debug package installed without replacing the existing stable package
artifact: docs/evidence/m1/install.txt

command: ten-minute physical Thor runtime smoke
result: PASS; emulator process remained active, no process exit, no FATAL/ANR,
and the original source ROM identity remained unchanged
artifact: docs/evidence/m1/baseline-logcat.txt
```

### Finding

- rc5 creates a visible `Presentation` on the Thor's lower display while the
  primary 1920x1080 panel is black for the tested layout. This is an observed
  baseline limitation, not a resolved ThorDS feature; M2 owns its diagnosis.

### Next

- next action: capture display roles, touch bounds, controller descriptors and
  lifecycle behavior without hardcoding a display ID
- remaining gate: M2 physical hardware proof

## 2026-07-31 - M2 partial hardware proof

### Findings

- display 0 is the physical top panel: `Built-in Screen`, 1920x1080 logical
  viewport
- display 4 is the physical lower panel: `Screen-2`, 1240x1080 logical
  viewport, presentation-capable and with the dedicated lower touchscreen
- the lower app content window is 1240x969; the remaining 111 pixels belong
  to Android navigation and are not an emulator touch rectangle
- the `Odin Controller` descriptor exposes expected buttons, sticks, triggers
  and D-pad axes
- background/resume and a 30-second sleep/wake retained an active emulator
  process and recreated a visible lower `Presentation` without a FATAL/ANR

### Changes

- code: added a debug-only display probe that labels each physical panel and
  logs received touch coordinates
- product code: none
- docs: M2 display, touch, controller and lifecycle evidence

### Validation

```text
command: launch ThorDisplayDiagnosticActivity and capture both physical displays
result: PASS; roles, names, dimensions and presentation eligibility visibly match
artifact: docs/evidence/m2/display-role-screens/

command: adb input -d 4 3x3 touch grid inside the 1240x969 app window
result: PASS; all nine coordinates arrived unchanged at the lower Presentation
artifact: docs/evidence/m2/touch-grid.json
```

### Remaining gate

- a physical-finger nine-point DS-coordinate accuracy run is still needed to
  prove the game touch rect itself, including its letterbox bars. The logged
  M2 grid proves Android routing but deliberately does not claim that final
  DS-coordinate acceptance.

## 2026-07-31 - M2 completion

### Validation

```text
command: ADB display-4 3x3 taps on the real ASMP RuntimeLayoutView
result: PASS; display points mapped exactly to:
0/128/255 x 0/96/191 DS coordinates
artifact: docs/evidence/m2/touch-grid-ds-log.txt

command: taps outside the 1240x930 DS view
result: PASS; no DS touch log was emitted
artifact: docs/evidence/m2/touch-grid-ds-log.txt
```

### Decision

- chosen: log the existing normalized DS coordinates only while the explicit
  Android `ThorDsTouch` log tag is enabled
- rationale: this proves the real touch path without changing touch behavior
  or making a false physical-finger claim

### Next

- next action: implement the M3 Thor role classifier and default layout
- remaining gate: M3 build, Thor install and dual-panel output proof

## 2026-07-31 - M3 partial product identity and safe defaults

### Changes

- product: introduced the isolated `io.github.joeblack2k.thords` application id,
  ThorDS Enhanced labels, version metadata, and an About screen with the
  pinned MelonDualDS/core provenance
- updater: production binds no-update implementations, omits both the install
  permission and update content provider, and hides the absent product channel
  from General settings
- defaults: AYN Thor receives hidden soft controls only when it has no existing
  preference; the default runs before migrations and migrations preserve that
  choice
- safe mode: the user toggle forces Native/software 4:3-compatible defaults,
  disables cheats and renderer extras transiently, and preserves the original
  user configuration when disabled
- isolation: settings backup and mirror filenames use `ThorDS.opts`, avoiding
  the prior `melonDualDS.opts` collision
- legal UI: the in-app About view exposes a readable offline component notice
  index and the canonical public ThorDS source link

### Validation

```text
command: CARGO=/tmp/thords-cargo ./gradlew --no-daemon \
  :app:assembleGitHubProdDebug :app:testGitHubProdReleaseUnitTest
result: PASS; all GitHub Prod release unit tests and the Thor debug APK built
artifact: docs/evidence/m3/

command: install the debug APK and traverse launcher -> Settings -> About ->
Licenses and notices on the physical Thor
result: PASS; product version, upstream/core provenance, public source action,
and offline notice index are visible
artifact: docs/evidence/m3/thor-validation.md

command: package/manifest inspection plus physical General settings toggle
result: PASS; ThorDS is installed alongside two MelonDualDS packages, the
release merged manifest has no install permission or update provider, safe mode
persists and defaults back to false after the probe
artifact: docs/evidence/m3/package-list.txt,
docs/evidence/m3/release-manifest-updater-check.txt,
docs/evidence/m3/safe-mode.txt
```

## 2026-07-31 - M3 publication completion

### Publication

- created public source repository `https://github.com/joeblack2k/ThorDS`
- preserved `upstream` and added the repository as `origin`
- replaced all temporary source-publication wording with the canonical URL in
  About, notices, README, source lock, provenance, and sync documentation
- removed physical display/UI captures from the local pushable history; public
  evidence contains only textual, redacted checks

### Validation

```text
command: GitHub identity/repository/history/ROM/save/secret safety scan
result: PASS; no prohibited artifact in a pushable ref
artifact: docs/evidence/m3/publication-safety.md

command: CARGO=/tmp/thords-cargo ./gradlew --no-daemon \
  :app:testGitHubProdReleaseUnitTest :app:assembleGitHubProdDebug
result: PASS

command: install on AYN Thor; open About source, notices, and safe-mode UI
result: PASS; source dispatches ACTION_VIEW to the public ThorDS URL; no
filtered FATAL EXCEPTION or ANR
artifact: docs/evidence/m3/thor-validation.md
```

### Next

- M4: Generic Enhancement Profile Engine.

## 2026-07-31 - M4

### Changes

- product: added catalog v1 with an embedded, read-only `original.generic`
  fallback; it cannot contain game identity or curated patches
- engine: added exact identity matching, capability probing, dependency and
  conflict resolution, immutable session plans, diagnostics, stable preference
  keys and corrupt-preference fallback
- safety: curated Action Replay code and user cheats are represented and
  composed separately; no code is emitted without an exact selected profile
- patch infrastructure: added synthetic byte-array IPS and BPS appliers only;
  no source ROM is opened writable and no cache patch is produced
- metadata: the About build metadata now reports profile catalog version `1`

### Validation

```text
command: CARGO=/tmp/thords-cargo ./gradlew --no-daemon \
  :app:testGitHubProdReleaseUnitTest :app:assembleGitHubProdDebug
result: PASS; all release unit tests and GitHub Prod debug APK built

command: adb install -r app/build/outputs/apk/gitHubProd/debug/app-gitHub-prod-debug.apk
result: PASS; io.github.joeblack2k.thords.dev version 0.1.0-dev launched on
the physical Thor and filtered logcat had no FATAL EXCEPTION or ANR
```

### Evidence

- `docs/evidence/m4/profile-catalog-dump.json`
- `docs/evidence/m4/profile-tests.txt`
- `docs/evidence/m4/session-plan-example-redacted.json`

### Scope

- deferred: all real SM64DS profile identity, game codes, controller behavior
  and patch addresses are M5 or later
- safety: no ROM, save, device serial, private capture or credential was
  added to the source tree

### Next

- M5: exact Super Mario 64 DS Europe identity and profile.

## 2026-07-31 - M5

- added exact ASMP / revision 0 / RetroAchievements system-hash profile cards
  for Original and Thor Enhanced
- both cards share the declared runtime-only save compatibility group
- AM64DS and all other runtime payloads remain intentionally absent until M6
- validation: full GitHub Prod release unit tests and debug APK build passed;
  the debug package reinstalled and launched on the physical Thor with no
  filtered FATAL EXCEPTION or ANR

### Next

- M6: integrate the exact AM64DS Europe Slot-2 analog profile.

## 2026-07-31 - M6 implementation and device activation

### Changes

- embedded the exact checked AM64DS Europe Action Replay runtime payload in the
  exact `sm64ds.eu.thor-enhanced` profile
- added revision-aware profile identity, launch planning, Slot-2 Analog
  activation, radial left-stick processing and right-stick D-pad hysteresis
- protected simultaneous physical D-pad and camera mappings from premature
  input release
- made the existing ThorDS safe-mode preference select the exact Original
  profile on the next launch, without a runtime payload or analog accessory
- added debug-only FileProvider launch support and safe-mode control for
  reproducible device validation; neither is included in production builds

### Validation

```text
command: CARGO=/tmp/thords-cargo ./gradlew --no-daemon \
  :app:testGitHubProdReleaseUnitTest :app:assembleGitHubProdDebug
result: PASS

physical Thor: enhanced launch selected one curated code, Slot-2 Analog and
camera override; safe-mode relaunch selected exact Original with all three off.
result: PASS; filtered logcat contained no FATAL EXCEPTION or ANR
```

### Scope

- physical walk/run, swim/fly/slide, right-stick camera response and controller
  reconnect still need a human gameplay observation
- no ROM, save, capture, private URI, device serial or credential was added to
  the source tree

## Castle Garden live classifier stability probe - 2026-08-01

- The owner-selected Castle Garden checkpoint was loaded directly through the
  existing app-private save-state route; no Bob-omb route was used.
- A 180-sample live Vulkan metadata burst completed with populated metadata for
  every requested sample.
- All samples kept the expected Castle Garden classification:
  top structured 3D `49152`, bottom structured 2D-only `49152`, fixed
  `[0,192,0,0]` display-mode counts, and the expected top/bottom compositor
  modes. No classifier change or ThorDS crash/ANR was observed.
- A temporary frozen geometry capture produced a non-empty 3D frame, but its
  coverage/attribute and plane-1 outputs did not provide a discriminating
  object or HUD mask at this checkpoint. No unsupported ratio claim was
  added.
- The first nine burst samples repeated `frameId=0` before the populated
  sequence advanced from `19` through `189`. This is a capture-startup
  limitation, not evidence of a rendered scene transition; it leaves W-20
  unique-frame cadence open.
- Evidence: `docs/evidence/m7/castle-garden-live-stability.txt`.

Result:

`PARTIAL_PASS_M7_LIVE_CLASSIFIER_STABLE`. Castle Garden remains the sole M7
representative scene; Bob-omb Battlefield is explicitly out of scope. W-01,
W-03, W-04, W-05, W-06 and W-20 remain open, so M8 remains blocked.

## Castle Garden live burst warmup correction - 2026-08-01

- Root cause: the debug-only dense capture buffer started immediately after
  resume and could collect the old renderer frame several times.
- Bounded fix: `RendererDebugCaptureLogger` now waits for one renderer frame
  advance before starting a live dense burst. Product rendering and frame
  pacing are unchanged.
- Validation: rebuilt and reinstalled GitHubProdDebug on the physical Thor,
  then repeated the exact 180-sample Castle Garden metadata command.
- Result: `180/180` populated samples with unique contiguous frame ids
  `20..199`; the expected world-safe classifier remained unchanged, with no
  classifier switch, ThorDS crash or ANR.
- The fix closes the capture startup repetition, not the distinct W-20
  world-to-pause-to-world transition gate. Object aspect, side-region culling,
  HUD/glyph/bottom ratios and actual transitions remain open.
- Evidence: `docs/evidence/m7/castle-garden-live-stability.txt`.

## M9 RetroAchievements policy foundation - 2026-08-01

- Added an Android-free `RetroAchievementsPolicyResolver` and synthetic JVM
  matrix under `domain/model/retroachievements`.
- Off remains Off, Casual remains Casual with enhancements, and Hardcore never
  silently downgrades: any integrity conflict becomes explicit `BLOCKED`.
- Stable reason codes cover profile/enhancement state, curated codes, enabled
  cheats, non-100 ARM9, rewind, state load, save-state resume, slowdown and
  frame advance.
- Hardcore and BLOCKED deny integrity-affecting runtime permissions and require
  a reset decision; this foundation does not yet alter the real launch flow.
- Focused validation passed: 10 tests, 0 failures, 0 errors. The unexcluded
  attempt stopped before Kotlin compilation in the pre-existing cargo/rustup
  librashader wrapper path; the established full-build wrapper remains the
  required build route.
- Evidence: `docs/evidence/m9/policy-tests.txt`.
- M9 remains `IN_PROGRESS`; runtime wiring, conflict UI, online/offline
  validation, save-state enforcement and physical RA session proof remain
  open. M7 Castle Garden remains the active release blocker.

## M9.2 RetroAchievements launch-policy wiring - 2026-08-01

- Built the complete profile/session plan before RA endpoint/session/native
  bootstrap. The plan now carries explicit Original versus Enhanced integrity,
  the unmodified requested RA mode, deterministic hash diagnostics, and the
  resolved policy.
- The planner requires requested RA mode explicitly; no launch path treats its
  former Casual default as user intent.
- Bound policy input to active enhancements, curated runtime codes, enabled
  user cheats, ARM9 100%, rewind false, ordinary lifecycle resume, and the
  explicit save-state autoload flag.
- `BLOCKED` now returns through the existing ROM load error state before
  `beginSession`, emulator `startSession`, or RA bootstrap. Off skips RA
  bootstrap and Casual preserves enhancements. Clean Original Hardcore remains
  eligible; no silent policy downgrade was added.
- Stable reason codes remain in the policy value/test contract, plan
  diagnostics, and a non-secret launch log line; no UI was added.
- A requested RA mode that the selected profile does not allow now fails closed
  with `requested_mode_unavailable` instead of silently becoming `OFF`.
- If launch-decision resolution throws after endpoint acceptance,
  `CancellationException` is rethrown; other failures end the endpoint
  session, report `RomLoadError` and return before emulator/native bootstrap.
- Focused validation passed: `ProfileEngineTest` 16/16 and the existing
  `RetroAchievementsPolicyTest` 10/10, with 0 failures and 0 errors. The
  source-order assertion and `git diff --check` also passed.
- Focused validation also passed `EmulatorViewModelLaunchPolicyTest` 1/1,
  proving the fail-closed cleanup and no fallback launch decision.
- Evidence: `docs/evidence/m9/launch-policy-tests.txt`.
- Publication audit: the current diff adds no ROM, save, private evidence or
  credential material. Reachable public history already contains deleted
  upstream binary paths `drastic_bios_arm7.bin`, `drastic_bios_arm9.bin` and
  `romlist.bin`; these were not added, restored or modified. Status is
  `PASS_WITH_KNOWN_LEGACY_HISTORY`; no history rewrite or force-push was
  attempted or authorized.
- This is M9.2 pre-bootstrap wiring only. Native/runtime, network/offline,
  conflict UI, physical session and full M9 acceptance remain open. No commit
  was created.

## M7 W-20 Castle Garden transition probe - 2026-08-01

- Ran the bounded physical Castle Garden live transition probe after the
  M9.2 push.
- Capture cadence was stable: 180/180 complete samples, unique contiguous
  frame ids `1077..1256`, complete metadata, and no filtered crash/ANR in the
  successful capture window.
- Two `KEYCODE_BUTTON_MODE` attempts produced no observable pause transition.
  The inspected samples remained world frames; 90/179 adjacent PNG pairs were
  byte-identical, so no-previous-frame transition proof is not available.
- One earlier no-ready setup attempt produced an app ANR before the successful
  capture. Temporary device and local artifacts were deleted.
- Decision: W-20 remains `PARTIAL`, not a pass. Evidence:
  `docs/evidence/m7/castle-garden-w20-transition.txt`.
- Next runnable gate: W-01 object aspect using a paired native/probe
  Castle Garden renderer frame and bounding-box ratio.

## M7 W-01 Castle Garden object-aspect probe - 2026-08-01

- Ran paired native (`widescreenProbe=0`) and probe
  (`widescreenProbe=1`) launches on the connected Thor.
- The requested Castle Garden slot-2 restore returned `success=0` in both
  launches, so checkpoint restoration was not proven.
- Both one-frame renderer captures completed successfully at 256x192, but
  every pixel in both renderer PNGs was opaque black. No identifiable object
  or bounding box existed; the composite screen frames were not suitable
  object sources.
- No capture timeout, filtered FATAL or ANR occurred in the bounded attempt.
  Temporary device and local artifacts were deleted.
- Decision: W-01 is `BLOCKED`, not a geometry pass. Evidence:
  `docs/evidence/m7/castle-garden-w01-object-aspect.txt`.
- The next M7 action is a valid Castle Garden renderer checkpoint, followed by
  a retry of W-01; no object-aspect claim is made.

## M7 W-01 Castle Garden checkpoint retry - 2026-08-01

- Kept Castle Garden/castle grounds as the only M7 representative scene.
- Reused the existing app-private checkpoint through the debug direct-path
  loader; native and developer-probe loads both returned `success=1`.
- Confirmed the harness detail that a paused state load leaves the previous
  presented framebuffer visible until a frame is rendered. The existing
  bounded capture route supplied a one-frame warmup before the final-primary
  screenshots.
- Native final-primary output was centered 4:3 at `1440x1080` with
  `FPS: 60`; the probe filled `1920x1080` with `FPS: 60` and exposed extra
  side terrain and foliage. Both outputs were upright.
- A center-region green-object mask measured `191x241` (ratio `0.7925`) in
  native and `195x247` (ratio `0.7895`) in the probe, an absolute ratio delta
  of `0.39%`.
- Decision: W-01 advances from `BLOCKED` to `PARTIAL`. This diagnostic mask
  is not yet the formal round/square reference-object proof, and a controlled
  camera repeat remains required. W-03 side culling, W-04 HUD, W-05 glyph,
  W-06 bottom geometry and W-20 transition proof remain open.
- Unrelated overlay output from one early capture was discarded; it is not
  evidence. No ROM, save, private screenshot, device identifier or credential
  material was retained.
- Evidence: `docs/evidence/m7/castle-garden-w01-object-aspect.txt`.

## M7 W-03 Castle Garden movement and culling probe - 2026-08-01

- Reused the Castle Garden checkpoint and held Slot-2 analog `x=1,y=0` for
  exactly 120 emulator frames, then neutralized the input.
- The Vulkan dense live `screen` burst completed `120/120` with contiguous
  frame IDs `845..964`, one-frame steps and no capture failures.
- All frames were opaque `256x384` RGBA images with `97,786..97,789`
  non-black pixels; no blank frame occurred. `59/119` adjacent pairs were
  byte-identical and `60/119` changed, matching the observed duplicate
  renderer cadence.
- The movement visibly changed the player pose, dust, camera and terrain. A
  final-primary endpoint remained upright and coherent with no visible black
  hole or broken edge.
- Important boundary: the burst is the complete native `256x192` top DS
  image, so it cannot itself prove pixels outside the final primary 4:3 safe
  rectangle. Internal edge-strip diagnostics were non-empty and changing on
  the left, but no semantic right-side animated landmark was established.
- A follow-up host-polled final-primary sample captured 16 `1920x1080`
  screenshots during the same movement. Both measured outer side ROIs were
  non-black in all samples and changed across all 15 adjacent pairs; the
  Android edge handle was excluded. The polling was not synchronized to
  explicit emulator-frame acknowledgements, and no single identifiable
  landmark was tracked across those exact frames.
- A clean warmed exact-step follow-up then captured 30 final-primary
  `1920x1080` screenshots, one after each successful `STEP_FRAMES 1`
  acknowledgement. All 30 frames were fully rendered, had
  `2,073,600` non-black pixels, and none of the 29 adjacent pairs was
  byte-identical. Subsampled left and right outer-ROI measurements changed
  in all 29 pairs. The camera pan moved castle wall, trees, hill and path
  through the side regions, but no single landmark stayed wholly inside one
  side ROI for the whole sequence.
- Decision: W-03 is `PARTIAL`, not a pass. The remaining gate is an
  exact-step final-primary sequence that tracks an identifiable animated
  landmark outside the mapped 4:3 safe rectangle across consecutive frames.
  W-01
  formal round/square repeat, W-04 HUD, W-05 glyph, W-06 bottom geometry and
  W-20 transition proof remain open.
- No ROM, save, private screenshot, device identifier or credential material
  was retained. Bob-omb Battlefield remains out of scope.
- Evidence: `docs/evidence/m7/castle-garden-w03-culling.txt`.

## M7 W-06 Castle Garden bottom-primary raster probe - 2026-08-01

- Reused the app-private Castle Garden checkpoint and requested a single live
  Vulkan capture with only `packedbottom`.
- The receiver returned `success=1`; the log reported
  `packed_bottom_primary` at `256x192` with `49,152` pixels.
- All `49,152` pixels were opaque, `48,635` were non-black, alpha ranged from
  `255-255`, and no magenta diagnostic pixels were present.
- Filtered device logcat contained no `FATAL EXCEPTION`, `ANR in` or `am_anr`.
- Decision: W-06 advances to `PARTIAL` for measured native bottom-raster
  geometry. This does not prove final secondary-panel placement, scaling,
  orientation or physical aspect; that display gate remains open.
- Evidence: `docs/evidence/m7/castle-garden-w06-bottom.txt`.

## M7 W-20 Castle Garden world-pause-world transition - 2026-08-01

- Loaded the Castle Garden checkpoint through the existing direct debug route
  and resumed without replaying the title or intro.
- A direct `TAP_INPUT` START command returned `success=1`; the next
  `1920x1080` final-primary screenshot showed the readable
  `CASTLE SECRET STARS` pause overlay and `TOUCH TO SELECT`.
- A second START command also returned `success=1`; the following
  final-primary screenshot returned to the Castle Garden world.
- Both world screenshots were fully non-black and all three screenshots were
  fully opaque. Filtered logcat contained no `FATAL EXCEPTION`, `ANR in` or
  `am_anr`.
- A short host-polled soak added three pre-pause samples, ten pause samples
  and ten return samples at approximately 50 ms intervals. Every sample was
  opaque and non-black; after the second START, the first sample still showed
  the overlay and the following nine showed the Castle Garden world.
- Decision: W-20 advances to `PARTIAL_WITH_OBSERVED_TRANSITION`. A real
  world-to-pause-to-world transition is now observed and the short soak found
  no black or transparent intermediate; longer no-flash/soak cadence remains
  open.
- Evidence: `docs/evidence/m7/castle-garden-w20-transition.txt`.

## M10 ARM9 overclock policy foundation - 2026-08-01

- Added the Android-free ARM9 policy with explicit `UNSUPPORTED`,
  `PLUMBING_ONLY`, `EXPERIMENTAL` and `VALIDATED` capabilities and the
  allowed requests `100/125/150/175/200`.
- Invalid requests, safe mode, unsupported/plumbing-only capability and
  requested Hardcore resolve to effective 100. Current live planner plumbing
  remains `PLUMBING_ONLY`; `ARM9_OC_CORE_SUPPORT` is not published in live
  enhancement capabilities.
- Added backward-compatible preference decoding/defaults, resolved requested
  and effective values plus capability to session plans, deterministic hashes
  and diagnostics. The planner passes the effective value to the existing
  RetroAchievements policy resolver.
- Focused validation passed: `Arm9OverclockPolicyTest` 5/5,
  `ProfileEngineTest` 16/16, `RetroAchievementsPolicyTest` 10/10 and
  `EmulatorViewModelLaunchPolicyTest` 1/1. `git diff --check` passed.
- The unexcluded Gradle task remains blocked by the empty fresh-worktree
  `melonDS-android-lib` checkout; the focused rerun excluded only the
  unrelated Vulkan/librashader pre-build tasks. No submodule content was
  changed.
- Evidence: `docs/evidence/m10/policy-foundation.txt`. M10 remains
  `IN_PROGRESS`; M7 remains `IN_PROGRESS`, M8 remains `NOT_STARTED`, and full
  M9 acceptance remains open. No commit was created.

## M11 session status snapshot - 2026-08-01

### Changes

- Added a `SessionStatusSnapshot` to `EmulatorSession` and latched it once
  from the resolved ROM launch plan.
- Added the snapshot to the ROM pause menu and rendered profile integrity,
  effective ARM9 percentage and effective RetroAchievements mode above the
  existing pause actions.
- Kept firmware and default pause menus on the existing item-list path; no
  policy recomputation was added to pause handling.
- Session reset clears the snapshot and a subsequent ROM launch creates a
  fresh one; a runtime Reset within the same ROM session keeps the latched
  values.

### Validation

- Focused M11/M10 JVM validation: 34 tests, 0 failures and 0 errors.
- Full SPIR-V check, GitHubProdRelease unit tests and GitHubProdDebug APK
  build: `BUILD SUCCESSFUL`, 145 actionable tasks.
- The debug APK installed on the connected Thor.
- A fresh ROM launch and a second launch after process restart showed the
  physical pause status `Profile: Enhanced`, `ARM9: 100%` and
  `RetroAchievements: Off`, with existing menu actions still present.
- The Thor pause list scrolled to the existing Reset and Exit actions; a
  runtime Reset kept the same status because the ROM session remained active.
- Filtered logcat after the clean physical ROM run had no FATAL EXCEPTION,
  ANR in or am_anr.
- The direct firmware launch did not yield a usable firmware session, so the
  physical firmware pause branch remains open and is recorded as a bound.

### Decision

`PASS_WITH_BOUNDS` for the bounded M11 session-status UI contract. Full M11
remains `IN_PROGRESS`; native ARM9 execution, broader UI/telemetry work and
the firmware physical branch are not claimed. M7 Castle Garden remains the
active release blocker, and Bob-omb Battlefield is out of scope.

Evidence: `docs/evidence/m11/session-status-pause.txt`.

## M12 release preflight inventory - 2026-08-01

- Recorded the current public repository, publication-safety, build/install,
  M7, M9, M10 and M11 gates as an explicit PASS/BLOCKED/OPEN matrix.
- Confirmed the public source is `joeblack2k/ThorDS` with `main` as the
  default branch and `upstream` preserved for MelonDualDS synchronization.
- Corrected the publication row to distinguish a clean current tree from the
  inherited public history: three deleted upstream firmware/romlist binary
  paths remain reachable, alongside the known NIST AES test-vector finding.
  No history rewrite or force-push was attempted.
- Kept M7 Castle Garden as the only representative scene. W-01 remains
  BLOCKED by the missing valid renderer checkpoint and black paired layers;
  W-20 is now `PARTIAL_WITH_OBSERVED_TRANSITION` after the direct
  final-primary world-pause-world sample; longer no-flash/soak coverage
  remains open.
- Kept M9 and M10 policy foundations separate from their unimplemented
  native/runtime/network/physical gates. M11 is PASS_WITH_BOUNDS with the
  firmware physical branch still open.
- Decision: M12 preflight result is `BLOCKED`; no production release or M8
  completion claim is made.

Evidence: `docs/evidence/m12/release-preflight.txt`.

## G0 state reconciliation - 2026-08-01

### Reconciliation

- Imported the additive 40-file ThorDS Green Pass dossier after verifying every
  manifest SHA-256.
- Confirmed live `HEAD` and public `origin/main` at the documented
  `a2aa88e5` baseline; the public default branch remains `main`.
- Preserved `upstream` for MelonDualDS synchronization and verified the
  initialized submodules. The pinned core commit is reachable from its public
  upstream branch.
- Kept the owner-requested `melon.zip` artifact untracked and outside the G0
  documentation commit.

### Device and ROM

- Confirmed the connected device class as AYN Thor and both expected physical
  display roles. Android display metadata alone does not resolve the owner's
  observed 180-degree top-screen rendering defect.
- Confirmed the local ROM is still ignored, unchanged and exactly ASMP,
  revision 0, with the documented RetroAchievements system hash.

### Safety

- Tracked and reachable-history ROM/save extension scans are empty.
- The tracked sensitive-name scan contains only the reviewed
  `AndroidKeystoreOfflineLedgerSigner.kt` source file, with no key material.
- Existing public-history bounds remain unchanged: deleted upstream firmware
  and romlist binaries plus the documented NIST AES test-vector false positive.
  G0 does not rewrite history or force-push.

### Decision

G0 is `PASS`. The active workstream is G1 analog closeout, followed by G2
Castle Garden proof closeout. Evidence:
`docs/evidence/g0/state-reconciliation.txt`.

## G1 deterministic analog and lifecycle slice - 2026-08-01

### Implementation

- Routed debug joystick samples through the live `EmulatorActivity` and
  production `InputProcessor`, with an injectable Slot-2 sink for focused
  tests.
- Added explicit input release on activity pause, destroy and input-pipeline
  replacement. Camera/D-pad ownership, digital fallback state and Slot-2
  analog all return to neutral.
- Cleared native Slot-2 atomics during emulator reset and stop.
- Kept debug startup metadata in the debug source set so release lint and
  unit-test assembly do not depend on a debug-only manifest entry.
- Added a bounded app-private sweep and gameplay-trial command. Public
  evidence stores only aggregate values.

### Validation

- `git diff --check`: PASS.
- Full SPIR-V, GitHubProdRelease unit-test and GitHubProdDebug APK gate:
  `BUILD SUCCESSFUL`.
- Final APK SHA-256:
  `cab47c10ea7b9388cf580a46ebaae4d1ea10a2f916de500bf7cbc69168213a3e`.
- Exact APK install on the connected AYN Thor: PASS.
- Live sweep: 64/64 samples, 16 directions, magnitudes
  `0.25/0.50/0.75/1.00`, deadzone boundary, HAT D-pad plus right-stick camera,
  all-neutral and pipeline recreation: PASS.
- Same-checkpoint Castle Garden low/mid/full movement and right-stick camera
  trials each advanced 30 renderer frames and produced distinct world/map
  response above the neutral control.
- Original relaunch: zero curated codes, Slot-2 off, camera off. Enhanced
  relaunch: one curated analog code, Slot-2 on, camera on.
- Filtered device logcat: no fatal exception, ANR or native fatal signal.
- The connected Android test source compiled, but the existing instrumented
  runner failed before test discovery because `androidx.tracing.Trace` is
  absent. After the required method change, the in-app Thor sweep supplied
  the device integration proof without adding a test-only dependency.

### Decision

The bounded deterministic G1 implementation slice is `PASS`; M6 and Analog
remain `PARTIAL`. Swim/fly/slide and usable physical play are not witnessed
while the owner-observed top screen remains 180 degrees inverted and gameplay
is approximately 20fps. G2 deterministic renderer instrumentation is the next
independent workstream. Evidence:
`docs/evidence/m6/analog-end-to-end.json`.

## G2 deterministic presenter and physical-surface instrumentation - 2026-08-01

### Implementation

- Added an opt-in presenter metadata buffer capped at 512 successful presents.
  It records only frame/timestamp, role, rect, draw mode and source-state
  metadata; no pixels or physical display identifiers enter the trace.
- Added a debug-only world-pause-world command that writes the bounded trace to
  app-private storage.
- Added exact-step in-app PixelCopy sequences for the main and secondary
  emulator surfaces, paired with the internal 3D or bottom source raster.
- Replaced the old resume-and-poll debug stepping with the existing native
  `debugStepFrame()` primitive. Four requested frames then advanced exactly
  `317 -> 318 -> 319 -> 320 -> 321`.
- Added dependency-free presenter and PNG geometry analyzers with synthetic
  self-tests.

### Validation

- Both analyzer self-tests: PASS.
- GitHubProdDebug APK build: PASS.
- APK SHA-256:
  `5a7be439d4be0ea91ae768297981e4085ab02229c222be3837ec9ef732d86542`.
- Exact APK install on the connected Thor: PASS.
- Internal world-pause-world trace: 192/192 records, 0 sequence gaps,
  0 frame regressions and 0 consecutive duplicate frame ids.
- Main PixelCopy: exact native frame steps with two successful presenter
  records per capture.
- Secondary PixelCopy: paired `256x192` source and `1240x1080` final output;
  active rect `x=0,y=75,width=1240,height=930`, rotation off and exact 4:3.
- Source-to-final lower output was pixel-identical after explicitly accounting
  for the native debug source's red/blue byte order.
- Lower bomb-body and digit-4 aspect deltas were both `0.0%`.
- Current 3x3 lower touch grid and both black-bar exclusions: PASS.

### Decision

W-06 is `PASS`. W-04/W-05 lower reference measurements pass but their top
UI-safe-plane gates remain `PARTIAL`. W-20 world-pause-world passes, while the
painting/star and pixel black/stale portions remain `PARTIAL`. W-01 and W-03
remain `PARTIAL`; no M7 or M8 completion claim is made.

Evidence:
`docs/evidence/m7/presenter-trace-analysis.json`,
`docs/evidence/m7/surface-geometry-analysis.json` and
`docs/evidence/m7/w06-touch-grid.json`.
## 2026-08-01 - M11 ROM-details resolved profile status

### Result

- Added a read-only ThorDS profile status to the existing ROM-details
  configuration screen.
- The status is resolved from the ROM header identity, the embedded profile
  catalog, the existing hash-bound `ProfilePreferencesRepository` and the
  existing `ProfileLaunchPlanner`.
- The UI shows the resolved profile, match classification, profile integrity,
  effective ARM9 percentage, effective RetroAchievements mode and effective
  widescreen mode.
- Missing ROM identity does not claim an Enhanced profile; the status remains
  absent until resolution succeeds.
- Added per-ROM Original/Thor Enhanced and RetroAchievements mode controls.
  These write the existing hash-bound preferences and are applied by the
  normal launch path after relaunch.
- Profile changes now show an explicit relaunch-required message in the ROM
  details screen.
- Profile edits remain pending in the details ViewModel and are committed only
  from `onRomValidated`, so leaving the screen or failing validation does not
  change the persisted launch preference.
- Physical Thor witness: long-pressing the SM64DS ROM card and selecting
  `Details` opened `RomDetailsActivity`; the resolved profile rows, pending
  Enhanced toggle, relaunch toast and RA Off/Casual/Hardcore dialog were
  visibly usable on the 1920x1080 display. The pending edit was abandoned
  without pressing Play.
- Confirmed the same flow end to end: Enhanced was selected, `Play` passed
  ROM validation, `EmulatorActivity` started, and the launch log recorded
  effective Enhanced/Casual/ARM9-100 state. After stopping and reopening
  details, the persisted status still showed Thor Enhanced and
  `MATCH_EXACT`.
- No second settings store, ROM database field or new toggle was introduced.

### Validation

- `:app:compileGitHubProdDebugKotlin`: PASS.
- Focused release unit tests: PASS; 32 tests, 0 failures, 0 errors.
- Added planner regression coverage proving `selectedProfileId` is honored;
  the focused profile suite is now 6 tests and remains green.
- `:app:assembleGitHubProdDebug`: PASS.
- APK installed on AYN Thor; launcher reached `RomListActivity`.
- Filtered logcat showed no `FATAL EXCEPTION` or `ANR in`.

### Boundary

- This closes the bounded M11 status/control slice, including the physical
  Enhanced relaunch witness.
- M9 RetroAchievements is already closed as `PASS`; the owner-confirmed real
  Casual unlock must not be repeated. The broader M11 acceptance remains
  open for the missing Analog, requested/effective widescreen, ARM9 ratio,
  60 FPS labeling, conflict-recovery and full physical-flow gates.
### 2026-08-01 - M13 F1 semantic execution monitor

- Added the core monitor at actual ARM9 JIT dispatch and interpreter execution
  boundaries, with nine independent EU target counters.
- Added a debug-only JNI/ADB enable and JSON dump path.
- Thor title-screen run produced VBlank `66 -> 133` in approximately one
  second; all semantic gameplay targets were zero as expected for the title.
- The slot-0 state path was unavailable after reinstall, so no gameplay claim
  was made.
- Evidence: `docs/evidence/m13/f1-thor-semantic-run.json`.

### 2026-08-01 - M13 ARM9 profile and compatible state load

- Fixed the debug profile-change path so the current ROM is relaunched after
  the hash-bound ARM9 preference changes; runtime settings refresh alone cannot
  rebuild launch-time configuration.
- Promoted the existing ARM9 scheduler plumbing to the explicit
  `EXPERIMENTAL` capability used by the profile planner. `VALIDATED` remains
  reserved for completed timing and hardware evidence.
- Thor evidence: `requestedArm9=125 effectiveArm9=125`, native telemetry
  `percent=125`, and a compatible private state loaded with `success=1`.
- Semantic telemetry after the load remained monitor-live with VBlank count
  `1786`; no gameplay cadence claim is made from this state-load gate.
- Evidence: `docs/evidence/m13/f1-arm9-profile-state8.json`.
- Next: F1 gameplay witness and F2 cadence-consumer classification.

### 2026-08-01 - M13 F2 cadence classifier refinement

- Refined `tools/thords/60fps/scan_cadence_consumers.py` to classify the
  inventory into scheduler, message/HUD, render/OAM, scene-update, boot/init,
  animation, physics, timer and scene-specific groups.
- Re-ran the EU inventory: 192 findings; 16 low-level `func_...` entries remain
  explicitly `unresolved-function` for manual decomp review.
- The explicit file mapping now resolves those entries to timer, scheduler,
  message/HUD, render/OAM, scene-update and boot/init categories. The generated
  inventory has no `unknown` or `unresolved-function` category.
- F2 remains open for semantic/manual review of the classified consumers and
  still does not authorize a cadence patch.

### 2026-08-01 - M13 F2 classification output consistency

- Regenerated `docs/evidence/m13/cadence-consumers.json` and its Markdown view
  from the pinned EU source tree after fixing the scanner to classify relative
  source paths consistently.
- The inventory remains 192 findings with the same source-tree SHA-256:
  `1a9a06cd51443f7f28fda8c35edbc276fc8a6074aaf2e9b49c4340e14d490499`.
- A self-check confirms there are no generic `unknown` or
  `unresolved-function` findings. This is classification evidence only; F2
  semantic review and 60fps runtime validation remain open.

### 2026-08-01 - M13 cadence write candidates

- Generated `docs/evidence/m13/cadence-writes.md` from the pinned EU cadence
  inventory; it contains 18 source-level write candidates.
- The list is deliberately not treated as a patch manifest. Pointer casts,
  overlay-local declarations and incomplete symbol typing require binary and
  runtime confirmation before F4 changes any word.

### 2026-08-01 - M13 private checkpoint retry

- Reached the connected Thor with four existing private M7 cache checkpoints
  and retried them through the debug `LOAD_STATE` API. Their local filenames
  are not published.
- All four loads returned `success=0`. The running display was black afterward.
- The game-loop sampler remained valid at 60 updates/s with `cadence=1`, but
  `stageTimer=0` and the non-gameplay surface mean this is not F1 gameplay
  evidence and does not advance the 60fps gate.
- Evidence: `docs/evidence/m13/f1-thor-cache-checkpoint-attempt.json`.

### 2026-08-01 - M13 legacy savestate format root cause

- The rejected legacy checkpoint was inspected through the current runtime
  error path. The file has valid `MELN` and `NDSG` sections, but its config word
  is `0x00000000`.
- The current ThorDS runtime requires `0x00020000`, which records the ARM9
  overclock savestate format. The runtime reports:
  `Expected config word 00020000, got 00000000. cannot load.`
- This is a format/layout incompatibility, not a bad path or a corrupted ROM
  state. A header-only edit would be unsafe because the subsequent serialized
  fields differ.
- F1 still needs a fresh current-format gameplay state. Evidence:
  `docs/evidence/m13/f1-legacy-state-format.json`.

### 2026-08-01 - M13 current savestate round-trip

- Saved the currently running Thor session using the active runtime and
  ARM9=125 profile; save returned `success=1`.
- Reloaded the newly created current-format state; load returned `success=1`.
- This confirms the current savestate format and ARM9 compatibility path. The
  display was still black with `stageTimer=0`, so it is not gameplay evidence.
- Evidence: `docs/evidence/m13/f1-current-state-roundtrip.json`.

### 2026-08-01 - M13 Original versus Enhanced black-output isolation

- Temporarily ran the same private ROM at ARM9=125 with
  `original.sm64ds.eu` and no enhancements, then compared it with the
  Enhanced profile and true-widescreen enabled.
- Both runs produced black DS-primary output. Enhanced had three curated codes;
  Original had zero. The original Enhanced preferences were restored after the
  test.
- This excludes M8 true-widescreen as the sole cause. F1 gameplay evidence and
  60fps validation remain open.
- Evidence: `docs/evidence/m13/f1-original-vs-enhanced-black-output.json`.

### 2026-08-01 - M13 Vulkan versus software black-output isolation

- Ran the same ROM and ARM9=125 profile with the Vulkan renderer and then the
  software renderer.
- Both renderer paths produced black DS-primary output. Vulkan was restored
  afterward and the Enhanced profile preferences remain restored.
- This excludes Vulkan as the sole cause. F1 gameplay evidence and 60fps
  validation remain open.
- Evidence: `docs/evidence/m13/f1-renderer-ab-black-output.json`.

### 2026-08-01 - M13 debug-latch black-output isolation

- Disabled the runtime `video_renderer_debug_tools_enabled` preference for one
  clean launch with Enhanced, Vulkan, true-widescreen and ARM9=125.
- The DS surface remained black. The original preference file was restored
  afterward, including the prior debug-tools value.
- This excludes the renderer debug latch as the sole cause. F1 gameplay
  evidence and 60fps validation remain open.
- Evidence: `docs/evidence/m13/f1-debug-tools-black-output.json`.

### 2026-08-01 - M13 F3 developer cadence probe definition

- Added the hidden, default-off exact-profile `60fps-dev-cadence` definition for
  ASMP revision 0 / RA hash `ba3c4052e00c5cc31df5d5534c39de1b`.
- The probe uses the documented guarded `2 -> 1` cadence override, requires a
  full relaunch and is marked experimental. It is not exposed as a product
  toggle and is not validated.
- Updated profile-engine regression expectations for the current ARM9
  `EXPERIMENTAL` capability and verified the new probe remains disabled by
  default.
- Focused GitHubProdRelease profile unit tests: PASS, 17 tests.

### 2026-08-01 - M13 F3 probe wiring and relaunch validation

- Added a debug-only `SET_SM64DS_CADENCE_PROBE` action bound to the exact
  ASMP/EU/hash identity. It writes the existing hash-bound enhancement map and
  requests a full current-ROM relaunch.
- Fixed `ProfileLaunchPlanner` to preserve hash-bound enhancement preferences
  instead of replacing them with a temporary widescreen-only map.
- Thor log evidence changed `curatedCodes=3` to `curatedCodes=4` after enabling
  the probe, with `requestedArm9=125` and `effectiveArm9=125` preserved.
- Disabled the probe again after the wiring check; no product 60fps claim was
  made because the device was still at course selection, not gameplay.
- Evidence: `docs/evidence/m13/f3-cadence-probe-activation.json`.

### 2026-08-01 - M13 game-loop telemetry debug bridge

- Exposed the existing native one-second sampler through the debug-only
  `DUMP_SM64DS_GAME_LOOP` action.
- The JSON schema reports validity, unique updates, emulator frames, cadence
  value, stage timer and camera behavior calls.
- A fresh title-screen launch correctly returned `valid=false`; no gameplay
  baseline is claimed from title/menu state.
## 2026-08-01 - M13 gameplay cadence baseline after intro

- Confirmed that the apparent white/black hang was the SM64DS intro waiting for
  a tap on the lower screen. After the lower-screen tap, the AYN Thor reached
  visible Castle Garden gameplay.
- Captured native game-loop telemetry from that live gameplay state:
  approximately 60 emulator frames per second but only 30 unique SM64DS
  updates per second, with `cadence=2`.
- The visible 60 FPS overlay is therefore presentation cadence, not proof of
  60 FPS game logic. Evidence: `docs/evidence/m13/f2-gameplay-30fps-baseline.json`.
- M13 60 FPS remains open. The next implementation target is the guarded
  game-update cadence path, followed by movement/camera and stability
  acceptance on the Thor.
## 2026-08-01 - M13 reusable private gameplay checkpoint

- Created a private save state after advancing past the SM64DS intro; its local
  app-private path is not published.
- Loaded the same checkpoint through the debug receiver and received
  `success=1`; the intro no longer needs to be replayed for each test.
- The state remains device-private. No ROM, save-state bytes or private capture
  were copied into the repository.
- Evidence: `docs/evidence/m13/f1-gameplay-savestate-roundtrip.json`.
## 2026-08-01 - M13 60 FPS gameplay checkpoint

- Enabled the exact EU SM64DS guarded cadence probe and loaded the reusable
  gameplay checkpoint.
- Created and round-tripped a second private cadence checkpoint; its local
  app-private path is not published.
- A 15-second native telemetry window reported 60 or 61 unique game updates
  per second in all steady-state windows, with one 51-update load/warm-up
  window. No crash or ANR was observed.
- This is the first strong Thor runtime evidence for the requested 60-FPS
  gameplay cadence. Product acceptance remains open for movement, camera,
  audio, long-run stability and release gates.
- Evidence: `docs/evidence/m13/f2-60fps-gameplay-checkpoint.json`.
## 2026-08-01 - M13 independent input response smoke tests

- Loaded the private 60-FPS gameplay checkpoint for each case and ran the
  existing renderer-frame surface harness independently for left-stick movement
  and right-stick camera input.
- Left-stick case: all 4 input events handled, 4/4 frames advanced, 4 distinct
  source frame hashes.
- Right-stick camera case: all 4 input events handled, 4/4 frames advanced,
  4 distinct source frame hashes.
- The harness reports `PARTIAL` because optional Vulkan presenter metadata did
  not complete; the input and rendered-frame measurements themselves passed.
- Physical direction/feel and longer camera behavior remain a manual Thor gate.
- Evidence: `docs/evidence/m13/f3-input-response-60fps.json`.
## 2026-08-01 - M13 right-stick camera route audit

- Audited the active camera path from Android `AXIS_Z/RZ` through
  `InputProcessor.updateProfileCamera` and `MelonEmulator.setSlot2CameraState`
  into the native Slot-2 camera interface.
- Confirmed that profile camera axes are excluded from the Slot-2 analog
  movement mapping, so the left stick and right stick do not share the same
  analog movement path.
- Repeated a camera-only Thor run from the private 60-FPS checkpoint:
  input handled, 4/4 frames advanced, 4 distinct rendered-frame hashes.
- Automated evidence cannot establish physical axis direction or feel; that
  remains a manual Thor check.
- Evidence: `docs/evidence/m13/f3-camera-route-audit.json`.
## 2026-08-01 - M13 60 FPS stability window

- Loaded the private 60-FPS gameplay checkpoint and ran a 60-second native
  telemetry window on the AYN Thor.
- Observed 59 complete windows, all at 60 or 61 unique updates per second,
  minimum 60, maximum 61, mean 60.61, with cadence value `1` throughout.
- Emulator-frame counts matched the unique-update counts and no crash or ANR
  was found.
- Evidence: `docs/evidence/m13/f4-60fps-stability-window.json`.
## 2026-08-01 - M13 camera stress harness failure and recovery

- A 60-frame forced camera-input surface sequence produced a black output with
  a horizontal noise band and an Android ANR.
- Logcat showed 120 dropped Vulkan presents during the overloaded harness run.
- Force-stopped and relaunched ThorDS, then reloaded the private 60-FPS
  checkpoint; normal gameplay output returned.
- This is a failed debug-stress run, not a release result. It exposes a
  harness/presentation robustness gap that must be isolated before using long
  forced camera sequences as acceptance evidence.
- Evidence: `docs/evidence/m13/f4-camera-stress-anr.json`.
## 2026-08-01 - M13 realtime camera trial harness

- Added a debug-only realtime camera trial that injects one right-stick state
  while emulation continues normally, waits for a bounded hold interval, then
  neutralizes the state.
- Built and installed `GitHubProdDebug` on the Thor.
- Camera hold completed with valid frames and no ANR; neutral control also
  completed with valid frames and no ANR.
- Pixel deltas alone did not prove camera movement because normal gameplay
  animation produced comparable changes. The new harness is stable, but a
  stronger camera-specific observable is still needed.
- Evidence: `docs/evidence/m13/f4-live-camera-trial.json`.
## 2026-08-01 - M13 native camera-state telemetry

- Added native/JNI telemetry for the transient Slot-2 camera protocol state.
- Rebuilt and installed `GitHubProdDebug` on the Thor.
- A realtime right-stick hold reported native state
  `yawInputQ12=4096`, `yawUnitsPerTick=1001`, `flags=3`; input and frame shape
  were valid and no ANR occurred.
- The neutral control reported `yawInputQ12=0` while retaining the protocol
  configuration, confirming that the input value is cleared after release.
- This proves the Android input reaches the native camera protocol. It does
  not yet prove the game-side camera patch visibly rotates the view or establish
  physical axis direction.
- Evidence: `docs/evidence/m13/f4-native-camera-state.json`.
## 2026-08-01 - M13 game-side camera hook failure isolated

- Added game-side camera telemetry for the EU camera pointer and yaw fields:
  `data_0209F318`, `camera+0x180`, `camera+0x182` and `camera+0x184`.
- Verified on the Thor that the hook word at `0x0200BCF0` is active and
  branches to the payload at `0x02075BB4`.
- During a valid native yaw hold (`yawInputQ12=4096`,
  `yawUnitsPerTick=1001`, `flags=3`), the camera pointer remained
  `0x0217E080`, but current yaw and yaw offset stayed zero.
- The failure boundary is now the SM64DS game-side hook/protocol consumption;
  Android input and the native camera-state bridge are no longer the blocker.
- Evidence: `docs/evidence/m13/f4-game-side-camera-hook.json`.
## 2026-08-01 - M13 camera protocol read boundary

- Added a runtime-only counter for `CartAnalog` mode-2 protocol reads.
- On the Thor, a valid native yaw hold (`yawInputQ12=4096`,
  `yawUnitsPerTick=1001`, `flags=3`) produced zero mode-2 protocol reads
  before and after the hold.
- The active hook word remained present, but the hook path did not consume the
  protocol. This narrows the defect to the exact hook entry/overlay execution
  path rather than the Android or native protocol bridge.
- A local research ARM9 binary was rejected as a patch source because its bytes
  do not match the exact loaded EU runtime layout.
- Evidence: `docs/evidence/m13/f4-camera-protocol-read-boundary.json`.

## 2026-08-01 - M13 paired semantic cadence and 2x-speed failure

- Removed the misleading `0x020199A4` scheduler marker after static review
  showed it is a conditional special-state loop, not the ordinary gameplay
  scheduler.
- Added exact-PC counters for `Scene::BeforeBehavior` at `0x0202E3D4` and
  `Scene::BeforeRender` at `0x0202E3A4`; existing counter indices remain
  stable.
- Verified both addresses at the product decomp pin and audit commit
  `755f0be5b9658e5f75871c4138ddc0133a2c07c4`; the audit changes only shared
  type declarations/readability in these functions.
- On the same private Castle Garden checkpoint, cadence 2 produced one Stage
  and Scene behavior/render pass per two VBlanks (`306/612`), while the guarded
  cadence 1 probe produced one pass per VBlank (`615/615`). The independent
  sampler showed `31/61` updates/frames versus `60/60`. ARM9 at effective 125%
  had no target debt.
- The owner physically observed approximately 2x gameplay speed with the probe
  enabled. This proves real semantic doubling but fails correct-speed product
  timing; F4 fixed-step movement, physics, animation, particles, timers and
  audio remain red.
- Disabled the probe, relaunched, restored the private normal-speed checkpoint
  and confirmed cadence 2 with 30 unique updates across 61 emulator frames.
  No crash or ANR was found.
- GitHubProdRelease unit tests and the GitHubProdDebug APK build passed. The
  installed APK SHA-256 is
  `ba9bcfebffedc93d82cbbfbbfa3b8b5cf3bce86e9bfda75199d64137c1a328ea`.
- Evidence: `docs/evidence/m13/f3-stage-dispatch-paired.json`.

## 2026-08-01 - M13 F4 player correct-speed timestep

- Reproduced the owner's 2x-gameplay report from one private Castle Garden
  checkpoint. At cadence 1, two debug steps advanced the player animation by
  8192 versus 4096 at normal cadence.
- Added exact-original guarded patches for player movement, player timers and
  `Animation::Advance`. The animation hook uses the proven main-loop parity
  only when the game cadence value is 1; cadence 2 executes the original
  function unchanged.
- Rejected the first quarter-step movement result after the real game-loop
  counter exposed a misleading renderer-frame comparison. The final x/z
  half-step measured 457.5 units against the 435.0 normal baseline, a 5.17%
  difference within the predeclared 10% acceptance bound.
- Player timer drop measured 60 against the normal 61, and player animation
  advance measured 4096 against the normal 4096. The previously observed 8192
  animation advance is gone.
- Added deterministic builder/verifier checks for all hook originals, both
  zero-filled payload reservations, the movement half-step instructions,
  animation skip-return and original continuations. Static patch tests,
  GitHubProdRelease unit tests, GitHubProdDebug build and Thor install passed.
- Installed APK SHA-256:
  `da51076d4286efc6b5e3883f45c03cfee74125a45fba1ed9ebc76a08dfeea8a0`.
  Fast-forward and semantic tracing were disabled after measurement; the
  60fps profile was left enabled for owner play validation.
- F4 remains partial for particles, non-player actors, cutscenes and audio.
  RA Casual and the 60-minute stability gate also remain open.
- Evidence: `docs/evidence/m13/f4-fixed-timestep-player.json`.

## 2026-08-02 - M13 F4 vertical timestep closeout

- Reused private gameplay save states to compare the exact same rising and
  falling player states at normal cadence and with the guarded 60fps patch.
  No ROM or save-state bytes were copied into the repository.
- Corrected the vertical half-step after review found that the earlier patch
  had not proven jump/fall parity. The final v5 payload halves vertical
  acceleration and displacement, adds an acceleration-eighth position term,
  and restores the full physical speed and acceleration after each update.
- Rising endpoints match exactly: both paths reached Y `367.683838` with
  vertical speed `20.395996`. Falling endpoints were Y `373.667969` normal
  versus `373.666992` enhanced, a `0.000977` position difference and `0.0136%`
  displacement difference.
- The guarded payload now occupies the complete 248-byte reservation. Static
  verification checks the two vertical correction instructions and every
  relocated continuation branch. Patch SHA-256:
  `9175547b75ca2cdc72aaa338e46f46c579a2cf7b956a32fa513bc19ce8a132d9`.
- GitHubProdRelease unit tests, GitHubProdDebug build and Thor install passed.
  Installed APK SHA-256:
  `409dc12a42fdc34db9538719e146e3507b997e68ed3ede513d16a91a0422c0ad`.
- F4 player horizontal movement, vertical physics, timers and animation are
  green. Particles, non-player actors, cutscenes, audio, RA Casual and the
  60-minute stability gate remain open.
- Evidence: `docs/evidence/m13/f4-fixed-timestep-player.json`.

## 2026-08-02 - M13 F4 v6 review correction and finalization

- Reviewed and finalized runtime id `sm64ds.eu.60fps-dev-cadence.v6`.
  Action Replay and profile code SHA-256 are both
  `4155b9ef2c9de2688f05ab06a9845cd69a20a49f1c919db8b9ae9c113426deea`.
  The player payload is 248 bytes and the animation payload is 48 bytes.
- Corrected the animation description to the Player-specific animation driver
  at `0x020BEDD4`, continuation `0x020BEDD8`. Global `Animation::Advance` is
  no longer patched. Four player timestep hooks, the Player animation driver
  and cadence original 2 are one fail-closed region; independently tampering
  every guard produced zero writes in the AREngine-semantic regression model.
  Malformed structures are rejected and every valid write includes cadence 1.
- Final v6 hardware animation window: cadence 1 counter `5027` to `5031`,
  four enhanced updates; body and auxiliary animation frames `143360` to
  `151552`, delta `8192`, equal to two original updates at `4096` each.
  `playerAnimationHookWord=0xEAFD1749` was observed active.
- Final live Vulkan sample recorded 60 unique updates and 60 emulator frames
  in `1.001396458s`, cadence 1, all five v6 hook words active, fast-forward
  false and semantic monitor false. The five-second smoke had no crash, ANR or
  fatal signal.
- GitHubProdDebug clean build succeeded with `144/144` Gradle tasks executed;
  the focused GitHubProdRelease unit-test target passed. APK
  SHA-256 `9dccbdef33f6deac505fa1dfcd4f54e620adefe40f26255dabed90e4f448fde7`
  and installation succeeded. A save state was created before reinstall and
  slot 8 was refreshed after v6 activation; state bytes/path/device serial are
  not published. Cadence enable and disable always relaunch; the unsafe
  disable-without-relaunch path was deleted.
- F4 player horizontal, vertical, timers and Player animation are PASS.
  M13 remains PARTIAL: particles, non-player actors, cutscenes, audio
  continuity, RA Casual, broad gameplay behavior and 60-minute stability
  remain open. This v6 entry supersedes v5 hashes and hook claims for the
  final build; the preceding v5 entries remain historical attempts.
- Evidence: `docs/evidence/m13/f4-fixed-timestep-player.json`.

## 2026-08-02 - M13 F4 cadence-reset recovery

- Reproduced the owner's later 2x-speed report after a scene or player reset:
  all five v6 player hooks remained active, but cadence had returned to 2 and
  the sampler measured 30 unique updates across 61 emulator frames.
- Root cause: v6 correctly failed closed after installation because its guard
  expected the original hook words. Once the hooks were already patched, a
  later scene/overlay cadence reset could not re-enter the install region.
- Added runtime id `sm64ds.eu.60fps-dev-cadence.v7`. Region A remains the
  exact-original fail-closed installer. Region B is maintenance-only: it
  requires all five exact patched hook words, all 62 player payload words, all
  12 animation payload words and cadence 2, then writes only cadence 1. It
  never repairs hooks or payload and does nothing for cadence 1, cadence 3 or
  any incomplete state.
- The generated Action Replay words and profile code are byte-for-byte equal.
  Both canonical SHA-256 values are
  `991e679818b9ea2f2a766559e9fd541b818385b1f5ffc0f5e42c8e37583c99fd`.
  A regression test now prevents profile/builder drift.
- Saved the live fault state before reinstall. The clean build ran 144/144
  Gradle tasks, the focused GitHubProdRelease unit test and patch verifier
  passed, and GitHubProdDebug installed successfully. APK SHA-256:
  `0f3ceeab5be2c7b658a77e29ec5a97700c49d0a9a4b66969317d51f66be622c8`.
- Re-loaded the private fault fixture three times. v7 recovered to cadence 1
  with `60/60`, `60/61` and `60/60` updates/frames; a later live sample remained
  `61/61`. Fast-forward and semantic monitoring were off, the process remained
  alive and the crash/ANR/fatal scan passed.
- This closes the observed scene/save-state cadence reset, not full M13.
  Particles, non-player actors, cutscenes, audio continuity, broad gameplay,
  RetroAchievements Casual interaction and the 60-minute stability gate remain
  open. No ROM, save-state bytes, private path, device serial or private capture
  is published.
- Evidence: `docs/evidence/m13/f4-cadence-reset-recovery.json`.
