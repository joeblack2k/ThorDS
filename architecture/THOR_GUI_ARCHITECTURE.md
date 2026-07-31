# Thor-first GUI-architectuur

## Doel

Op de Thor moet de app voelen als een dual-screenconsole, niet als een telefoonemulator die toevallig een tweede display heeft.

## Appstart

### Top panel

- ThorDS branding;
- ROMbibliotheek;
- geselecteerde gamecover/banner uit lokale ROMmetadata;
- profielbadge;
- laatste gespeeld;
- RA-progressie indien ingelogd.

### Bottom touchscreen

Voor geselecteerde game:

- profielkeuze;
- enhancementtoggles;
- RA mode;
- controllerstatus;
- rendererstatus;
- grote `Spelen`-knop;
- compatibility waarschuwingen.

## SM64DS card

```text
Super Mario 64 DS
Europe • exact match

Profile: Thor Enhanced

[x] Analog controls
[x] True Widescreen
[ ] ARM9 overclock — Experimental
[ ] 60 FPS — Not yet validated

RetroAchievements: Casual
Hardcore requires Original

[ PLAY ]
```

## Gameplay

### Top

- uitsluitend gameoutput;
- subtiele RA-popup;
- geen permanente emulatorchrome;
- optional FPS/debug only developer.

### Bottom

- normaal: volledige DS-bottomoutput;
- touch exact;
- geen permanente ThorDS-navbar.

## Pauze

Fysieke pause/back:

- game pauzeert;
- top dimt/stopt stabiel;
- bottom toont ThorDS pauzemenu.

Opties:

- Resume;
- Achievements;
- Save state/load state waar policy toestaat;
- Enhancements;
- Controller;
- Restart;
- Exit.

Een enhancement die relaunch vereist toont dat en past pas na bevestiging toe.

## Settingshiërarchie

1. Game card quick toggles.
2. Game profile details.
3. General Thor defaults.
4. Advanced emulator settings.
5. Developer diagnostics.

Verberg complexiteit niet volledig, maar plaats die niet vóór Play.

## Safe mode

In launcher:

```text
Start in Safe 4:3 Mode
```

Deze forceert:

- Original;
- Vulkan/renderer safe default;
- no AR codes;
- no OC;
- reset display layout;
- preserves save.

Ook toegankelijk via een gedocumenteerde fysieke knop tijdens launch.

## Niet-Thor

- behoud normale single-screen/library UI;
- enhancements kunnen beschikbaar zijn;
- Thor-specific defaults alleen bij exacte device/capabilitymatch;
- geen crash wanneer tweede display ontbreekt.

## Accessibility

- touch targets minimaal 48dp;
- duidelijke modekleur plus tekst, niet kleur alleen;
- screen reader labels;
- focusvolgorde;
- geen snel knipperende scene classifier;
- foutteksten met herstelactie.

## State management

Gebruik bestaande ViewModels/flows. Nieuwe state:

```text
SelectedProfileUiState
EnhancementToggleUiState
EffectiveSessionPolicyUiState
ThorDisplayStatusUiState
```

UI geeft resolved state weer, niet losse toggles die runtime onmogelijk kan uitvoeren.
