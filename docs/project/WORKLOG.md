# Worklog

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
