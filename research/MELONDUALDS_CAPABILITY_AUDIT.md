# Audit van bestaande MelonDualDS-capabilities

Dit document voorkomt dubbel werk.

## Al aanwezig in rc5

### Androidproductlaag

- Gradle flavors voor GitHub/Play Store en prod/nightly.
- SDK 36, NDK 28 en Java 21.
- ViewBinding en Compose.
- ROMbibliotheek en per-ROM-config.
- savebestanden en save states;
- rewind;
- controllerconfiguratie;
- layouteditor;
- custom backgrounds;
- updater;
- Android lifecycle;
- Oboe-audio;
- Adreno/Vulkanondersteuning.

### Dual screen

- `DisplayManager.DISPLAY_CATEGORY_PRESENTATION`;
- `ExternalPresentation`;
- onafhankelijke renderer/surface per display;
- top- en bottom-screenrectangles per layout;
- screen swap;
- touchmapping;
- display listener;
- lifecycle recovery;
- specifieke `AynThorLayoutDisplayMapper`.

### Input

- digitale DS-knoppen;
- fysieke controlleraxes;
- GBA-slotconfig;
- `AnalogInput` als Slot-2-device;
- `Slot2AnalogMapping`;
- deadzone/inversion/devicefilter;
- JNI-functie voor `setSlot2AnalogInput`;
- controllerprofielen.

### Patches/cheats

- handmatige cheatinput;
- cheatdatabase;
- Kotlinmodellen en Room-opslag;
- JNI-parser voor Action Replay-woorden;
- native `ARCode`;
- runtimevervanging van de codelijst.

### Rendering

- software;
- OpenGL;
- Compute/Adrenohistorie;
- Vulkan in de MelonDualDS-lijn;
- interne resolutiescaling;
- filters;
- RetroArch shaderpresets;
- compositor met packed 2D/control-data;
- afzonderlijke live/previous/capture 3D-bronnen;
- meerdere surfacepresenters.

### RetroAchievements

- accountlogin/tokenopslag;
- gamehash;
- achievements;
- measured progress;
- rich presence;
- leaderboards;
- offline/pending submissionlogica;
- save-state runtimeprogress;
- Casual/Hardcore-sessiepolicy;
- UI-events en popups.

## Nog te bouwen

### Productisering

- ThorDS branding en applicationId;
- stabiele eigen updaterpolicy;
- first-run Thorervaring;
- eenvoudiger gameprofiel-UX;
- profilecatalogus en provenance.

### Enhancement engine

- gecureerde patches los van user cheats;
- exact ROMvariantmatching;
- dependency-/conflictresolver;
- BPS/IPS-cachepipeline;
- capabilitygates;
- actieve enhancementstatus;
- testbare declaratieve schema’s.

### SM64DS

- automatisch Europees profiel;
- ingebouwde Europese analogcode;
- radiale analogcurve;
- right-stick camera override;
- Europese 16:9-patchderivatie;
- True Widescreen compositor mode;
- scènedetectie/fallback;
- featuretests.

### ARM9

- profielconfig;
- native ratio;
- schedulerinstrumentatie;
- safe overclockstrategie;
- save-statecompatibiliteit;
- UI en performancebewijs.

## Belangrijk architectuurvoordeel

Rc5's compositor heeft meer informatie dan een normale emulator die slechts een compleet 256×192-frame presenteert. De bestaande code onderscheidt onder meer:

- packed 2D-kleur;
- structured 3D-slot;
- 2D boven 3D;
- 2D-only;
- capture-backed 3D;
- vorige 3D-framebronnen;
- controlmetadata per pixel/lijn.

Daardoor kan de presentatielaag twee verschillende X-transformaties toepassen:

```text
world UV → volledige 16:9
UI UV    → gecentreerde 4:3-safe-area
```

Dat is de kern van True Widescreen.

## Auditregel

Luna inspecteert vóór nieuwe code steeds of rc5 al een equivalente abstraction heeft. Nieuwe functies horen aan te sluiten op de bestaande repository-, ViewModel-, configuration-, renderer- en eventarchitectuur.
