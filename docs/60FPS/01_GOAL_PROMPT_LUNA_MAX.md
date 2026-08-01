# /goal — Implement and validate SM64DS 60 FPS on Luna Max

Work in the existing ThorDS repository and continue from the live worktree.

## Mandatory first response

State this explicitly in your first response:

> Use at most 3 parallel subagents where useful. Keep them bounded and
> preferably read-only for (1) decomp/game-patch analysis, (2) core/JIT/ARM9
> work, and (3) independent test/audit review. Work as the Luna Max lead agent
> yourself, wait for their results, inspect their evidence and independently
> verify every conclusion before code or publication.

Then begin immediately. Do not stop after writing another plan.

## Primary goal

Deliver a validated product feature named:

```text
Super Mario 64 DS — 60 FPS
```

for only this exact ROM identity:

```text
ASMP / revision 0 / RA hash ba3c4052e00c5cc31df5d5534c39de1b
```

The completed mode must:

- execute approximately 59.826 semantic gameplay updates per real second in
  ordinary 30 FPS gameplay scenes;
- generate a new rendered game state for those updates;
- preserve ten-minute wall-clock timing within the documented tolerance;
- preserve player movement, gravity, jump arcs, enemies, platforms,
  animations, particles, cutscenes, timers and audio tempo;
- work with Analog, Smooth Orbit Camera, True Widescreen, a validated ARM9
  overclock ratio and RA Casual;
- remain unavailable in Hardcore;
- be user-toggleable with requested/effective/reason state;
- survive relaunch, save/load, sleep/wake and controller reconnect;
- pass the entire stress and 60-minute stability matrix.

A display FPS number, `NDS::RunFrame()` count, VBlank count, duplicated frame,
fast-forward or interpolated frame does not satisfy this goal.

## Preserve the live worktree

Reference ancestors:

```text
ThorDS: 6eaf0df8cc435e3328aae248f8f5d5a5602f218b
Core:   3c54a9c8b5e6b0a928487597ee33dcf110d01c4e
```

Before editing:

```bash
pwd
git status --short --branch
git remote -v
git rev-parse HEAD
git log --oneline --decorate -15
git fetch origin
git rev-parse origin/main
git merge-base --is-ancestor 6eaf0df8cc435e3328aae248f8f5d5a5602f218b HEAD || true
git submodule status --recursive
git -C melonDS-android-lib status --short --branch
git -C melonDS-android-lib remote -v
adb devices -l
```

Rules:

- never reset, force-checkout, rebase away, discard or overwrite newer work;
- preserve the Smooth Orbit work and every unrelated local change;
- do not redo M0-M9 or restart True Widescreen/RA validation;
- import this dossier as a separate safe commit only after scans;
- treat current code and current device behavior as authoritative when an older
  document differs;
- never force-push.

## Existing evidence that must not be misread

The repository already samples:

```text
data_020A0DB0  main-loop counter
data_0208EE44  game cadence/delta value
data_0209F304  candidate Stage timer
```

Castle Garden showed roughly 60/61 loop samples in both Original and Enhanced.
Therefore:

- this path proves liveness;
- it does not prove 60 semantic gameplay updates;
- the Stage timer being zero in that checkpoint is not useful;
- do not mark 60 FPS green from those fields alone.

Previous experimental hooks at `MonitorARM9Jump()` and ARM64 JIT branch helpers
reported `stageBehaviorCalls=0`. Do not repeat those failed approaches.

## Correct semantic instrumentation

Implement two independent routes.

### Route A — actual ARM9 execution boundary

Add a profile/debug-gated monitor at the real ARM9 execution boundary:

```text
ARMv5::Execute<CPUExecuteMode::JIT>()
  instrAddr
  LookUpBlock()
  ARM_Dispatch()
```

The observation must happen on actual block dispatch, not on branch-generation
helpers.

Add the corresponding interpreter observation before actual instruction
execution.

Initially count these normalized addresses independently:

```text
0x02019404  main-loop active-object slot-1 callback
0x02019144  cadence-gated render/OAM callback
0x02019100  unconditional lag/VBlank callback
0x0202BBBC  Stage::Behavior candidate
0x0202B8A4  Stage::Render candidate
0x0211A2B8  dScEntry_c::Behavior candidate
0x0211A26C  dScEntry_c::Render candidate
0x0211A410  dScEntry_c::InitResources
0x0201A534  IRQ::VBlankHandler
```

Verify every address against the exact current decomp and local EU binary before
using it. Record counters even when zero. If a target is compiled inside a
larger block instead of at block entry, add compile-time block-membership
metadata and then a narrowly injected target callback; do not return to the
failed branch-monitor approach.

### Route B — game-side counter fallback

If Route A cannot produce a reliable semantic Behavior/Render count after two
instrumented attempts, build a guarded game-side debug patch that increments a
private counter at the proven semantic entry.

Requirements:

- checked-in ARM assembly source;
- exact original-word guards;
- proven safe injection area;
- deterministic build/generator;
- private counter address proven unused for the active scene set;
- Original mode untouched;
- no guessed code cave;
- no distributed patched ROM.

Both routes may coexist for cross-checking.

## Timing model

The DS display cadence is approximately 59.826 Hz. Most SM64DS gameplay
normally completes an important game update every second DS frame, while title
and star-select paths can already run at the full cadence.

The EU decomp shows:

```text
IRQ::VBlankHandler @ 0x0201A534
data_0209D514      VBlank accumulator
data_0208EE44      threshold and delta value
func_02019144      cadence-gated callback
func_02019100      unconditional callback
```

The first developer implementation may use this exact-profile, guarded probe:

```text
5208EE44 00000002
0208EE44 00000001
D0000000 00000000
D2000000 00000000
```

This is **developer-probe code only**. Its Action Replay/VBlank ordering and
scene-reset behavior must be measured. It is not automatically the product
patch.

The product patch must:

1. make ordinary gameplay wake/update every VBlank;
2. keep title/menu scenes from being doubled;
3. prevent scene initializers from silently restoring 30 FPS;
4. retain `data_0208EE44` as the correct elapsed-tick input where consumers
   already use it;
5. identify and fix every relevant fixed-step consumer that would otherwise
   double or halve;
6. use source-level/generated patch output, not opaque hand-edited words.

## Cadence consumer audit

Run the supplied scanner against the exact pinned decomp and generate:

```text
docs/evidence/m13/cadence-consumers.json
docs/evidence/m13/cadence-consumers.md
```

Classify every `data_0208EE44` use as:

```text
initializer/write
delta-aware timer
delta-aware animation
delta-aware physics
fixed-step timer
fixed-step animation
fixed-step physics
message/HUD
particle/effect
scene-specific
unknown
```

For each unknown or fixed-step consumer, either:

- prove it is irrelevant to ordinary gameplay;
- patch it;
- or keep the product gate red.

Use the latest full-source audit checkout at `755f0be5b9658e5f75871c4138ddc0133a2c07c4` as a
read-only comparison sidecar. Do not silently replace the repository's
existing decomp pin. Compare the relevant function bodies and record whether
the newer source changes semantics or only readability/coverage.

## Community-patch provenance correction

The public community path is:

```text
gamemasterplc YouTube video:
https://www.youtube.com/watch?v=yJXEAIOFcNU

Known target:
USA / NTSC-U revision 1.1
```

The existing `sm64games.com` page is an N64 Super Mario 64 patch page and must
not remain cited as SM64DS binary provenance.

The community patch is an optional behavioral oracle only. Public reports
include:

- slowdown around Chain Chomp;
- slowdown on the Bob-omb Battlefield mountain;
- King Bob-omb and explosion/Yoshi-mouth timing problems;
- enemies running too fast;
- Bob-omb Battlefield running at half speed;
- Tiny-Huge Island becoming unbeatable;
- overclock not fixing all logic issues.

Do not ship or blindly translate it.

If a legal local USA Rev1 original plus patched copy is available, use the
supplied diff tools to produce hashes, section ranges and decomp mappings.
Never commit ROM bytes, the xdelta file without permission, or the patched
ROM. Absence of this optional oracle is not a blocker: derive the EU patch from
the decomp and measured behavior.

## Product implementation sequence

### F0 — dossier and current-state baseline

- safely commit this dossier;
- record live superproject/core/decomp references;
- build/install current APK;
- capture Original and current Enhanced timing baselines in at least:
  - title;
  - star select;
  - castle grounds;
  - Bob-omb Battlefield;
- preserve private states locally.

### F1 — semantic telemetry

- implement Route A;
- prove interpreter and ARM64 JIT coverage;
- add exact-profile gating;
- expose one-second native JSON windows;
- include main-loop, behavior, render, VBlank, lag callback, cadence and ARM9
  telemetry;
- add deterministic host tests;
- validate on Thor;
- use Route B only if required by the two-attempt rule.

### F2 — full cadence inventory

- run the prepared decomp scanner;
- classify all writes/reads;
- map scene initializers and overlay residency;
- determine why Castle Garden reports cadence 1;
- choose a known 30 FPS baseline checkpoint;
- document the exact patch model.

### F3 — developer cadence mode

- add a hidden exact-profile `60fps-dev-cadence` enhancement;
- use guarded runtime code;
- expose requested/effective/reason;
- require a full relaunch;
- automatically collect semantic/timing telemetry;
- default off and never call it validated.

### F4 — timing-correct source patch

- create checked-in ARM assembly/source fragments;
- patch every required initializer and fixed-step consumer;
- build deterministic AR output;
- verify original words;
- calculate canonical SHA-256;
- add mismatch/fail-closed tests;
- keep Original/Safe/Hardcore clean.

### F5 — ARM9 headroom

Use the existing public core and M10 foundation.

- revalidate 100% equivalence;
- validate 125%;
- test 150%, 175% and 200% only in ascending order;
- select the lowest ratio that sustains the full mode;
- keep DS wall clock, ARM7, GPU, audio, timers and RTC normal;
- do not use fast-forward;
- maintain savestate ratio compatibility;
- Hardcore forces 100%.

A 60 FPS mode may request a ratio, but UI must show requested and effective
values separately.

### F6 — product profile and UI

Add the profile enhancement:

```text
id: 60fps
display: 60 FPS
default: on only after VALIDATED
requires: exact EU + Vulkan + validated ARM9 ratio
conflicts: RA Hardcore
relaunch: required
```

Until the entire matrix passes, display:

```text
60 FPS — Experimental
```

After validation:

```text
60 FPS — On
```

The user controls RA Off/Casual. Hardcore remains unavailable with any
enhancement.

### F7 — deterministic timing parity

Use identical private state/input replays for Original and 60 FPS.

Prove:

- semantic updates: approximately 29.913 vs 59.826 in known gameplay scenes;
- render callbacks: approximately 29.913 vs 59.826;
- wall-clock drift <= 0.1% over ten minutes;
- player distance over fixed real time within tolerance;
- jump apex and landing time within tolerance;
- gravity/fall time within tolerance;
- enemy/platform periods within tolerance;
- animation and particle duration within tolerance;
- countdowns, messages and cutscenes within tolerance;
- audio pitch/tempo and underruns acceptable;
- RA Casual still identifies and submits normally.

### F8 — stress and integration

At minimum:

```text
Bob-omb Battlefield spawn
Chain Chomp
mountain path
King Bob-omb
Bob-omb explosions
Yoshi holding/swallowing Bob-ombs
Tiny-Huge Island
water/swimming
flying
sliding
caps
moving platforms
doors/key transitions
star-select
cutscenes
minigames
sleep/wake
save/load
controller reconnect
```

Combined mode:

```text
Analog
Smooth Orbit Camera
True Widescreen
60 FPS
effective ARM9 ratio
RA Casual
```

### F9 — final product gate

- 60-minute soak;
- no crash/ANR;
- no persistent slow motion;
- no duplicated/half-speed systems;
- clean relaunch;
- private artifacts removed;
- public safety scans;
- bounded commits pushed;
- status/ADR/worklog/final report updated.

## Prepared code

Read and use the files under:

```text
docs/60fps/code/
```

They contain:

- a complete semantic counter class;
- the correct JIT/interpreter insertion points;
- Kotlin requested/effective state models;
- profile JSON;
- ARM patch skeleton;
- runnable decomp scanner;
- runnable NDS section/diff tools;
- deterministic ARM build and AR generators;
- AR verifier;
- timing/telemetry comparators;
- ADB runner.

Adapt them to the live code; do not rewrite them from scratch unless the current
source requires a justified change.

## Test and build gate

At every bounded slice, run the smallest tests first. Before every
superproject commit:

```bash
git diff --check
CARGO=/tmp/thords-cargo ./gradlew --no-daemon   :app:testGitHubProdReleaseUnitTest   :app:assembleGitHubProdDebug
```

Use the live repository's equivalent commands if they have changed.

For core changes:

```bash
git -C melonDS-android-lib diff --check
```

Push the core commit first, then update the superproject gitlink.

Install and test the exact APK:

```bash
adb install -r app/build/outputs/apk/gitHubProd/debug/app-gitHub-prod-debug.apk
```

## Commit plan

Suggested bounded commits:

```text
docs: add SM64DS 60fps implementation dossier
test: add semantic SM64DS execution telemetry
research: classify SM64DS EU cadence consumers
patch: add guarded SM64DS EU 60fps developer mode
patch: correct SM64DS 60fps timing consumers
core: validate ARM9 headroom for SM64DS 60fps
ui: add effective SM64DS 60fps profile control
test: validate SM64DS 60fps timing on AYN Thor
release: complete SM64DS 60fps product gate
```

Do not combine a speculative patch and a green status claim in one commit.

## Safety and publication

Never publish:

- `.nds`, `.srl`, `.rom`, `.sav`, `.dsv`;
- xdelta output or patched ROM without explicit legal/provenance approval;
- ROM bytes or large instruction dumps;
- save states or saves;
- screenshots/private captures;
- device serials;
- credentials/tokens;
- unreviewed APKs containing game data.

Minimal expected-word guards and ThorDS-authored generated instructions are
allowed.

## Stop policy

Do not stop because:

- the first counter is zero;
- an initial cadence-only probe is imperfect;
- a long build is needed;
- instrumentation must be improved;
- an optional USA community reference is unavailable;
- one stress scene fails.

After two partial attempts using the same method, change the instrumentation or
patch strategy.

Stop only for a real unresolved blocker involving ROM safety, data loss,
authentication/publication ownership, inability to build the exact source or
an unusable physical Thor after retry. Before stopping, finish every
non-blocked workstream and document the exact smallest next action.

## Completion report

Report all of:

```text
ThorDS SHA
core SHA
decomp pin/audit SHA
60fps runtime-code SHA-256
APK SHA-256
effective ARM9 ratio
semantic update rates
render rates
wall-clock drift
physics/animation/audio parity
stress matrix
combined-feature matrix
RA policy/result
60-minute soak result
remaining non-60fps project gates
```

Do not call the feature complete unless every requirement in
`05_DEFINITION_OF_DONE.md` is green.
