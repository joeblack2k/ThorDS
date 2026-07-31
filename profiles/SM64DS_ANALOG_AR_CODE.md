# SM64DS Europe — AM64DS Action Replay-code

## Status

```text
profile patch id: sm64ds.eu.am64ds-analog.v1
variant:          Europe / ASMP
source:           LRFLEW AM64DS
type:             runtime Action Replay
default:          enabled in Enhanced
Hardcore:         incompatible
```

## Autoritatieve code

Gebruik exact deze woorden voor de Europese release:

```text
02024D60 EA00000C
E202C404 00000040
E3A03301 E3833F81
E1D300B0 E3C00080
E1C300B0 E3A03409
E3833C01 E8930003
E3700001 03A00000
03A01000 E2893008
E8830003 E3A00001
E5C90014 EA0000B4
E2075658 00000008
02000100 00000000
520FA7C0 E7D22001
020FA7C0 E3A02000
D0000000 00000000
520FC0C0 E19100B0
020FC0C0 EA000070
D2000000 00000000
```

Bron:

```text
https://github.com/LRFLEW/AM64DS_DeSmuME/blob/analog/PATCHES.md
```

## Canonical payloadhash

Canonicalisatie:

- uppercase hex;
- één spatie tussen de twee woorden;
- Unix newline na iedere regel, inclusief de laatste.

Verwachte SHA-256:

```text
e68025c3aad3a47941ab2903dd9d212b91bafedff705ea6252677c27d07bdb1c
```

## Parsevorm

Bewaar in catalogus als:

- genormaliseerde uppercase hex;
- twee 32-bit words per regel;
- geen comments in runtimepayload;
- SHA-256 van canonical bytes/string.

## Applicability

Alleen wanneer:

```text
gameCode == ASMP
revision == expected
raHash == ba3c4052e00c5cc31df5d5534c39de1b
Slot2 capability == ANALOG_INPUT
```

## Integratie

De session plan:

1. zet GBA Slot op `AnalogInput`;
2. voegt deze code toe aan curated codes;
3. configureert left-stick route;
4. configureert right-stick camera;
5. start pas daarna emulatie.

## Verificatie

- AR engine accepteert alle words;
- conditional checks falen veilig op mismatch;
- game leest Slot-2 state;
- raw and processed analog telemetry;
- geen source ROM wijziging;
- toggle off verwijdert code volledig na relaunch/reset;
- user cheat list blijft apart.

## Niet aanpassen zonder bewijs

Geen adres, instruction of regionvariant wijzigen zonder:

- decomp/disassembly;
- original instruction capture;
- nieuwe patch id/version;
- volledige analog regressietest.
