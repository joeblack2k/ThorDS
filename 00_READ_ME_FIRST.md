# ThorDS Enhanced — lees dit eerst

Dit dossier is de uitvoeringsspecificatie voor een **Thor-first fork van MelonDualDS**. De eerste volledig ondersteunde game is **Super Mario 64 DS (Europa)**. Het product blijft een algemene Nintendo DS-emulator, maar herkende games kunnen een gecontroleerd enhancementprofiel krijgen met onder meer analoge besturing, echte widescreen-presentatie, game-specifieke controllerinstellingen en een duidelijk RetroAchievements-beleid.

## Wat jij vooraf doet

1. Maak een lege map met de naam `MelonDS`.
2. Zet jouw eigen, ongewijzigde Europese `Super Mario 64 DS`-ROM als `.nds`-bestand direct in die map.
3. Pak deze ZIP in dezelfde map uit. De map bevat dan de ROM en dit dossier.
4. Sluit de AYN Thor via ADB aan en controleer:

   ```bash
   adb devices -l
   ```

5. Open de map `MelonDS` als werkmap in Codex.
6. Kies **Luna Xhigh**.
7. Plak de volledige inhoud van `01_GOAL_PROMPT.md` als `/goal`.

Je hoeft MelonDualDS niet zelf te klonen. Luna doet de bootstrap veilig in de reeds gevulde map, zonder de ROM te tracken of te wijzigen.

## Gewenst eindproduct

De eerste release heet voorlopig **ThorDS Enhanced** en levert:

- een werkende Android APK op de fysieke AYN Thor;
- automatische herkenning van de twee ingebouwde displays;
- het DS-bovenscherm op het brede bovenpaneel;
- het DS-onder-/touchscreen aspect-correct op het onderste paneel;
- een ingebouwd `Super Mario 64 DS — Thor Enhanced`-profiel;
- Europese AM64DS-analogbesturing via runtime Action Replay plus Slot-2 Analog Input;
- rechterstickcamera;
- **True Widescreen** waarbij de 3D-wereld 16:9 vult, terwijl HUD, tekst en andere 2D-assets niet horizontaal worden uitgerekt;
- RetroAchievements als gebruikerskeuze: `Uit`, `Casual` of `Hardcore`;
- Hardcore alleen in een ongewijzigd `Original`-profiel;
- een experimentele, standaard uitgeschakelde ARM9-overclockfundering;
- geen standaard ingeschakelde 60fps-patch totdat die aantoonbaar stabiel is;
  60fps blijft wel een verplichte product- en release-eis.

## Belangrijk onderscheid

`True Widescreen` betekent in dit project niet alleen het volledige 256×192-frame uitrekken. De game krijgt een 16:9-projectie/cullingpatch en de Vulkan-presentatielaag behandelt 3D en 2D afzonderlijk:

```text
3D-wereld    → volledige 16:9-breedte
2D-HUD       → gecentreerde 4:3 safe area
2D-menu      → volledig 4:3, geen vervorming
onderste DS  → altijd 4:3
```

Als een scène niet betrouwbaar als 3D-plus-HUD kan worden geclassificeerd, moet zij veilig terugvallen naar gecentreerd 4:3. Een vervormd beeld mag nooit als “True Widescreen” worden geaccepteerd.

## Uitvoeringsvolgorde

Luna leest en gebruikt minimaal:

1. `01_GOAL_PROMPT.md`
2. `03_NON_NEGOTIABLES.md`
3. `06_EXECUTION_ORDER.md`
4. `research/SOURCE_PINS.md`
5. `implementation/MILESTONE_PLAN.md`
6. de mijlpaalspecificatie van de actieve fase;
7. de relevante architectuur-, profiel- en testdocumenten.

`05_PROJECT_MAP.md` wijst per onderwerp naar de juiste bestanden.

## Wat dit pakket niet bevat

- geen ROM;
- geen Nintendo-assets;
- geen vooraf gebouwde APK;
- geen onbekende YouTube-60fps-patch;
- geen releaseclaim voor 60fps voordat de volledige gameplay-acceptatie groen is;
- geen toestemming om de ROM of afgeleide volledige ROM-bestanden te committen of distribueren.

Het pakket is de complete bouw-, onderzoeks-, test- en acceptatiespecificatie waarmee Luna de broncode en APK in deze werkmap moet maken.
