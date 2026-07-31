# Selectie van de basisrepository

## Kandidaten

### `rafaelvcaetano/melonDS-android`

Sterke punten:

- actuele algemene Androidfrontend;
- officiële RA-ondersteunde melonDS Android-lijn;
- secondary-display- en Thorcode;
- cheatengine;
- moderne Kotlin/NDK-architectuur.

Nadeel voor dit project:

- de specifieke Slot-2-analog- en uitgebreide Vulkanwerkzaamheden die voor de Redditopstelling zijn gebruikt, zitten in de MelonDualDS-fork.

### `SapphireRhodonite/melonDS-android`

Dit is de MelonDualDS-lijn. Tag `0.7.0.rc5` bevat:

- AYN Thor-specifieke displaymapping;
- `RomGbaSlotConfig.AnalogInput`;
- configureerbare Slot-2-analogmapping;
- controllerasdoorvoer naar native core;
- Vulkanrenderer en uitgebreide compositor;
- gestructureerde 2D/3D-laagdata;
- RetroArch shaderpipeline;
- huidige RetroAchievementsintegratie;
- recursieve native submodules;
- recente lifecycle-, updater- en rendererwijzigingen.

### `Liprax/DualMelon`

Deze fork richt zich vooral op twee afzonderlijke Androidtelefoons via netwerk. Dat is niet de juiste basis voor twee ingebouwde schermen in één Thor.

## Besluit

Gebruik exact:

```text
repository: SapphireRhodonite/melonDS-android
tag:        0.7.0.rc5
commit:     9b28076281545a1e08dccee0b3f925febb8933ac
```

## Waarom niet zomaar `main`

- De fork is actief en kan snel wijzigen.
- Renderer- en corewijzigingen zijn groot.
- Een vaste pin maakt visuele regressie, timing en ADB-bewijs reproduceerbaar.
- Luna mag later een upstream-syncbranch maken, maar niet tijdens de eerste productimplementatie ongericht migreren.

## Buildrelevante gegevens uit rc5

```text
compileSdk: 36
targetSdk:  36
minSdk:     24
NDK:        28.0.13004108
Java:       21
CMake:      3.22.1
version:    0.7.0.rc5
```

De GitHubvariant gebruikt AdrenoTools en rc5 bouwt tevens een vastgepinde librashaderdependency via Rust.

## Base acceptance

Voordat productcode verandert:

```bash
./gradlew :app:assembleGitHubProdDebug
```

Daarna:

- APK SHA-256 vastleggen;
- installeren op Thor;
- één ongewijzigde DS-ROM starten;
- twee schermen, audio en touch bewijzen;
- baseline logcat bewaren.

## Forkstrategie

- Voeg `upstream` toe voor SapphireRhodonite.
- Voeg later optioneel `parent-upstream` toe voor `rafaelvcaetano/melonDS-android`.
- Houd onze productcode in duidelijk afgebakende packages/modules.
- Verander de melonDS core alleen waar ARM9-OC dat noodzakelijk maakt.
- Patch True Widescreen bij voorkeur in de bestaande Android Vulkan-presentatielaag, niet als brede corefork.
