# SM64DS Thor-controllerprofiel

## Doel

De game laten spelen als een moderne 3D-platformer zonder de originele touchfuncties te verliezen.

## Routes

```text
Left stick   → Slot-2 analog X/Y
Right stick  → DS D-pad camera
D-pad        → DS D-pad camera
Touch        → DS bottom screen
Start/Select → DS Start/Select
Shoulders    → DS L/R
Face buttons → DS A/B/X/Y via geteste Thor semantic layout
```

## Face buttons

Luna bepaalt op fysieke Thor:

- welke labels Android rapporteert;
- welke ergonomische “jump/action/crouch” mapping logisch is;
- of Nintendo- of Xbox-letterpositie de standaard moet zijn.

UI toont semantiek plus DS-letter:

```text
Jump (DS B)
Attack/Run (DS Y)
Crouch (DS R)
...
```

Geen ongeteste aanname in dit dossier hardcoderen.

## Analog defaults

```text
inner deadzone: 0.10
outer deadzone: 0.02
curve: linear
invert: false
```

User settings:

- deadzone;
- curve;
- invert;
- controller descriptor.

## Camera digitalizer

```text
press threshold: 0.55
release threshold: 0.35
```

Test dominant-axis versus diagonal. Kies gedrag dat geen ongewenste dubbele camera-input veroorzaakt.

## Touch

Onderste scherm blijft touchbaar voor:

- menus;
- minigames;
- map;
- character interactions;
- alle originele gameflows.

Analog vervangt niet generiek alle touch.

## Profile overlay safety

- immutable built-in defaults;
- user override delta;
- stable ROM identity;
- no filename serialization;
- parse errors isolated;
- reset profile controls button;
- global config untouched.

## Acceptance scenes

- castle grounds;
- precise walking around doors;
- Bob-omb Battlefield;
- bridge/Chain Chomp;
- mountain turns;
- swimming;
- flying;
- sliding;
- climbing;
- minigame touch;
- menu navigation;
- character switch.

## Metrics

Log for testbuild:

- raw X/Y;
- processed X/Y;
- magnitude;
- action speed/state if accessible only for diagnostics via non-invasive memory read or visual observation;
- camera D-pad states;
- input device descriptor.

Geen memory editor in release/Hardcore.
