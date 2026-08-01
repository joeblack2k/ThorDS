# G5 — M11 Thor Enhanced GUI

## Goal

Give the exact European SM64DS ROM a useful configuration experience without turning the app into a game-specific dead end.

## Minimum product-complete flow

On the exact ROM details/game card:

```text
Profile: Original / Thor Enhanced
Analog Controls: on/off
True Widescreen: requested/effective
ARM9: only validated/experimental passing ratios
60 FPS: off/on, with validation label
RetroAchievements: Off / Casual / Hardcore
Compatibility and relaunch message
Play
```

## Rules

- UI reads and writes the existing per-ROM profile preferences.
- UI displays the **resolved plan**, not just requested toggles.
- Unsupported options are disabled with a reason.
- Hardcore + Enhanced shows the two explicit recovery choices.
- Changes requiring relaunch are staged and applied atomically on confirmed restart.
- The lower screen remains game output during play; the pause menu may temporarily provide enhancement/status actions.
- Preserve advanced emulator settings and non-Thor UI.
- No second database or independent settings model.

## Physical tests

- first run and exact ROM recognition;
- unknown ROM fallback;
- toggle conflicts;
- Play and relaunch;
- safe mode;
- process recreation;
- both displays/insets;
- pause/resume and Reset/Exit remain reachable.

## Suggested commit

```text
ui: deliver SM64DS Thor Enhanced controls
```
