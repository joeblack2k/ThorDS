/goal
@ponytail

Continue the existing ThorDS Enhanced repository through a focused productization and closure pass. This is not a new project, not a rewrite, and not another planning-only run.

## Mandatory reading order

Read these files before editing source:

1. `docs/thords-green-pass/00_READ_ME_FIRST.md`
2. `docs/thords-green-pass/02_MODEL_DECISION.md`
3. `docs/thords-green-pass/03_CURRENT_BASELINE.md`
4. `docs/thords-green-pass/04_FINISH_DEFINITION.md`
5. `docs/thords-green-pass/05_NON_NEGOTIABLES.md`
6. `docs/thords-green-pass/06_EXECUTION_ORDER.md`
7. `docs/thords-green-pass/07_ACCEPTANCE_MATRIX.md`
8. `docs/thords-green-pass/08_TEST_EVIDENCE_POLICY.md`
9. `docs/thords-green-pass/09_GIT_PUBLICATION_POLICY.md`
10. `docs/thords-green-pass/10_BLOCKER_POLICY.md`
11. `docs/thords-green-pass/11_FILE_AND_COMPONENT_MAP.md`
12. `docs/thords-green-pass/12_RISK_REGISTER.md`
13. `docs/thords-green-pass/13_NO_REDO_POLICY.md`
14. every file under `docs/thords-green-pass/contracts/`
15. the active workstream file before implementing that workstream
16. the existing repository files `docs/project/STATUS.md`, `docs/project/WORKLOG.md`, `04_DEFINITION_OF_DONE.md`, and the original architecture/testing documents linked from the workstream

Treat the checked-out source, current Git history and current project status as the source of truth when they are newer than this dossier.

## Repository and source lock

Expected public repository:

```text
origin   https://github.com/joeblack2k/ThorDS
upstream https://github.com/SapphireRhodonite/melonDS-android.git
```

Minimum known public baseline:

```text
a2aa88e58031b15a5a77abe33feb2d7fe70a3721
```

At start:

```bash
git status --short --branch
git rev-parse HEAD
git merge-base --is-ancestor a2aa88e58031b15a5a77abe33feb2d7fe70a3721 HEAD
git remote -v
git submodule status --recursive
adb devices -l
```

Rules:

- Never reset, rebase away, discard, stash-and-forget, or overwrite existing work.
- A newer descendant of the minimum baseline must be preserved.
- Do not rebuild M0-M5 or recreate systems that already exist.
- Keep the local European ROM ignored and unchanged.
- Do not force-push.
- Do not publish ROMs, saves, save states, credentials, tokens, device identifiers or private captures.
- Public source commits may be pushed after their bounded gate is green and the publication scan passes.

If actual ARM9 core changes are required, follow `contracts/CORE_FORK_POLICY.md`. Never leave a local-only submodule commit referenced by the superproject.

## Primary objective

Move every currently open product feature to an evidence-backed green state:

```text
Analog                PASS
True Widescreen       PASS_PRODUCT
RetroAchievements     PASS_OFF_CASUAL_HARDCORE_GATE
ARM9 overclock        PASS_EXPERIMENTAL_AT_LEAST_125
Thor Enhanced GUI     PASS
60fps                 PASS_VALIDATED
Stability/release     PASS
```

A feature is not green when it is only a developer probe, policy object, read-only label, renderer FPS counter, hidden placeholder, untested patch, or host-polled screenshot sequence.

## Execution order

Execute the workstreams in this order:

```text
G0  reconcile current state and import this dossier
G1  close M6 analog end-to-end acceptance
G2  close the remaining M7 proof gates with deterministic instrumentation
G3  implement and validate M8 product True Widescreen
G4  implement the real M10 ARM9 runtime, JNI, telemetry and ratio validation
G5  deliver the M11 exact-ROM Thor Enhanced configuration experience
G6  complete M9 RetroAchievements UI/runtime/network acceptance
G7  implement and validate M13 real 60fps
G8  run M12 full stability, safety and release acceptance
```

Do not skip a dependency, but do finish independent non-blocked work when a physical gate is temporarily unavailable.

## Critical behavior change from the previous run

Do not continue an evidence treadmill.

After two failed or partial attempts with the same measurement method, the third attempt must change source instrumentation, capture method, deterministic input, checkpoint, or test design. Do not collect another nearly identical screenshot burst and call that progress.

For the remaining M7 gates, add the smallest debug-only instrumentation necessary to produce deterministic machine-readable proof. Castle Garden remains the M7 representative gameplay scene; Bob-omb Battlefield is not reintroduced as an M7 gate. M8 product acceptance and M13 stress testing may use broader scenes.

## Product decisions already made

- Exact target game is the European retail SM64DS identity: `ASMP`, revision `0`, RA hash `ba3c4052e00c5cc31df5d5534c39de1b`.
- Analog uses the existing verified European AM64DS runtime code and Slot-2 analog implementation.
- True Widescreen means full-width 3D plus aspect-correct 2D/HUD, not full-frame stretch.
- Bottom DS output remains 4:3 and touch-correct.
- RetroAchievements is a user choice: Off or Casual may run with enhancements.
- Requested Hardcore with enhancements must fail closed and offer Original + restart; never silently downgrade.
- ARM9 overclock is extra ARM9 work inside normal DS wall time, not fast-forward.
- An overclock feature is green when at least one ratio above 100%, normally 125%, is genuinely effective and passes its gate. Expose only ratios that pass individually; do not force 175% or 200% to appear.
- The renderer reporting 60 FPS is not proof of 60fps gameplay. M13 must prove 60 unique game updates per second, normal timers, normal physics and normal audio.
- The 60fps toggle remains off by default, but this goal is not complete until the mode itself is validated.

## Implementation discipline

For each workstream:

1. inspect current source and evidence;
2. write or update the smallest ADR when a core architectural decision is required;
3. implement the missing product behavior;
4. add deterministic automated tests;
5. build the exact GitHub Prod debug variant;
6. install and test on the connected Thor through ADB;
7. collect redacted text/JSON/CSV evidence;
8. update `docs/project/STATUS.md` and `docs/project/WORKLOG.md` truthfully;
9. run ROM/save/secret/private-path scans;
10. create one or more bounded intentional commits;
11. push each green bounded commit to `origin/main` without force;
12. continue immediately to the next workstream.

Read-only review agents may audit a completed diff, but the primary Terra session owns the implementation and decisions. Do not delegate an entire unresolved workstream to a weaker side model and then wait for it.

## Build and device gate

Reuse the repository's known working toolchain. The final full gate must include at least:

```bash
./gradlew --no-daemon \
  :app:regenerateVulkanSpirv \
  :app:checkVulkanSpirv \
  :app:testGitHubProdReleaseUnitTest \
  :app:assembleGitHubProdDebug

adb install -r app/build/outputs/apk/gitHubProd/debug/app-gitHub-prod-debug.apk
```

Adapt only the local Cargo wrapper/environment when required; do not commit machine-specific absolute paths.

## Completion rules

Do not declare this goal complete until:

- all rows in `07_ACCEPTANCE_MATRIX.md` required for the full target are green;
- M6 through M13 status is updated accurately;
- True Widescreen is a normal exact-profile option, not a developer extra;
- at least one actual ARM9 ratio above 100% is effective on the core and device;
- the exact SM64DS profile has a usable enhancement menu;
- RA Off/Casual and the Hardcore conflict path are physically validated;
- 60 unique gameplay updates per second are proven for the 60fps mode;
- combined Analog + True Widescreen + validated ARM9 + 60fps + RA Casual is tested;
- 60-minute stability, save, relaunch, sleep/resume and transition tests pass;
- the public repository contains no prohibited material;
- the final report clearly distinguishes validated ratios and modes from unavailable ones.

A partial result is preferable to a false claim, but do not stop merely because a problem is difficult. Change method, instrument the source, isolate the blocker and continue every non-blocked workstream.
