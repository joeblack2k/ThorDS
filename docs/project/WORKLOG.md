# Worklog

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
