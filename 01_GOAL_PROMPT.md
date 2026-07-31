/goal

Werk als de primaire programmeer- en integratieagent voor dit project. Je draait als **Luna Xhigh** in de huidige werkmap `MelonDS`. Lever een werkende, geteste Android-fork op; schrijf niet alleen een plan.

# Einddoel

Bouw **ThorDS Enhanced v0.1**: een Thor-first fork van MelonDualDS met `Super Mario 64 DS (Europe)` als eerste gecureerde enhancementprofiel.

De verplichte releasefunctionaliteit is:

1. MelonDualDS `0.7.0.rc5` als exact vastgepinde basis.
2. Een veilige bootstrap in de huidige, reeds gevulde map.
3. De gebruikers-ROM blijft ongewijzigd, ongecommit en lokaal.
4. Automatische AYN Thor-detectie en betrouwbare displayroltoewijzing.
5. Bovenste DS-scherm op het fysieke 1920×1080-achtige bovenpaneel.
6. Onderste DS-touchscreen aspect-correct op het lagere paneel.
7. Een generieke Enhancement Profile Engine.
8. Een `Super Mario 64 DS — Thor Enhanced`-profiel voor de Europese retail-ROM.
9. AM64DS-analog via de bekende Europese runtime Action Replay-code en Slot-2 Analog Input.
10. Linkerstick als echte analoge beweging en rechterstick als camerabesturing.
11. **True Widescreen**:
    - correcte 16:9-gameprojectie en culling;
    - 3D-wereld over de volledige 16:9-breedte;
    - 2D-HUD, tekst, iconen en menu-assets zonder horizontale rek;
    - onderste scherm altijd aspect-correct;
    - veilige 4:3-fallback voor 2D-only of ambigue scènes.
12. RetroAchievements als gebruiker-toggle:
    - `Off`;
    - `Casual`, ook wanneer enhancements actief zijn;
    - `Hardcore` uitsluitend wanneer het profiel `Original` actief is en alle enhancements/cheats/overclock/ongeoorloofde functies uit staan.
13. Een unieke, stabiele ThorDS Enhanced User-Agent.
14. Een experimentele ARM9-overclockfundering die standaard op 100%/uit staat:
    - configuratie;
    - JNI/core plumbing;
    - telemetrie;
    - veilige reset/relaunch-policy;
    - echte extra ARM9-headroom alleen wanneer met timingtests bewezen.
15. Een Thor-first launcher, instellingenstroom en lower-screen-pauzemenu.
16. Bouwen, installeren en testen op de via ADB aangesloten fysieke Thor.
17. Een APK, commits en volledig bewijsdossier.

60fps is **niet** vereist als standaardfeature van v0.1. Bouw wel de overclockfundering en de reproduceerbare onderzoeksharness voor de volgende mijlpaal. Voeg geen onbekende 60fps-binary toe en claim geen 60fps zonder volledige acceptatie.

# Leesvolgorde

Lees vóór wijzigingen:

1. `00_READ_ME_FIRST.md`
2. `03_NON_NEGOTIABLES.md`
3. `04_DEFINITION_OF_DONE.md`
4. `06_EXECUTION_ORDER.md`
5. `research/SOURCE_PINS.md`
6. `research/BASE_REPOSITORY_SELECTION.md`
7. `architecture/WORKSPACE_BOOTSTRAP.md`
8. `architecture/SYSTEM_ARCHITECTURE.md`
9. `implementation/MILESTONE_PLAN.md`
10. daarna per mijlpaal de gekoppelde documenten uit `05_PROJECT_MAP.md`.

Lees de overige bestanden doelgericht zodra de actieve mijlpaal ze noemt. Houd `operations/STATUS_TEMPLATE.md`, `operations/WORKLOG_TEMPLATE.md` en ADR’s tijdens het werk actueel.

# Werkmap en ROM

- Zoek in de huidige root naar `.nds`-bestanden.
- Selecteer automatisch de ROM die:
  - gamecode `ASMP` heeft;
  - een Europese retailrevision is;
  - bij voorkeur de verwachte RetroAchievements Nintendo DS-hash `ba3c4052e00c5cc31df5d5534c39de1b` heeft.
- Behandel deze waarde als een RA-systeemhash, niet automatisch als volledige-bestands-MD5.
- Bereken daarnaast lokaal volledige SHA-256, bestandsgrootte, header CRC en revision byte.
- Log alleen bestandsnaam en hashes; log of kopieer nooit ROM-inhoud.
- Voeg vóór de eerste commit minimaal `*.nds`, `*.srl`, ROM-cache en afgeleide ROM-bestanden toe aan git-excludes.
- Wijzig de bron-ROM nooit in-place.
- Commit, upload, archiveer of verspreid geen ROM of uit de ROM geëxtraheerde Nintendo-assets.
- Wanneer meer dan één kandidaat bestaat, kies de exacte ASMP/RA-hashmatch. Stel geen vraag wanneer de juiste kandidaat objectief kan worden bepaald.
- Wanneer geen geldige ROM bestaat, voltooi alle bron-, build-, GUI- en testharnesswerkzaamheden die zonder ROM kunnen; documenteer daarna de exacte blocker.

# Veilige repositorybootstrap

Wanneer de root nog geen gitrepository is:

1. inventariseer en hash de ROM;
2. voer `git init` uit;
3. bescherm de ROM direct via `.git/info/exclude`;
4. voeg remote `upstream` toe voor `https://github.com/SapphireRhodonite/melonDS-android.git`;
5. fetch exact tag `0.7.0.rc5`;
6. maak branch `thords/enhancement-platform-v1` vanaf die tag;
7. initialiseer alle submodules recursief;
8. verifieer dat HEAD overeenkomt met tagcommit `9b28076281545a1e08dccee0b3f925febb8933ac`;
9. voeg duurzame ROM-excludes toe aan de bestaande `.gitignore`;
10. commit dit dossier en de bootstrapadministratie zonder ROM.

Vernietig of overschrijf geen bestaande repository. Als `.git` al bestaat, inspecteer remote, HEAD, status en submodules en pas de procedure veilig aan.

# Uitvoering

Werk de verplichte mijlpalen **M0 t/m M12** in volgorde af. M13 is de volgende 60fps-doelstelling; lever daarvoor alleen de in M13 vereiste research-, instrumentatie- en disabled-by-default-fundering op tenzij M0–M12 volledig bewezen zijn en een stabiele 60fps-implementatie werkelijk door alle gates komt.

Voor iedere mijlpaal:

1. lees de mijlpaalspecificatie;
2. inspecteer de echte broncode; vertrouw niet op oude padnamen;
3. noteer de baseline;
4. implementeer een kleine, samenhangende wijziging;
5. bouw relevante unit-/instrumentatie-/Androidtargets;
6. voer statische checks en tests uit;
7. installeer waar relevant op de Thor;
8. verzamel ADB/logcat/screenshot/performancebewijs;
9. werk status, worklog en eventuele ADR bij;
10. commit met een gerichte commitboodschap;
11. ga alleen door als de gate aantoonbaar groen is of de blocker expliciet is vastgelegd.

Maak geen grote ongeteste einddump.

# Technische kernregels

## Basis

- Gebruik exact MelonDualDS tag `0.7.0.rc5`.
- Gebruik de daarin vastgepinde core-submodule; wijzig een core-pin alleen via een ADR en met volledige regressiebewijzen.
- Gebruik de huidige Gradle/SDK/NDK/Java/Rust-toolchain uit de tag.
- Eerste buildtarget: `:app:assembleGitHubProdDebug`.
- Maak een afzonderlijke applicationId, voorlopig `io.github.joeblack2k.thords`.
- Houd de bestaande Kotlin/Java namespace aanvankelijk intact tenzij een gecontroleerde refactor nodig is.
- Schakel de upstream updater uit of laat hem uitsluitend ThorDS Enhanced-releases accepteren.

## Profielen en patches

- Bouw een afzonderlijke Enhancement Profile Engine; vermeng gecureerde enhancements niet onzichtbaar met de gebruikers-cheatdatabase.
- Ondersteun:
  - runtime Action Replay-codes;
  - emulatorfeatureflags;
  - controller- en layoutoverrides;
  - BPS/IPS-cachepatches als generieke infrastructuur.
- Runtimecodes worden pas na ROM-identificatie, maar vóór game-executie geladen.
- Profielen matchen exact op gamecode, revision en systeemhash; geen “ongeveer passende” adressen.
- Iedere patch heeft provenance, auteur, bron, versie, checksum, dependencies, conflicten en teststatus.
- Gebruik de Europese AM64DS-code exact zoals vastgelegd in `profiles/SM64DS_ANALOG_AR_CODE.md`.
- De bekende Amerikaanse Reddit-widescreenadressen zijn alleen onderzoeksreferentie en mogen niet in het Europese productieprofiel worden gekopieerd.

## True Widescreen

- Begin met een diagnostische Vulkan-spike.
- Gebruik de bestaande gestructureerde Vulkan-compositor die actuele 3D, vorige 3D, packed 2D/control en capture-3D afzonderlijk beschikbaar heeft.
- Leid alle relevante Europese aspect-/cullingpatchlocaties af uit de lokale ROM en de vastgepinde SM64DS-decomp.
- Voeg een expliciete presentatiemodus toe, geen impliciete stretchhack.
- Voor wereldscènes:
  - 3D gebruikt de volledige 16:9-viewport;
  - HUD/2D-overlay gebruikt een gecentreerde 4:3-safe-area;
  - onderste scherm blijft 4:3.
- Voor 2D-only schermen: gecentreerd 4:3.
- Voor ambigue/capture/transities: veilige 4:3-fallback.
- OpenGL krijgt niet stilzwijgend het label True Widescreen wanneer de laagseparatie ontbreekt.
- Voeg debugweergaven toe voor 3D-only, 2D-underlay, 2D-overlay, controlmask en final composite.
- Claim True Widescreen pas wanneer de volledige matrix uit `testing/TRUE_WIDESCREEN_ACCEPTANCE_TESTS.md` groen is.

## Analog

- Linkerstick gaat via Slot-2 Analog Input.
- Verbeter de huidige per-as deadzone naar een geteste radiale deadzone en bereikrescaling.
- Rechterstick wordt via een gameprofiel met hysterese naar DS-D-pad/camera gemapt.
- Virtuele knoppen staan op de Thor standaard uit.
- Behoud een herstelbare globale controllerconfiguratie; een profieloverride mag ROM-scanning of de bibliotheek nooit breken.
- Test lopen, rennen, sluipen, diagonalen, zwemmen, vliegen, glijden en camera.

## RetroAchievements

- Behoud de bestaande veilige login/tokenopslag.
- Gebruik een eigen User-Agent voor ThorDS Enhanced; identificeer niet als een andere client.
- De gebruiker kiest RA aan of uit.
- Casual mag met enhancements actief blijven.
- Hardcore is zichtbaar maar niet selecteerbaar zodra een enhancement, runtimecode, user cheat, overclock, rewind/load-state of niet-goedgekeurde functie actief is.
- Overschakelen naar Hardcore vereist Original-profiel en volledige reset/relaunch.
- Overschakelen van Hardcore naar Casual mag volgens bestaande regels, maar toon dit duidelijk.
- Omzeil geen RA-hash, servercontrole of submissionbeleid.
- Test één normale Casual-unlock met de originele Europese ROM en actieve enhancements door normale gameplay; manipuleer geen geheugen en gebruik geen award-endpoint.
- Doe daarnaast een Original/Hardcore-policytest zonder per se een accountunlock te forceren wanneer dat onnodige accountvervuiling zou geven.

## ARM9-overclock

- Bouw eerst meetinstrumentatie.
- Maak instellingen `100/125/150/175/200%`, standaard `100%`.
- Overclock is experimenteel en alleen voor enhanced/Casual/offline.
- Overclock mag geen fast-forward zijn: video, VBlank, timers, ARM7 en audio blijven op normale wandklok.
- Verander `ARM9ClockShift` of schedulertimestamps niet blind.
- Schrijf vóór de corewijziging een ADR met ten minste twee onderzochte implementatiestrategieën.
- Vereis pause/reset/relaunch bij ratioverandering tenzij live wijzigen bewezen veilig is.
- Serializeer de ratio in save states of weiger een incompatibele state met een heldere fout.
- Bij onvoldoende bewijs blijft de feature op 100% effectief en verborgen achter developer/experimental UI; lever dan wel de complete plumbing en telemetrie.

# Thor- en ADB-regels

Gebruik de echte Thor als bron van waarheid:

```bash
adb devices -l
adb shell getprop ro.product.manufacturer
adb shell getprop ro.product.model
adb shell dumpsys display
adb shell dumpsys window displays
adb shell wm size
adb shell wm density
```

Verzamel per relevante gate:

- buildvariant en APK SHA-256;
- package/version;
- display-ID’s, namen, flags, afmetingen en refreshmodes;
- installatie- en startbewijs;
- logcat zonder crash;
- screenshots van beide fysieke displays waar mogelijk;
- touchbewijs op het onderste scherm;
- controllerinputbewijs;
- frame pacing en renderergegevens;
- sleep/resume- en display-recoverybewijs.

Gebruik geen hardcoded display-ID. Gebruik gemeten roltoewijzing met AYN-identiteit en dimensies, plus veilige fallback.

# Bewijs en eerlijkheid

Een build zonder fysieke test is geen Thor-proof.
Een gestretcht volledig framebuffer is geen True Widescreen.
Een zichtbare login is geen RA-unlockbewijs.
Een 60fps-counter is geen bewijs van 60 unieke correcte gameplayframes.
Een werkend titelscherm is geen gameacceptatie.

Gebruik `testing/EVIDENCE_REQUIREMENTS.md` en lever onder `docs/evidence/` minimaal:

- baseline;
- buildlogs;
- testresultaten;
- ADB-inventaris;
- screenshots;
- true-widescreen layer captures;
- analoginputlog;
- RA-policytests;
- ARM9-telemetrie;
- regressiematrix;
- releasechecklist;
- eindrapport.

Redacteer accounttokens, wachtwoorden, e-mailadressen, serienummers en lokale privépadinformatie.

# Gitgedrag

- Commit per groene mijlpaal.
- Gebruik geen `git add -A` voordat secret/ROM-scan is uitgevoerd.
- Controleer iedere staged file.
- Commit geen buildoutputs, ROMs, saves met persoonsgegevens, tokens of afgeleide Nintendo-assets.
- Push niet en maak geen PR tenzij de gebruiker dat later expliciet vraagt.
- Wijzig upstream-submodules alleen bewust en documenteer de pin.
- Houd upstream-sync later mogelijk; vermijd willekeurige wholesale rewrites.

# Blockers

Stel geen vraag wanneer de code, ROM-header, ADB, tests of dit dossier het antwoord kunnen geven. Kies de veiligste reproduceerbare optie en documenteer de beslissing.

Bij een echte blocker:

1. reproduceer hem;
2. verzamel foutbewijs;
3. zoek de kleinste root cause;
4. probeer begrensde alternatieven;
5. rond alle niet-geblokkeerde mijlpalen af;
6. maak geen valse voltooiingsclaim;
7. lever een concrete hervattingsinstructie.

# Vereiste eindlevering

Lever aan het eind:

1. alle bronwijzigingen;
2. commits per mijlpaal;
3. werkende debug-APK en, indien signing beschikbaar is, release-APK;
4. APK SHA-256;
5. installatiepad en package-id;
6. samenvatting van het actieve SM64DS-profiel;
7. exacte True Widescreen-architectuur en bewijs;
8. analog- en camerabewijs;
9. RA Off/Casual/Hardcore-policybewijs;
10. ARM9-overclockstatus en meetresultaten;
11. regressieresultaten;
12. bekende beperkingen;
13. expliciete status van 60fps als volgende doel;
14. `operations/FINAL_REPORT_TEMPLATE.md` volledig ingevuld.

Ga nu aan de slag. Begin met M0, inventariseer de map en de Thor, bootstrap exact de vastgepinde basis en bouw de ongewijzigde baseline voordat je productcode verandert.
