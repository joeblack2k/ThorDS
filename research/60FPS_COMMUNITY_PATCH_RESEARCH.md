# SM64DS 60fps — communitypatchonderzoek

## Status

De gekoppelde AYN Thor-praktijkproef bewijst dat er een combinatie van:

- analoge besturing;
- widescreen;
- een 60fps-gamepatch;
- MelonDualDS;
- de fysieke Thor

kan worden gestart en gespeeld. Dezelfde proef meldt echter reproduceerbare slow motion en gameplayvertraging in zwaardere scènes, waaronder Chain Chomp, de berg en King Bob-omb. Daarom is de communitypatch **onderzoeksmateriaal**, geen vertrouwde releasepatch.

De publiek terugvindbare 60fps-patchprovenance verwijst naar de
NTSC-U/USA revision 1.1-ROM. ThorDS ondersteunt als eerste doel de Europese
`ASMP`, revision `0`; de Amerikaanse patch wordt daarom niet op de Europese
profilecatalogus toegepast. Zie ook de publieke patchverwijzing in
`SOURCE_INDEX.md`.

Provenance:

- oorspronkelijke patchvideo/auteur: `gamemasterplc`,
  `https://www.youtube.com/watch?v=yJXEAIOFcNU`;
- publieke distributiepagina:
  `https://www.youtube.com/watch?v=yJXEAIOFcNU`;
- de bronverwijzing beschrijft een xdelta-patch voor USA/NTSC-U v1.1;
- publieke broncode, AR-codewoorden en reproduceerbare binary-to-source map
  zijn niet gevonden.

## Productbesluit

Voor v0.1:

```text
Analog Controls       releasefeature
True Widescreen       releasefeature
ARM9 OC foundation    experimentele fundering
60fps                 verplichte product- en releasefeature
```

Geen onbegrepen binary, vooraf gepatchte ROM of YouTube-download wordt in de repository, APK of profielcatalogus opgenomen.

## Onderzoeksprocedure

Wanneer een legale lokale kopie van de communitypatch beschikbaar is:

1. bewaar de ongewijzigde Europese ROM als enige bron van waarheid;
2. maak uitsluitend lokaal een gepatchte testkopie;
3. leg volledige SHA-256 van beide bestanden vast;
4. diff ARM9, overlays, NitroFS en header afzonderlijk;
5. map iedere gewijzigde instruction naar:
   - runtimeadres;
   - ROMoffset;
   - overlay;
   - functie/symbool in de vastgepinde decomp;
6. classificeer wijzigingen als:
   - frame-skip;
   - VBlank-/IRQ;
   - delta/timestep;
   - physics;
   - animation;
   - object timers;
   - audio;
   - renderer;
7. schrijf de bedoelde wijziging opnieuw als begrijpelijke, reproduceerbare source-/AR-/BPS-patch;
8. test zonder en met ARM9-overclock;
9. distribueer alleen de deltapatch en provenance, nooit de ROM.

## Mogelijke oorzaken van slowdown

Onderzoek minimaal:

- game vraagt 60 updates maar ARM9 haalt de work budget niet;
- frame-delta is gehalveerd zonder alle timers aan te passen;
- object- of animationtimers lopen nog op 30Hz;
- VBlankcounter en gameplaycounter hebben verschillende cadence;
- GPU/GXFIFO-stalls blokkeren ARM9;
- ARM7/audio-IPC verwacht de originele verhouding;
- emulator presenteert 60Hz maar game produceert niet 60 unieke updates.

Een zichtbare 60fps-counter bewijst geen correcte 60fps.

## Acceptatie

De verplichte 60fps-toggle blijft verborgen of experimenteel totdat hij
`VALIDATED` is. `VALIDATED` vereist:

- 60 unieke gameplayupdates per seconde zijn gemeten;
- tien minuten wandklok exact overeenkomt met gametijd binnen de testtolerantie;
- audio pitch en tempo normaal blijven;
- physics, vijanden, platformen en cutscenes niet dubbel of half lopen;
- bekende stressscènes geen slow motion tonen;
- save/load, sleep/resume en RA Casual blijven werken;
- minimaal zestig minuten stabiliteit is bewezen.

Zie `implementation/M13_60FPS_NEXT_GOAL.md` en `testing/PERFORMANCE_BENCHMARKS.md`.
