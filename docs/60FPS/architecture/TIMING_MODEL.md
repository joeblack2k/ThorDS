# SM64DS timing model

## Clock layers

```text
Android display/presenter
        ↓
melonDS NDS::RunFrame
        ↓
DS scanlines / VBlank (~59.826 Hz)
        ↓
ARM9 VBlank handler
        ↓
game-thread wake threshold
        ↓
scene Behavior/Render
```

Do not collapse these into one `FPS` value.

## Recovered VBlank logic

Conceptually:

```c
vblankCounter++;
if (vblankCounter >= cadence && gameThreadCanWake) {
    wake(gameThread);
    vblankCounter = 0;
    cadenceGatedRenderCallback();
}
unconditionalLagCallback();
```

## Ordinary 30 FPS

For most gameplay:

```text
DS VBlank:          ~59.826/s
semantic updates:   ~29.913/s
important values:   update every second DS frame
```

## Target 60 FPS

```text
DS VBlank:          unchanged
semantic updates:   ~59.826/s
wall clock:         unchanged
audio clock:        unchanged
```

## Variable roles

`data_0208EE44` has at least two roles:

1. game-thread wake threshold;
2. elapsed-tick/delta input.

A correct patch must preserve both semantics.
