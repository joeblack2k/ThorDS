# ThorDS SM64DS 60 FPS — Luna Max implementation dossier

This folder is a **focused implementation package** for the required
Super Mario 64 DS 60 FPS milestone in ThorDS Enhanced.

It is intended to be extracted directly into:

```text
docs/60fps/
```

## Product decision

The target is not a renderer counter, frame interpolation, fast-forward or a
blind port of the unstable USA community binary.

The target is:

> **An exact-European, game-side 60 FPS mode that produces approximately sixty
> semantic gameplay updates and sixty render opportunities per real second,
> while preserving wall-clock timing, physics, animation, audio and save/RA
> behavior.**

The exact game identity remains:

```text
System: Nintendo DS
Game code: ASMP
Revision: 0
RetroAchievements system hash: ba3c4052e00c5cc31df5d5534c39de1b
```

## Current public references

These are reference ancestors only:

```text
ThorDS public reference:
6eaf0df8cc435e3328aae248f8f5d5a5602f218b

Current public core gitlink at that reference:
3c54a9c8b5e6b0a928487597ee33dcf110d01c4e

Current SM64DS decomp audit reference:
755f0be5b9658e5f75871c4138ddc0133a2c07c4
```

Never reset a newer worktree to these SHAs.

## What is already available

ThorDS already contains:

- exact European profile matching;
- analog movement;
- a True Widescreen product path;
- RA Off/Casual and Hardcore policy plumbing;
- ARM9-overclock configuration and telemetry foundation;
- a native one-second SM64DS loop/cadence sampler;
- a public core fork;
- a connected AYN Thor ADB workflow.

The existing sampler is useful but **not sufficient**: Original and Enhanced
both produced roughly 60/61 main-loop samples in Castle Garden. That proves
the sampler is alive, not that gameplay is already 60 FPS.

## Install

From the root of the existing ThorDS worktree:

```bash
mkdir -p docs/60fps
unzip /path/to/thords-sm64ds-60fps-luna-max-v1.zip -d docs/60fps
git status --short
```

Start a new **Luna Max** session in that same worktree and paste the complete
contents of:

```text
docs/60fps/01_GOAL_PROMPT_LUNA_MAX.md
```

## Reading order

1. `01_GOAL_PROMPT_LUNA_MAX.md`
2. `02_PRODUCT_DECISION.md`
3. `03_SCOPE_AND_NON_GOALS.md`
4. `04_NON_NEGOTIABLES.md`
5. `05_DEFINITION_OF_DONE.md`
6. `06_EXECUTION_ORDER.md`
7. `07_CURRENT_STATE.md`
8. `08_SOURCE_PINS.md`
9. `09_PROJECT_MAP.md`
10. architecture, research, implementation and code documents
11. testing and operations documents

## Safety

The user's ROM, patched test copies, saves, save states, screenshots, private
captures, account data and device identifiers are local test inputs only.
They must never be committed or published.
