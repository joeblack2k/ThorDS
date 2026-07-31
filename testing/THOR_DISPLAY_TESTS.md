# Thor-displayacceptatietests

## TD-01 Inventaris

Verwacht twee ingebouwde displays. Leg alle metadata vast.

Pass:

- exact role assignment;
- geen hardcoded ID;
- dimensions sane.

## TD-02 Top output

Toon diagnostische top-grid.

Pass:

- fysiek boven;
- volledig zichtbaar;
- juiste orientation;
- geen touch/controls overlay.

## TD-03 Bottom output

Toon bottom-grid met DS-coordinate labels.

Pass:

- fysiek onder;
- 4:3 fit;
- bars gelijkmatig;
- no stretch.

## TD-04 Touch grid

Tik op negen targets:

```text
(0,0) (128,0) (255,0)
(0,96) ...
(255,191)
```

Pass:

- mapped error ≤ 2 DS pixels;
- bars triggeren geen DS touch;
- release events correct.

## TD-05 Swap/recover

- swap;
- swap back;
- relaunch.

Pass: roles remain deterministic.

## TD-06 Sleep/resume

- 30 s sleep;
- wake;
- app foreground.

Pass:

- both surfaces recover;
- audio resumes;
- no black stale surface;
- save intact.

## TD-07 Presentation dismissal

Force/imitate removal or background.

Pass:

- no crash;
- game pauses or safe single-screen fallback;
- Presentation recreated.

## TD-08 Main activity on unexpected panel

Start app where possible from lower display context.

Pass:

- role classifier corrects assignment or safe prompt;
- controller focus remains.

## TD-09 Refresh modes

Measure actual panel refresh. Pass if emulator/presenter chooses stable cadence without duplicate-frame jank.

## TD-10 Non-Thor fallback

Fake/unit or second Android device:

- no Thor role assumptions;
- normal layout;
- no crash.

## Evidence

Per test:

- command;
- build;
- screenshots;
- log excerpt;
- result;
- actual dimensions.
