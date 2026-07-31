# Analog-inputarchitectuur

## Flow

```text
Android MotionEvent
→ device/axis resolver
→ radial deadzone + curve
→ Slot2AnalogState
→ JNI setSlot2AnalogInput
→ melonDS Slot-2 analog accessory
→ AM64DS patched game
```

Camera:

```text
right stick
→ hysteresis digitalizer
→ DS D-pad state
→ AM64DS camera path
```

## Profile overlay

Maak geen fragiele volledige kopie van de globale controllerconfig. Gebruik een overlay:

```text
BaseControllerConfig
+ GameControllerOverlay
+ UserProfileOverrides
= EffectiveControllerConfig
```

De overlay bevat alleen:

- left stick Slot-2 route;
- right stick D-pad camera;
- eventueel face-button semantic preset;
- deadzones/curves.

Library database keys blijven ROMidentity-gebaseerd.

## Radiale verwerking

Parameters:

```text
innerDeadzone = 0.10
outerDeadzone = 0.02
curveExponent = 1.0
invertX/Y = false
```

Algoritme:

1. lees X/Y;
2. clamp;
3. bereken magnitude;
4. inner deadzone;
5. rescale resterend bereik;
6. outer saturation;
7. curve;
8. herstel richting;
9. send only on relevant change plus regular keepalive if accessory needs it.

## Devicekeuze

Thor built-in controller:

- detecteer input source;
- gebruik stable descriptor/vendor/product waar mogelijk;
- deviceId is niet stabiel over reboots;
- user override op descriptor, niet alleen integer ID.

## Camera hysterese

Per as:

```text
press ≥ 0.55
release ≤ 0.35
```

Diagonal policy moet bij SM64DS worden getest. Mogelijk:

- dominante as;
- diagonalen toestaan;
- camera step/repeat.

Geen D-pad key chatter.

## Inputfocus

Secondary `Presentation` is niet-focusbaar. Fysieke controller blijft bij hoofdactivity. Touch op lower presentation blijft via layoutview/system input naar bottom DS gaan.

## Debugging

Developer overlay/log:

```text
raw x/y
processed x/y
magnitude
angle
active device
Slot-2 connected
camera digital states
```

Rate limited; geen permanente logspam.

## Safe fallback

Als analogaccessory ontbreekt of codeprecondition faalt:

- disable enhancement;
- gebruik normale DS D-pad;
- toon reden;
- start game nog steeds.

## Tests

- center drift;
- cardinal full range;
- diagonals;
- slow circle;
- rapid reversal;
- reconnect;
- app background;
- second controller;
- right-stick camera;
- touch simultaneously;
- no impact on non-profile games.
