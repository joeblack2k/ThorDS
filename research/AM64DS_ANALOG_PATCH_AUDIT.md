# AM64DS analogpatchaudit

## Bron

`LRFLEW/AM64DS_DeSmuME`, branch `analog`.

De oplossing bestaat uit twee delen:

1. een gamepatch die SM64DS analoge waarden uit een virtueel Slot-2-device laat lezen;
2. een emulatoraccessory die X, Y, magnitude en angle aanbiedt.

## Slot-2-protocol

De oorspronkelijke emulatorimplementatie exposeert bij het AM64DS-subregistergebied onder meer:

```text
0x09000100 → magnitude
0x09000102 → X
0x09000104 → Y
0x09000106 → angle
```

Waarden gebruiken 12-bit fixed-pointachtige schaal:

```text
1.0 ≈ 0x1000
angle ≈ radians × 32768 / π
```

De input wordt op lengte begrensd zodat diagonalen niet boven magnitude 1 komen.

## Europese code

De bronrepository levert een expliciete Europese runtimecode. Zie het enige autoritatieve exemplaar in:

```text
profiles/SM64DS_ANALOG_AR_CODE.md
```

Gebruik geen Amerikaanse revision 0/1-code.

## Wat rc5 al doet

- `RomGbaSlotConfig.AnalogInput`;
- native Slot-2-accessory in de fork/core;
- X/Y-config;
- inversion;
- devicefilter;
- deadzone;
- JNI-setter;
- controller MotionEvents.

## Wat wij verbeteren

### Radiale deadzone

Huidige per-as cutoff kan diagonalen vervormen. Gebruik:

```text
m = hypot(x, y)

if m <= deadzone:
    x = 0
    y = 0
else:
    scaled = clamp((m - deadzone) / (1 - deadzone), 0, 1)
    x = x / m × scaled
    y = y / m × scaled
```

Optioneel:

- outer deadzone;
- curve exponent;
- debugraw/processed waarden.

### Camera

AM64DS gebruikt D-pad/camera. Het SM64DS-profiel vertaalt rechterstick naar digitale D-padrichtingen met hysterese:

```text
activate threshold: 0.55
release threshold:  0.35
```

Voorkom flapperen rond het midden.

### Run/sprintgedrag

Niet aannemen dat full magnitude exact hetzelfde voelt als N64-Mario. Test:

- langzaam lopen;
- normaal lopen;
- rennen;
- direction changes;
- sprint-/runbutton indien de game die nog gebruikt;
- swimming;
- flying;
- shell;
- slopes.

Bied indien nodig twee profielvarianten:

```text
Analog Full Range
Analog + Original Run Modifier
```

maar maak pas een tweede optie als het gedrag in de game aantoonbaar verschilt.

## Runtimecode versus IPS

Voorkeur: runtime Action Replay.

Voordelen:

- source ROM ongewijzigd;
- originele RA-hash;
- per-profieltoggle;
- makkelijk uit te schakelen;
- geen gepatchte ROMcache nodig.

IPS blijft alleen als compatibiliteits-/debugpad.

## Provenance

Leg vast:

- bronrepository;
- branch/commit;
- oorspronkelijke auteur;
- codehash;
- onze normalisatie;
- exacte ROMvariant;
- testresultaten.

Verander de patchcode niet zonder decomp-/disassemblybewijs en een nieuwe profielversie.
