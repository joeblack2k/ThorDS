# Toekomstprofiel — The Legend of Zelda: Phantom Hourglass

## Waarom kandidaat

- sterk touchscreen-georiënteerd origineel;
- Thor heeft echt bottom touchscreen;
- bestaande open-source D-padpatch;
- Europese en Amerikaanse ondersteuning;
- actieve decomp;
- RetroAchievements heeft compatibiliteitspaden voor bekende varianten.

## Bronnen

```text
https://github.com/StraDaMa/Legend-of-Zelda-Phantom-Hourglass-D-Pad-Patch
https://github.com/zeldaret/ph
```

## Potentiële enhancements

- D-pad/left stick movement;
- fysieke attack/interact/roll;
- touch behouden voor:
  - kaartnotities;
  - boomerangroutes;
  - specifieke puzzels;
- mic-QoL waar verantwoord;
- top/bottom Thor-layout;
- hogere interne resolutie;
- eventuele layer-aware widescreen pas na gameaudit;
- RA user toggle/Hardcore policy.

## Profielstrategie

De bestaande patch is source-based en gebruikt armips/BLZ/ndstool. Dat maakt een reproduceerbare BPS/IPS-cachepatch waarschijnlijk logischer dan alleen AR.

Vereist:

- exact supported ROMhash;
- patchbuild in tooling, niet ROM in Git;
- target hash;
- save compatibility;
- RA hash/compatibility controleren;
- touch fallback.

## Niet onderdeel van v0.1

Alleen:

- schema future-proof;
- roadmap;
- testbare generic patchengine.

Geen Zelda-code toevoegen voordat SM64DS M12 groen is.
