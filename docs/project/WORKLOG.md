# Worklog

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
