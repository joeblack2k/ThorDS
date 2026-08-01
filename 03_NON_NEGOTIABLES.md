# Niet-onderhandelbare regels

Deze regels gaan vóór gemak, snelheid en esthetiek.

## ROM en auteursrechtelijk materiaal

- De gebruikers-ROM wordt nooit in Git opgenomen.
- De ROM wordt nooit in-place gewijzigd.
- Volledige gepatchte ROMs worden niet gegenereerd buiten app-private cache tenzij de gebruiker daar lokaal expliciet voor kiest.
- App-private cachebestanden zijn niet exporteerbaar via normale releasefunctionaliteit.
- Geen uit de ROM geëxtraheerde Nintendo-assets worden in bron, tests, fixtures, screenshots voor distributie of releasepackages opgenomen.
- Testfixtures gebruiken synthetische bytes of door de test zelf gegenereerde data.
- De ZIP met dit dossier bevat uitsluitend Markdown.

## Basis en reproduceerbaarheid

- Baseline: `SapphireRhodonite/melonDS-android` tag `0.7.0.rc5`.
- Verwachte tagcommit: `9b28076281545a1e08dccee0b3f925febb8933ac`.
- Clone altijd met recursieve submodules.
- Een afwijkende basis vereist een ADR, buildvergelijking en regressiereden.
- Geen branch-HEAD gebruiken wanneer een pin is opgegeven.

## True Widescreen

- Geen globale framebufferstretch als eindoplossing.
- Geen uitgerekte power meter, letters, munten, sterren, menu’s of bottom-screenkaart.
- Geen “true” label zonder objectieve geometrie- en scènetests.
- Vulkan is de eerste ondersteunde true-widescreenrenderer.
- Onveilige scènes vallen terug naar 4:3.
- Bottom screen blijft aspect-correct.
- Culling en projectie moeten de bredere wereld daadwerkelijk ondersteunen.

## Patches

- Alleen exacte ROMmatch.
- Check-before-write waar Action Replay dit ondersteunt.
- Geen bekende Amerikaanse adressen op de Europese ROM.
- Patches hebben herkomst, versie, checksum en teststatus.
- Onbekende YouTube-binaries zijn geen vertrouwde productdependency.
- Communitycode mag worden hergebruikt conform licentie; communitybinary’s worden eerst geanalyseerd en waar nodig reproduceerbaar opnieuw opgebouwd.
- Gebruikerscheats en gecureerde enhancements blijven afzonderlijk zichtbaar en beheerd.

## RetroAchievements

- De RA-toggle blijft van de gebruiker.
- Casual mag actief zijn met enhancements.
- Hardcore is niet beschikbaar met:
  - runtimepatches;
  - user cheats;
  - ARM9-overclock;
  - rewind;
  - save-state load;
  - slowdown/frame advance;
  - andere niet-Hardcore-veilige functies.
- Geen hashspoofing.
- Geen handmatige awardcalls.
- Geen identificatie als een andere emulator.
- Credentials en tokens worden nooit gelogd.
- Modewijziging naar Hardcore vereist reset/relaunch.

## ARM9-overclock en 60fps

- Overclock staat standaard uit/100%.
- Overclock is geen fast-forward.
- Audio, VBlank, ARM7, RTC en timers blijven normale tijd volgen.
- Geen blinde wijziging van `ARM9ClockShift`.
- 60fps staat standaard uit.
- Een 60fps-counter alleen is geen acceptatie.
- Slow motion, versneld gedrag of dubbele gamelogica betekent fail.
- Release 1 mag niet als compleet worden gemarkeerd zonder de gevalideerde 60fps-feature.

## Thor-validatie

- ADB-fysiek bewijs is verplicht.
- Geen hardcoded display-ID.
- Displayrollen komen uit actuele device-inventaris.
- Controller- en touchscreenbewijs komen van de echte Thor.
- Sleep/resume en onverwachte Presentation-recovery worden getest.
- Geen voltooiingsclaim op basis van desktop/emulator-only tests.

## Engineering

- Geen grote ongecontroleerde rewrite.
- Geen productiecode zonder tests voor profielmatching en patchconflicten.
- Geen swallowed exceptions in launch-, patch- of displaypaden.
- Geen secrets in logs of artifacts.
- Geen push/PR zonder expliciete gebruikersopdracht.
- Iedere mijlpaal eindigt met bewijs en een gerichte commit.
