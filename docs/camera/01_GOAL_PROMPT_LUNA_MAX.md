# GOAL — Implement the ThorDS Smooth Orbit Camera on Luna Max

Work in the existing ThorDS repository and continue from the current worktree.

## Mandatory first message

In your first response, state explicitly:

> Use at most 3 parallel subagents where useful. Keep subagents bounded and preferably read-only for source/decomp, core/JNI, and test/audit work. Work as the Luna Max lead agent yourself, wait for their results, inspect their evidence, and verify every conclusion independently before changing or publishing code.

Then begin work immediately. Do not stop after producing a plan.

## Primary product goal

Replace ThorDS's current SM64DS Enhanced right-stick implementation — which maps `AXIS_Z` / `AXIS_RZ` to digital DS D-pad inputs — with a real, continuous, proportional right-stick orbit camera.

The release behavior must be:

```text
Left stick      existing AM64DS Slot-2 analog movement
Right stick X   continuous horizontal camera orbit
Right stick Y   reserved and neutral in camera v1
R3              one-shot recenter behind the player
D-pad           original digital camera fallback when right stick is neutral
Touch camera    still available only in Original mode
Enhanced HUD    no touch-camera buttons or bouncing camera arrows
Normal yaw      no camera sound
R3 recenter     optional original recenter sound, default on
```

The exact supported game identity remains:

```text
System: NDS
Game code: ASMP
Revision: 0
RA system hash: ba3c4052e00c5cc31df5d5534c39de1b
```

Do not add this patch to any approximate, filename-only, region-only or hash-mismatched ROM.

## Source of truth and worktree preservation

Known public reference commits:

```text
ThorDS reference ancestor:
a7831c38c55e9eeef2376bb2390a99a108ab2bd0

Latest relevant input ancestor:
e95699aae96d0d5a86bdf332a514650a9619b4f9

SM64DS decomp source pin:
2307f06d9ce10e114fa00d2e9318d5161aaed311

AM64DS reference pin:
d3ae02560c32c402672036677e06e0df6e692fd1
```

These are not instructions to reset.

Before editing:

```bash
pwd
git status --short --branch
git remote -v
git rev-parse HEAD
git log --oneline --decorate -12
git fetch origin
git rev-parse origin/main
git merge-base --is-ancestor a7831c38c55e9eeef2376bb2390a99a108ab2bd0 HEAD || true
git submodule status --recursive
adb devices -l
```

Rules:

- preserve every newer local or remote commit;
- preserve unrelated worktree changes;
- do not reset, rebase away, overwrite, stash-and-forget or force-push;
- do not rebuild M0-M7 from scratch;
- do not reopen completed True Widescreen proof work;
- import this dossier safely and commit it separately only when the repository safety scan is green;
- use the current repository and device as the source of truth when they differ from an older dossier statement.

## Scope

This goal includes:

1. deterministic baseline instrumentation of the existing digital camera;
2. removal of digital right-stick D-pad synthesis from the Enhanced path;
3. a dedicated, transient right-stick camera configuration;
4. a backward-compatible Slot-2 camera register protocol;
5. Kotlin → JNI → native core state transport;
6. an exact European runtime patch derived from the pinned decomp and locally verified ROM;
7. continuous camera yaw inside the original normal orbit-camera routine;
8. one-shot R3 recenter;
9. suppression of both permanent touch-camera buttons and camera tutorial/bouncing arrows in Enhanced mode;
10. removal of ordinary-yaw camera sound by eliminating its trigger, never by globally muting sound IDs;
11. profile/settings/status integration;
12. unit, native, patch-generator, ADB and physical Thor validation;
13. bounded commits and public source updates.

## Non-goals

Do not implement in this goal:

- vertical free-look or pitch;
- a replacement camera engine;
- 60fps gameplay;
- ARM9 overclock implementation;
- new True Widescreen rendering behavior;
- new RA achievement logic;
- other games;
- a physically patched ROM;
- hash spoofing;
- broad melonDS input refactors unrelated to this exact profile.

The protocol must be ready for a future 60fps camera step and reserved pitch field, but those features stay inactive.

## Required architecture

### A. Frontend input

The Enhanced profile must no longer call `CameraDpadHysteresis` for ordinary right-stick motion.

Implement a pure, tested mapping:

```text
raw right stick
→ axis ownership
→ inversion
→ radial deadzone
→ radial rescale
→ response exponent
→ normalized yaw input [-1, +1]
→ Slot-2 camera state
```

Default values:

```text
deadzone: 0.12
response exponent: 1.50
max yaw speed: 165 degrees/second
invert X: false
recenter sound: true
R3: KEYCODE_BUTTON_THUMBR
```

When the smooth camera profile is active, reserve the physical right-stick axes before the generic axis-to-button mapper so an old user mapping cannot also emit DS D-pad inputs.

The Thor's known right-stick defaults are:

```text
MotionEvent.AXIS_Z
MotionEvent.AXIS_RZ
```

Verify the live controller ranges before relying on them. Keep a fail-closed per-device fallback resolver; do not silently choose trigger axes.

### B. Slot-2 protocol v1

Extend the existing fake `CartAnalog` card with a new read bank at:

```text
0x09000200
```

Register contract:

```text
+0x00 s16 yawInputQ12          [-4096, +4096]
+0x02 s16 pitchInputQ12        reserved; write/read 0 in v1
+0x04 u16 yawUnitsPerTick
+0x06 u16 recenterSequence
+0x08 u16 magic                0x5343
+0x0A u16 protocolVersion      1
+0x0C u16 flags
       bit 0: smooth camera enabled
       bit 1: play recenter sound
       bit 2: pitch capability, always 0 in v1
+0x0E u16 reserved             0
```

Mode 0 and mode 1 behavior used by AM64DS movement must remain byte-for-byte behavior-compatible.

Camera protocol state is transient. Do not silently change the existing savestate format. Neutralize camera state around load-state, ROM reset, profile change, controller disconnect and session teardown.

### C. Game patch

Use the pinned European decomp as the semantic source:

```text
tangosdev/sm64ds-decomp
commit 2307f06d9ce10e114fa00d2e9318d5161aaed311
```

The relevant normal orbit routine is:

```text
func_0200bb28
address 0x0200BB28 in the EU decomp
```

Verified semantic fields used by the recovered function:

```text
camera + 0x154  flags
camera + 0x180  current yaw
camera + 0x182  base yaw
camera + 0x184  yaw offset / target offset
camera + 0x196  digital repeat timer
camera + 0x19E  recenter target yaw
camera + 0x1A0  recenter-active state
camera + 0x110  player pointer
player + 0x8E   player yaw used by original recenter
```

Existing digital inputs:

```text
0x0200  camera left
0x0100  camera right
0x4000  camera recenter
```

The recovered digital path adds/subtracts `0x2000`, i.e. 45 degrees, and repeats after `0x14` updates. The smooth path must not emit those bits during ordinary yaw.

Preferred patch strategy:

1. disassemble the exact EU function from the local ROM;
2. map every instruction to the decomp;
3. identify the complete legacy digital-yaw block and its live registers;
4. prefer an in-place guarded replacement when the block has sufficient space;
5. otherwise use a guarded trampoline into a proven safe injection region;
6. preserve the original path whenever protocol magic/version/flags are invalid or the right stick is neutral;
7. preserve original D-pad fallback;
8. do not set the legacy `0x20/0x40` digital lockout flags for continuous yaw;
9. update `camera+0x184` by:

```text
delta = (yawInputQ12 * yawUnitsPerTick) >> 12
```

10. let the original `ApproachLinear` and camera-position/collision code complete the movement;
11. fail closed during scripts, camera tags, dialogue, cannon, first-person and other non-normal camera states;
12. do not overwrite the final camera position from the emulator.

At the measured normal update rate, configure:

```text
165 deg/s at 30 updates/s → yawUnitsPerTick = 1001 (0x03E9)
165 deg/s at 60 updates/s → yawUnitsPerTick = 501  (0x01F5)
```

Do not assume the current camera update frequency. Instrument and measure calls/updates first. Do not derive this from display FPS.

### D. R3 recenter

Use a monotonically increasing 16-bit `recenterSequence`.

A single R3 down edge with `repeatCount == 0` increments it once. Key repeat and key-up must not increment it.

The game patch compares the register with its last consumed sequence and executes one recenter. Reproduce the original semantics:

```text
target = playerYaw + 0x8000
activate the original recenter approach
play sound 0x1A only when the protocol sound flag is enabled
```

Do not create recenter by continuously holding both digital camera directions.

### E. HUD and sound cleanup

Audit both visual paths:

```text
HUD::RenderCameraButtons
Stage::RenderBouncingArrows
```

The existing AM64DS EU guard at `0x020FC0C0` is intended to skip touch camera buttons, but the user still observes arrows. Determine whether the visible elements are:

- permanent camera buttons;
- the camera tutorial/bouncing arrows;
- or both.

Instrument the overlay load and verify the effective instruction, do not add an unverified duplicate patch.

Enhanced mode must suppress both camera-specific visual paths while preserving their original behavior in Original mode.

Ordinary smooth yaw must not call or indirectly synthesize the recenter sound path. Never globally mute `Sound::Play2D` or sound ID `0x1A`.

## Core fork and publication

The superproject currently references:

```text
SapphireRhodonite/melonDS-android-lib
branch GBARumble_PR
```

A public superproject cannot point to an unpublished core commit.

The user authorizes creation or use of this public fork solely for ThorDS core changes:

```text
https://github.com/joeblack2k/melonDS-android-lib
```

Procedure:

1. verify `gh auth status` is `joeblack2k`;
2. inspect whether the fork already exists;
3. never delete or overwrite an existing repository;
4. branch from the exact currently pinned submodule commit;
5. preserve the SapphireRhodonite repository as `upstream`;
6. commit and push the bounded core protocol change;
7. update `.gitmodules` and the superproject submodule pointer deliberately;
8. verify a clean clone can fetch the exact core commit;
9. do not force-push either repository.

## Implementation workstreams

Execute in this order.

### C0 — Baseline and semantic proof

- record current HEAD, submodule SHA and clean/dirty state;
- capture current digital camera telemetry;
- prove current 45-degree stepping;
- distinguish permanent arrows from bouncing arrows;
- trace accidental or intentional `0x4000` recenter;
- measure normal camera update frequency;
- write a hook/liveness document;
- commit only documentation/instrumentation when green.

### C1 — Frontend smooth input

- add pure mapping/configuration classes;
- reserve right-stick axes in Enhanced mode;
- remove ordinary digital D-pad synthesis;
- implement R3 edge sequence;
- neutralize state across lifecycle events;
- add JVM and instrumented tests.

### C2 — Core Slot-2 protocol

- create/use the core fork;
- add mode-2 registers without altering mode 0/1;
- add synchronized setters;
- keep camera fields transient;
- add native tests;
- push the core commit;
- update the superproject pointer.

### C3 — JNI and session integration

- add Kotlin external API;
- add JNI and native-interface methods;
- bind requested/effective profile state;
- send neutral state before/after state loads and on teardown;
- add debug telemetry and a deterministic camera sweep command.

### C4 — Exact EU runtime patch

- create checked-in ARM assembly source;
- create a reproducible assembler/generator;
- verify exact original words from the local EU ROM without publishing bytes;
- generate guarded Action Replay words;
- add code SHA-256 and provenance;
- prove mismatch fail-closed;
- integrate only with `sm64ds.eu.thor-enhanced`.

### C5 — HUD, tutorial arrows and audio

- identify every visible arrow path;
- suppress only the camera-specific UI in Enhanced;
- verify Original is unchanged;
- prove normal yaw never sets both left/right bits and never triggers recenter sound;
- preserve optional R3 sound.

### C6 — Product settings and migration

- preserve or safely migrate the existing `right-stick-camera` enhancement ID;
- bump profile/catalog version only when the existing schema requires it;
- expose requested and effective state;
- default Smooth Orbit on in Enhanced;
- expose sensitivity, deadzone, invert-X and recenter-sound;
- retain a safe off/original recovery path;
- Hardcore still requires Original and a relaunch.

### C7 — Integration and physical Thor acceptance

Test at least:

```text
Castle grounds
Castle interior
Bob-omb Battlefield
Chain Chomp area
mountain path
swimming
flying
sliding
dialogue
door/key transition
cannon/first-person
pause/resume
sleep/wake
controller disconnect/reconnect
save/load state
Original profile
Enhanced + True Widescreen
Enhanced + RA Casual
```

Run a minimum 60-minute final soak only after all functional gates pass.

## Required automated evidence

Create a debug camera trace with, per update:

```text
frame/update index
raw right-stick X/Y
processed X/Y
protocol magic/version/flags
yawUnitsPerTick
recenterSequence
camera state identifier
camera current yaw
camera base yaw
camera target/yaw offset
applied delta
legacy 0x100/0x200/0x4000 bits
HUD camera-buttons draw count
bouncing-arrows draw count
camera-related sound trigger count
```

Evidence may be written publicly only as redacted text/JSON. Do not publish screenshots, ROM paths, ROM bytes or device serials.

A deterministic sweep must prove:

- 0.00 input → no yaw drift;
- 0.25, 0.50, 0.75, 1.00 input → strictly increasing angular speed;
- left/right symmetry within 5%;
- no individual ordinary-yaw step near `0x2000`;
- 165°/s full-deflection target within ±10%;
- same degrees/second when `yawUnitsPerTick` is simulated for 30 and 60 update modes;
- exactly one recenter per R3 down edge;
- no recenter from ordinary right-stick movement.

## Build and device gate

At each bounded slice run the smallest relevant tests, then before each commit run:

```bash
git diff --check
CARGO=/tmp/thords-cargo ./gradlew --no-daemon   :app:testGitHubProdReleaseUnitTest   :app:assembleGitHubProdDebug
```

Use the repository's actual working build command if it has evolved.

Install the exact produced APK:

```bash
adb install -r app/build/outputs/apk/gitHubProd/debug/app-gitHub-prod-debug.apk
```

Then verify:

- exact package launches;
- ASMP boots;
- both Thor displays remain correct;
- no crash or ANR;
- temporary ROM copies and private captures are removed;
- source ROM hash is unchanged;
- Original and Enhanced relaunch cleanly.

## Commit and push policy

Use bounded commits. Suggested sequence:

```text
docs: add smooth camera implementation dossier
test: add deterministic SM64DS camera telemetry
input: replace digital right-stick camera with smooth state
core: expose Slot-2 smooth camera protocol
input: bridge smooth camera state into melonDS core
patch: add guarded SM64DS EU smooth orbit camera
ui: integrate smooth camera settings and camera HUD cleanup
test: validate smooth camera on AYN Thor
```

For every commit:

- review the full staged diff;
- run ROM/save/private-evidence/secret scans;
- do not include local device identifiers;
- push without force;
- verify the remote SHA;
- update `docs/project/STATUS.md` and `docs/project/WORKLOG.md`.

## Publication safety

Never commit or publish:

- `.nds`, `.srl`, `.rom`, `.sav`, `.dsv`;
- ROM bytes, disassembly byte dumps or patched ROMs;
- credentials or tokens;
- private screenshots/captures;
- device serials;
- temporary ADB files;
- unreviewed APKs containing game data.

The generated runtime patch may contain only original ThorDS-authored injected instructions, guarded addresses/expected words and provenance. Do not include copied Nintendo code beyond the minimum numeric original-word guards needed for safe application.

## Stop policy

Do not stop for:

- a long build;
- a failed first patch attempt;
- a need to add instrumentation;
- a subjective uncertainty that can be measured;
- a missing convenience script;
- an old document that no longer matches current code.

Use the two-attempt rule: after two partial attempts with the same method, improve the instrumentation or implementation strategy instead of repeating it.

Stop only for a real unresolved blocker involving:

- ROM safety;
- data loss;
- authentication/publication ownership;
- inability to build the exact core;
- a physically disconnected or unusable Thor after retry;
- an exact binary mismatch that makes safe patching impossible.

Before stopping, complete every non-blocked workstream and document exact evidence and the smallest next action.

## Final completion report

The goal is complete only when all camera gates are green.

Report:

- ThorDS superproject commit SHA;
- core fork commit SHA;
- generated camera runtime-code SHA-256;
- exact source pins;
- build/APK SHA-256;
- automated sweep results;
- physical Thor acceptance matrix;
- Original regression result;
- RA Off/Casual/Hardcore-policy result;
- True Widescreen regression result;
- remaining non-camera project gates, clearly separated.

Do not claim vertical free-look or 60fps. The completed feature name is:

```text
SM64DS Smooth Orbit Camera v1
```
