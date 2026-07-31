# Analog-acceptatietests

## Automated

### A-01 Dead center

Raw jitter within deadzone → processed zero.

### A-02 Radial shape

Sample circle at fixed raw magnitude. Processed magnitude constant within tolerance.

### A-03 Diagonal normalization

`(1,1)` clamps to magnitude 1, niet 1.414.

### A-04 Range rescale

Net buiten deadzone begint nabij nul; outer edge bereikt 1.

### A-05 Invert/curve

Deterministische unit vectors.

### A-06 Camera hysteresis

Geen repeated press/release tussen 0.35 en 0.55.

### A-07 Device reconnect

Stable descriptor restore.

## Physical gameplay

### A-10 Castle precision

- langzaam rond deur;
- korte correcties;
- 360° cirkel;
- geen 8-way snapping.

### A-11 Bob-omb field

- sprint rechte lijn;
- diagonalen;
- scherpe turns;
- Chain Chomp bridge;
- mountain path.

### A-12 Movement types

- swim;
- fly;
- slide;
- climb;
- crawl/sneak indien aanwezig;
- shell.

### A-13 Camera

- right stick left/right;
- no accidental movement;
- no camera stuck key;
- physical D-pad alternative.

### A-14 Menus/touch

- file select;
- pause;
- map;
- minigame;
- touch remains.

### A-15 Relaunch/library

- close app;
- relaunch;
- library present;
- profile active;
- config not corrupt.

### A-16 Toggle off

Start Original:

- no Slot-2 analog;
- game falls back digital;
- no stale code/input.

## Latency

Gebruik event timestamps waar mogelijk. Geen extra meerframe queue.

## Pass

- continuous analog;
- stable center;
- reliable camera;
- all core movement playable;
- no general-game regression.
