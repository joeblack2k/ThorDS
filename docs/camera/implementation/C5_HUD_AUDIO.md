# C5 — HUD and audio cleanup

## HUD investigation

Trace both:

```text
HUD::RenderCameraButtons
Stage::RenderBouncingArrows
```

Classify the visible UI by draw location and call count.

## Enhanced behavior

When Smooth Orbit is effective:

- no permanent touch camera buttons;
- no camera tutorial/bouncing arrows;
- lower map remains untouched;
- zoom/other unrelated UI remains intact.

## Overlay safety

Verify the overlay-2 expected word after the overlay is loaded. If the current AM64DS guard is not applying, repair the generic overlay patch timing or code composition rather than adding an unexplained duplicate.

## Audio

Instrument the relevant camera input and sound path.

Normal yaw:

```text
legacy left bit = 0
legacy right bit = 0
recenter bit = 0
recenter sound count = 0
```

R3:

```text
recenter sequence increments once
recenter action count = 1
sound count = 1 when enabled
```

Never mute a global sound function or ID.
