# True Widescreen-architectuur

## Productcontract

`TRUE_WIDESCREEN_3D_UI_SAFE` betekent:

- game-side 16:9-perspectief;
- correcte side culling;
- full-width 3D;
- aspect-correcte 2D-HUD;
- 4:3 bottom screen;
- 4:3 menu/fallback.

## Twee noodzakelijke helften

### Gamepatch

Wijzigt relevante Fix12 aspectratio/camera-/clipperpaden:

```text
0x1555 (4:3) → 0x1C72 (16:9)
```

De Europese adressen worden afgeleid en conditioneel gepatcht.

### Presentatielaag

Verwijdert de anamorfische squeeze alleen voor 3D. 2D krijgt een andere transform.

Zonder gamepatch is full-width 3D alleen crop/stretch.
Zonder layer-aware presentatie blijft HUD uitgerekt.

## Renderpad

```text
melonDS GPU
├── high-resolution 3D image
├── packed 2D planes/control
├── previous 3D images
└── capture 3D
       ↓
TrueWidescreen compositor/presenter
├── world sampler
├── UI-safe sampler
├── underlay policy
├── scene classifier
└── fallback
       ↓
top display surface
```

## Geometry

```text
destination = actual top surface
worldRect = full destination
uiRect = largest centered 4:3 rect
```

Bij 1920×1080:

```text
worldRect = 0,0,1920,1080
uiRect    = 240,0,1440,1080
```

## Samplingrollen

### World

- 3D-current/previous/capture;
- full destination;
- anamorphic source X over full width.

### UI overlay

- structured 2D above;
- only inside `uiRect`;
- source 0..255/0..191;
- transparent outside.

### Underlay

- profile-/scene-dependent;
- stretch/edge extend/center;
- nooit blind voor menu’s.

### 2D-only

- complete originele compositor;
- fit binnen `uiRect`;
- sidebars/background.

## Scene classifier

Inputs:

- existing packed screen stats;
- display mode counts;
- structured slot pixels;
- 2D-only pixels;
- capture flags;
- current/previous 3D validity;
- screen swap;
- profile scene overrides.

Output:

```text
WORLD_3D_SAFE
WORLD_3D_CAPTURE_SAFE
MENU_2D
TRANSITION
AMBIGUOUS
```

Gebruik hysterese:

- mode pas wijzigen na N consistente frames;
- transition mag laatste veilige mode kort vasthouden;
- nooit frame-voor-frame flikkeren.

## Debug modes

Developer menu/ADB:

```text
final
world3d
packed-plane0
packed-plane1
ui-overlay
control
capture3d
scene-classification
safe-area
```

Captures moeten afzonderlijk per scherm kunnen worden opgeslagen.

## Game-side HUDuitzondering

Als een HUDelement in 3D-image zit:

- detecteer via layer capture;
- patch draw projection/position game-side;
- of classificeer scène 4:3;
- geen maskeringshack zonder bewijs.

## Renderer policy

V0.1:

```text
Vulkan + supported device → True Widescreen
OpenGL/software          → Native 4:3
optional developer       → Anamorphic diagnostic
```

UI noemt duidelijk waarom True Widescreen niet beschikbaar is.

## Recovery

Een verborgen/zichtbare safe-mode launch:

- houd een fysieke knopcombinatie of launcheroptie;
- disable profile patches;
- force Native 4:3;
- reset display mapping;
- behoud save.
