# Audit van de SM64DS-decomp als patchbron

## Repository

```text
tangosdev/sm64ds-decomp
research pin: 2d38fe9b825199deec408240849b64b91c965d85
target ROM: Europese retailbuild
```

Het is een matching decompilation: bronfuncties zijn bedoeld om byte-identiek terug naar de DS-ROM te compileren. Dat is bijzonder waardevol voor patchanalyse.

## Gebruik in ThorDS

De decomp wordt niet als Androidengine gelinkt. Hij dient voor:

- symbolen en functierollen;
- game-side aspectratio;
- culling;
- HUD-/sceneanalyse;
- timing en frame skip;
- patchadresderivatie;
- reproduceerbare ARMips/BPS/AR-generatie;
- verificatie van communitypatchdiffs.

## Widescreenrelevante bevindingen

### `Clipper`

De herstelde struct bevat:

```c
Fix12i aspectRatio;
```

en initialiseert deze met:

```text
0x1555
```

### 3D-initialisatie

De perspectiefsetup ontvangt eveneens `0x1555`.

### Meerdere codepaden

Een repositorysearch vindt meer voorkomens. Niet ieder voorkomen is automatisch aspectratio; Luna classificeert:

- camera/clipperinit;
- cutscene/viewobjectinit;
- overlay-specifieke perspective;
- niet-gerelateerde numerieke constants.

Alle productiepatchlocaties moeten semantisch worden bewezen.

## Patchderivatie

1. clone exact de researchpin;
2. gebruik de eigen EU-ROM volgens de decompinstructies, lokaal en gitignored;
3. genereer symbols/maps;
4. zoek de echte functies;
5. identificeer de geladen ARM9-/overlayadressen;
6. vergelijk runtimegeheugen met decompadres;
7. maak conditional AR-writes of een gecontroleerde hook;
8. verifieer originele woordwaarde vóór write;
9. test iedere scèneklasse.

## 60fpsrelevante gebieden

Onderzoek:

- VBlank-handler;
- globale framedelta;
- gameframe skip;
- scene update versus render;
- object timers;
- animation stepping;
- input sampling;
- audio cadence;
- physics integrators;
- RNG/frame counters;
- overlays die een eigen updatecadans hebben.

De DS-video loopt rond 60Hz, terwijl veel SM64DS-gameplay effectief iedere tweede videoframe update. Een correcte 60fps-patch moet niet alleen de skip verwijderen; alle deltatijdafhankelijke systemen moeten consistent blijven.

## Grenzen

- De decompstatus kan hoog zijn maar namen/structuren zijn niet overal volledig opgeschoond.
- Matchingcode kan hardwareadressen en NitroSDKcalls behouden.
- Een numerieke constante zonder context is geen patchbewijs.
- Decompcode en ROM-assets blijven afzonderlijk.
- Geen proprietary compiler of ROMdata wordt gedistribueerd.

## Deliverables uit de audit

Luna maakt:

```text
docs/research/sm64ds-widescreen-symbol-map.md
docs/research/sm64ds-aspect-sites.json
docs/research/sm64ds-60fps-candidate-sites.md
docs/evidence/m7/eu-aspect-original-words.txt
```

De JSON mag alleen adressen, symbolen en hashes bevatten, geen ROMbytes buiten de noodzakelijke instructiewoorden.
