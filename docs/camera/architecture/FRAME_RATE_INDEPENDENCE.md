# Frame-rate independence

## Distinguish three rates

- Android display/presentation FPS;
- melonDS emulation/VBlank rate;
- SM64DS camera/game update rate.

The camera patch runs at the game-camera update rate. Do not calculate sensitivity from `getFPS()`.

## Formula

SM64DS uses a 16-bit full circle:

```text
65536 units = 360 degrees
```

For a target speed:

```text
yawUnitsPerTick =
round((degreesPerSecond / 360) * 65536 / gameUpdatesPerSecond)
```

For 165°/s:

```text
30 updates/s → 1001 = 0x03E9
60 updates/s →  501 = 0x01F5
```

## Required measurement

C0 must count actual executions of the selected camera update path over at least ten real seconds.

## Future 60fps

A future 60fps enhancement changes only `yawUnitsPerTick`. The camera patch and protocol do not need to be rewritten.

Automated tests must simulate both values and compare degrees per second.
