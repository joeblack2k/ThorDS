# C6 — recenter and settings

## Recenter sequence

Frontend state:

```kotlin
private var recenterSequence: Int = 0

fun onR3Down(repeatCount: Int) {
    if (repeatCount == 0) {
        recenterSequence = (recenterSequence + 1) and 0xFFFF
        publishCameraState()
    }
}
```

Do not reset the sequence during routine `releaseAllInputs()`.

## Game consumption

Keep the last consumed sequence in patch-owned state. On change:

- set original recenter target;
- activate original recenter movement;
- optionally call original sound wrapper;
- consume exactly once.

## UI

Expose:

- Smooth Orbit requested/effective;
- sensitivity;
- deadzone;
- response;
- invert X;
- recenter sound;
- D-pad fallback.

All settings require a relaunch if the current architecture cannot update them atomically.

## Defaults

Enhanced defaults on. Original defaults off.
