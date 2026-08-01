# Recovered SM64DS camera flow

## Input construction

`Stage::CheckCameraInput()` converts touchscreen regions to camera bits.

In standard mode:

```text
0x0200 = left camera
0x0100 = right camera
0x8000 = zoom/camera-mode control
```

`Stage::CheckInput()` maps physical DS inputs and synthesizes:

```text
left + right = 0x4000 recenter
```

## Normal orbit

`func_0200bb28`:

1. marks camera controls available;
2. checks recenter input;
3. updates base yaw from camera tags/scripts;
4. processes digital camera bits;
5. adds/subtracts `0x2000`;
6. sets a repeat timer of `0x14`;
7. approaches current yaw to base + offset with `0x800`;
8. calculates camera orbit position;
9. applies height and collision behavior.

## Sound

`func_02012790(id)` calls:

```text
Sound::Play2D(2, id)
```

The recovered recenter branch uses sound ID `0x1A`.

Ordinary left/right stepping in the recovered orbit block does not directly call sound. A normal-yaw sound therefore indicates another camera mode or accidental recenter and must be traced, not muted.
