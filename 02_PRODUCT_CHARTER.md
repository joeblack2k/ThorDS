# Product charter

## Productnaam

Werknaam: **ThorDS Enhanced**

## Productbelofte

ThorDS Enhanced maakt Nintendo DS-games op de AYN Thor direct bruikbaar als dual-screenhandheldervaring, zonder dat de gebruiker losse cheats, ROM-patchers, schermlayouts en controllerworkarounds hoeft te combineren.

De emulator blijft algemeen bruikbaar. Voor geselecteerde games bevat hij echter **gecureerde enhancementprofielen** die:

- de exacte ROM herkennen;
- alleen passende patches activeren;
- de juiste displaylayout kiezen;
- controllerinput game-specifiek verbeteren;
- rendererfeatures configureren;
- RetroAchievements transparant behandelen;
- bekende risico’s en incompatibiliteiten tonen.

## Eerste productprofiel

`Super Mario 64 DS — Thor Enhanced`

Doelervaring:

```text
bovenpaneel
└── 16:9 3D-wereld op hoge interne resolutie
    └── HUD en tekst met correcte verhoudingen

onderpaneel
└── originele DS-kaart/touchscreen in 4:3

controller
├── linkerstick: echte analoge beweging
├── rechterstick: camera
└── fysieke knoppen: getest Thor-profiel

RetroAchievements
├── Uit
├── Casual + enhancements
└── Hardcore alleen Original
```

## Primaire gebruiker

Een AYN Thor-eigenaar die:

- eigen Nintendo DS-ROMs gebruikt;
- geen algemene emulatorconfiguratie wil uitzoeken;
- fysieke controls en beide schermen optimaal wil gebruiken;
- RetroAchievements optioneel wil;
- enhancements expliciet wil kunnen zien en aan-/uitzetten;
- altijd naar een veilige originele configuratie moet kunnen terugkeren.

## Productprincipes

1. **Eerst speelbaar, daarna experimenteel.** Analog, True Widescreen en
   60fps moeten samen stabiel gevalideerd worden; geen van deze features mag
   als compleet worden geclaimd zonder de bijbehorende gates.
2. **Geen verborgen patches.** De actieve profielonderdelen zijn altijd zichtbaar.
3. **Exacte ROM-match.** Geen patch wordt op “waarschijnlijk dezelfde” ROM toegepast.
4. **Bron-ROM blijft intact.**
5. **Thor is de hoofdtarget.** Generieke Androidondersteuning blijft bestaan, maar bepaalt niet de standaard-UX.
6. **True betekent true.** 3D-breedte zonder vervormde UI.
7. **Casual is een gebruikerskeuze.** Enhancements blokkeren niet automatisch RetroAchievements.
8. **Hardcore is een integriteitscontract.**
9. **Communitywerk wordt hergebruikt met herkomst.**
10. **Decompilaties zijn technische documentatie.** Ze helpen patches begrijpen en reproduceerbaar maken.
11. **Upstream blijft samenvoegbaar.**
12. **Bewijs boven aannames.**

## Scope van v0.1

In scope:

- exacte basisbootstrap;
- Thor autodetectie;
- enhancementprofielengine;
- SM64DS EU-identiteit;
- runtime AM64DS analog;
- rechterstickcamera;
- Vulkan True Widescreen;
- RA-toggle en Hardcore-gate;
- ARM9-OC-fundering;
- Thor-first GUI;
- ADB-acceptatie;
- documentatie en releasebuild.

Niet in scope als release-eis:

- stabiele 60fps;
- texturepacks;
- multiplayer;
- native SM64DS-port;
- nieuwe gameplaycontent;
- automatische download van ROMs;
- Nintendo-assets in de repository;
- Zelda-profielen volledig implementeren.

## Langetermijnrichting

Na SM64DS:

1. Phantom Hourglass met D-pad/QoL-profiel;
2. Spirit Tracks met D-pad/microfoon-QoL;
3. aanvullende DS-games met bestaande, controleerbare communitypatches;
4. een onderhoudbare profielcatalogus met onafhankelijke teststatus per game en ROMvariant.
