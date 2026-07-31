# Definitie van gereed

ThorDS Enhanced v0.1 is pas gereed wanneer alle onderstaande categorieën aantoonbaar slagen.

## 1. Bron en build

- HEAD stamt aantoonbaar af van MelonDualDS `0.7.0.rc5`.
- Alle submodules staan op de door de tag voorgeschreven commits.
- `git status` is schoon behalve expliciet genegeerde lokale ROM/testevidence.
- `:app:assembleGitHubProdDebug` slaagt.
- Unit-, JVM-, native en relevante instrumentationtests slagen.
- APK SHA-256 is vastgelegd.
- Package-id is ThorDS-specifiek en botst niet met MelonDualDS.

## 2. ROMveiligheid

- Europese ROM wordt automatisch herkend op gamecode/revision/RA-hash.
- Volledige SHA-256 wordt lokaal geregistreerd.
- ROM staat niet in Git-index, history, buildartifact of ZIP.
- Source ROM is na alle tests byte-identiek.
- Runtimepatches werken zonder bron-ROM te wijzigen.
- Cachepatching heeft atomic write, checksumcontrole en cleanup.

## 3. Thor-hardware

- Beide ingebouwde displays worden gevonden.
- Fysieke roltoewijzing is correct, ook na relaunch en sleep/resume.
- Top DS-output staat op bovenpaneel.
- Bottom DS-output staat op onderpaneel.
- Touchcoördinaten corresponderen met bottom DS-pixels.
- Virtuele controls zijn standaard verborgen.
- Controllerinput blijft na relaunch correct.
- Een onverwacht verdwenen display veroorzaakt geen gamecrash of savecorruptie.

## 4. SM64DS analog

- Europese AM64DS runtimecode wordt exact en conditioneel geladen.
- Slot-2 Analog Input is actief.
- Linkerstick levert continue magnitude en hoek.
- Radiale deadzone en bereikrescaling zijn getest.
- Rechterstickcamera heeft hysterese en blijft onafhankelijk van linkerstick.
- Geen library-scanverlies of controllerconfigcorruptie.
- Alle verplichte bewegingstests slagen.

## 5. True Widescreen

- 3D-gameplay vult het 16:9-bovenpaneel.
- De wereld toont meer horizontale inhoud; geen crop of simpele stretch.
- Culling aan linker- en rechterzijde is correct.
- Power meter en ronde iconen blijven rond.
- Tekstglyphs behouden aspectratio.
- 2D-only scènes blijven 4:3.
- Bottom screen blijft 4:3.
- Ambigue scènes vallen zonder visuele breuk terug.
- Geen ontbrekende 2D-lagen, zwarte 3D-holes, oude frameghosts of capturecorruptie.
- De volledige scènematrix is vastgelegd met screenshots en geautomatiseerde geometriechecks.

## 6. RetroAchievements

- Uit schakelt RA volledig uit.
- Casual werkt met analog en True Widescreen actief.
- Een normale testunlock kan door gameplay worden waargenomen en gesubmit.
- Hardcore is niet selecteerbaar met enhancements.
- Original-profiel kan de bestaande Hardcore-regels volgen.
- Save-state load, rewind en cheats worden in Hardcore geblokkeerd.
- Client gebruikt eigen stabiele User-Agent.
- Geen credentials/tokens in logs/evidence.

## 7. ARM9-overclockfundering

- Instellingen en profielcapability bestaan.
- Standaard is 100%.
- Ratio wordt correct door Kotlin/JNI/native core doorgegeven.
- Telemetrie toont CPU-budget en timing.
- Veranderen vereist veilige reset/relaunch.
- Save-statecompatibiliteit is behandeld.
- Wanneer >100% effectief is ingeschakeld, bewijzen tests dat wandklok, audio en gameplaytempo gelijk blijven.
- Wanneer dat bewijs ontbreekt, blijft >100% developer-only en functioneel geblokkeerd; dat is eerlijk gedocumenteerd.

## 8. UX

- Thor opent met logische defaults.
- Enhancementstatus is op één plaats zichtbaar.
- Play-flow vereist geen handmatige cheat- of layoutconfiguratie.
- Pauzemenu verschijnt bruikbaar op het onderste scherm.
- Reset-to-safe-layout bestaat.
- Fouten noemen de exacte ROM/profile/rendererreden.
- Generieke DS-games blijven speelbaar in een veilige algemene modus.

## 9. Stabiliteit

- 60 minuten SM64DS zonder crash.
- Meerdere leveltransities.
- Save schrijven, app sluiten, relaunch en save laden.
- Sleep/resume.
- Scherm open/dicht of display lifecycle waar Thor dit rapporteert.
- Rendererwissel of correcte blokkering wanneer wissel tijdens sessie onveilig is.
- Geen ernstige logcatfouten, ANR of memory growth buiten budget.

## 10. Levering

- Releasechecklist ingevuld.
- Eindrapport ingevuld.
- Bekende beperkingen vermeld.
- 60fps expliciet als volgende doel of bewezen feature gemarkeerd.
- APK-pad, hash, commits en ADB-bewijs aanwezig.
