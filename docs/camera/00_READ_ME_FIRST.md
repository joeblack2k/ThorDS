# ThorDS Smooth Orbit Camera — read this first

This dossier is a bounded implementation package for **Luna Max**. It does not restart ThorDS and it does not replace the existing True Widescreen, analog-movement, RetroAchievements or ARM9 work.

## Product decision

The next camera implementation is:

> A real, continuous horizontal right-stick orbit camera for the exact European Super Mario 64 DS profile.

It replaces the current digital right-stick-to-D-pad adapter.

The finished Enhanced experience must have:

- continuous right-stick yaw, proportional to stick deflection;
- no 45-degree camera stepping;
- no repeated camera click/recenter sound during ordinary yaw;
- no duplicated touchscreen camera arrows in Enhanced mode;
- R3 as an explicit one-shot recenter;
- original SM64DS camera collision, easing and scripted-camera behavior;
- Original mode unchanged;
- Casual RetroAchievements still user-selectable;
- Hardcore still restricted to Original.

## Known source state

The newest public `ThorDS` commit observed while this dossier was produced was:

```text
a7831c38c55e9eeef2376bb2390a99a108ab2bd0
render: close SM64DS structured widescreen proof gates
```

The latest relevant input commit was:

```text
e95699aae96d0d5a86bdf332a514650a9619b4f9
input: harden and trace Slot-2 analog lifecycle
```

These are **reference ancestors, not reset targets**. Luna must preserve any newer local or remote work.

## Installation

The ZIP contains files relative to `docs/camera/`.

From the ThorDS repository root:

```bash
mkdir -p docs/camera
unzip /path/to/thords-smooth-camera-luna-max-v1.zip -d docs/camera
```

Then start a new **Luna Max** goal in the same repository and paste the entire contents of:

```text
docs/camera/01_GOAL_PROMPT_LUNA_MAX.md
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
10. the architecture documents;
11. the research documents;
12. the implementation documents;
13. the testing and operations documents.

## Important safety boundary

The local Nintendo DS ROM is input data only. Never commit, publish, copy into an APK, quote, dump, attach, or include ROM bytes in evidence.

Public evidence must be text-only and redacted. Private physical captures may be used locally but must remain ignored.
