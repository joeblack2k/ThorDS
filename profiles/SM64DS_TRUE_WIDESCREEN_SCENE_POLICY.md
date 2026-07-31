# SM64DS True Widescreen — scènepolicy

## Default classifierbeleid

| Scene class | Presentation |
|---|---|
| 3D gameplay + structured HUD | full 16:9 world + 4:3 UI |
| 3D gameplay no HUD | full 16:9 world |
| 2D title/menu | centered 4:3 |
| Star select with reliable 3D | measured policy |
| Cutscene with safe layers | full world + safe UI |
| Capture/transition ambiguous | centered 4:3 |
| Bottom screen | fit 4:3 |

## Verplichte scènes

### Boot/title

- Nintendo/logo;
- title face;
- file select;
- options.

Verwachting: 4:3 tenzij een afzonderlijk bewezen 3D-titlebeleid esthetisch correct is.

### Castle

- outside grounds;
- main lobby;
- doors/signs;
- character room;
- pause menu.

### Bob-omb Battlefield

- start;
- Chain Chomp;
- mountain;
- cannon;
- King Bob-omb;
- star collection;
- course exit.

### Andere representatieve typen

- Jolly Roger Bay water;
- Cool Cool Mountain slide;
- Big Boo interior;
- Hazy Maze fog;
- Lethal Lava effects;
- Shifting Sand pyramid;
- Tiny-Huge scale;
- Tick Tock moving geometry;
- Rainbow Ride;
- Bowser;
- cap/flying;
- minigames.

## Hysterese

Scene class wijziging:

- minimaal 3 consistente frames voor safe world;
- direct naar fallback bij invalid source;
- hold last safe world maximaal beperkte transitionframes;
- logging van reason/confidence.

## UI-safe geometry

Bij world mode blijft UI op originele 4:3 posities. Dat betekent visueel dat HUD niet naar uiterste 16:9-hoeken schuift. Een latere optionele game-side HUD layout kan elementen naar buiten verplaatsen, maar v0.1 prioriteert onvervormde assets.

## 2D underlay

Per scene capture bepaalt Luna:

- stretch;
- edge extend;
- center;
- fallback.

Onbekend = fallback.

## User override

Normale gebruiker krijgt:

```text
True Widescreen
Native 4:3
```

Developer mag scene debug/fallback force gebruiken. Geen ingewikkelde per-scène UI in v0.1.

## Evidence naming

```text
<scene>-43-reference-top.png
<scene>-truews-top.png
<scene>-layers-3d.png
<scene>-layers-ui.png
<scene>-classification.json
```

Lokale gamebeeldcaptures blijven private evidence tenzij gebruiker publicatie kiest.
