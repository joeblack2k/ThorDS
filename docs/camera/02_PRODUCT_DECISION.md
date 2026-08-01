# Product decision

## Approved direction

ThorDS Enhanced will use a real right-stick camera path rather than translating the right stick to DS buttons.

The product name for this bounded feature is:

```text
SM64DS Smooth Orbit Camera v1
```

## User-visible result

| Input | Enhanced behavior |
|---|---|
| Left stick | Existing AM64DS analog movement |
| Right stick X | Continuous horizontal orbit |
| Right stick Y | Reserved; no action in v1 |
| R3 | One-shot recenter |
| D-pad | Original digital fallback |
| Touch camera UI | Hidden in Enhanced |
| Original profile | Fully original behavior |

## Why horizontal orbit first

SM64DS recomputes vertical view angle from the camera and look-at vectors and switches among multiple camera states. A proper pitch implementation would need additional collision, first-person, cannon, swimming, flying and scripted-camera work.

Horizontal orbit can be integrated inside the recovered normal orbit routine while preserving:

- original camera target;
- original camera distance;
- original collision;
- original camera position solver;
- original easing;
- scripted camera state changes.

That is the correct product cut.

## Quality target

The camera must feel like a modern handheld port, not like rapid button emulation.

A successful result has:

- proportional angular speed;
- stable center;
- no snap to 45-degree targets;
- no accidental recenter;
- no camera-button animation;
- no sound for ordinary yaw;
- consistent sensitivity across future 30/60 update modes.
