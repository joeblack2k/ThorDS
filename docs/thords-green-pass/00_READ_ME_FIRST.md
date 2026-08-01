# ThorDS Green Pass — read this first

This pack is an **additive continuation dossier** for the existing public ThorDS repository. It does not replace the original project documents, rewrite the product, or restart M0.

## Install

Extract the ZIP at the root of the existing ThorDS checkout. The result must be:

```text
ThorDS/
└── docs/
    └── thords-green-pass/
        ├── 00_READ_ME_FIRST.md
        ├── 01_GOAL_PROMPT_TERRA_XHIGH.md
        └── ...
```

Do not copy these files over the original root `00_READ_ME_FIRST.md` or `01_GOAL_PROMPT.md`.

## Run model

Use **Terra Xhigh** as the primary implementation model. Open the existing ThorDS repository as the work folder and paste the complete contents of:

```text
docs/thords-green-pass/01_GOAL_PROMPT_TERRA_XHIGH.md
```

The AYN Thor must remain connected over ADB. The local European Super Mario 64 DS ROM must remain ignored and outside Git.

## Baseline represented by this pack

```text
origin:       https://github.com/joeblack2k/ThorDS
minimum HEAD: a2aa88e58031b15a5a77abe33feb2d7fe70a3721
base:         MelonDualDS 0.7.0.rc5
ROM identity: ASMP / revision 0 / RA hash ba3c4052e00c5cc31df5d5534c39de1b
```

A newer descendant of the minimum HEAD is valid. Terra must never reset a newer worktree back to this commit.

## Product target

The pass closes the current partial implementation and delivers:

- physically validated analog controls;
- layer-aware True Widescreen with undistorted 2D/HUD and a 4:3 lower screen;
- a real, guarded ARM9 overclock runtime rather than policy-only plumbing;
- a game-specific Thor Enhanced configuration flow for the exact European SM64DS ROM;
- RetroAchievements Off/Casual user control and a fail-closed Hardcore gate;
- a real 60fps SM64DS mode with 60 unique game updates, normal wall clock and normal audio;
- final stability, safety and release evidence.

No item counts as complete merely because a counter, menu label or configuration value exists.
