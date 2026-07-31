# Systeemarchitectuur

## Hoofdlagen

```text
┌─────────────────────────────────────────────────────────┐
│                    ThorDS Android UI                    │
│ launcher • game details • pause • settings • RA UI     │
├─────────────────────────────────────────────────────────┤
│                Enhancement Profile Engine               │
│ identity • resolver • patches • policy • provenance     │
├──────────────────────────┬──────────────────────────────┤
│ Input / Thor displays    │ Renderer / presentation      │
│ Slot-2 analog            │ Vulkan layer-aware WS        │
│ controller overrides     │ 4:3 fallback                 │
├──────────────────────────┴──────────────────────────────┤
│            Existing MelonDualDS Android bridge          │
│ Kotlin/ViewModel/JNI/audio/saves/lifecycle/RA            │
├─────────────────────────────────────────────────────────┤
│                     melonDS core                        │
│ ARM9/ARM7 • GPU • SPU • cart • AR engine • JIT          │
└─────────────────────────────────────────────────────────┘
```

## Dataflow bij launch

```text
user selects ROM
    ↓
ROM identity service
    ├── game code
    ├── revision
    ├── RA hash
    └── full SHA-256
    ↓
profile resolver
    ├── Original
    └── SM64DS Thor Enhanced
    ↓
capability/policy resolver
    ├── renderer
    ├── displays
    ├── analog
    ├── patches
    ├── ARM9 OC
    └── RA mode
    ↓
session plan (immutable)
    ↓
load original ROM
    ↓
set Slot-2 accessory
    ↓
load curated AR codes + permitted user cheats
    ↓
setup RA
    ↓
start emulation
```

## Immutable session plan

Maak vóór `startEmulation` één object:

```text
ResolvedSessionPlan
```

Inhoud:

- source ROM identity;
- active profile/version;
- enabled enhancements;
- resolved patch order;
- GBA Slot-2 device;
- controller overlay;
- display layout;
- renderer and widescreen mode;
- ARM9 ratio;
- RA requested/effective mode;
- disabled features with reasons;
- provenance summary.

Na start mag alleen een expliciet `runtimeMutable` subset wijzigen. Aspectpatch, Slot-2-device, OC-ratio en Hardcore vereisen relaunch/reset.

## Scheiding curated/user

```text
CuratedEnhancementCodes
UserCheatCodes
```

Ze worden apart opgeslagen en apart in de UI getoond. Alleen vlak vóór native setup worden ze gecombineerd in een geordende `EffectiveCodeList`.

## Renderer

Voor SM64DS True Widescreen:

```text
game aspect AR patch
        ↓
3D source compressed for 16:9
        ↓
Vulkan structured compositor
        ├── 3D world UV: full width
        ├── 2D UI UV: centered 4:3
        └── scene-safe fallback
        ↓
top physical panel
```

Bottom:

```text
bottom DS framebuffer
→ fit 4:3
→ lower physical panel
→ touch map
```

## RA-policy

RA is geen onderdeel van de patchresolver maar krijgt de resolved session als policyinput:

```text
requestedMode + sessionIntegrity
→ effectiveMode + explanation
```

Casual blijft toegestaan. Hardcore vereist een volledig originele integriteitsset.

## OC

ARM9-OC loopt via:

```text
profile/config
→ Kotlin configuration
→ Parcelable/JNI mapping
→ native EmulatorConfiguration
→ melonDS core timing strategy
→ telemetry events
```

De default 100%-route moet byte-/behavior-equivalent blijven aan upstream.

## Failure isolation

- Profile parsing error → Original fallback, geen patch.
- Unknown ROM → General mode.
- Missing Vulkan → Native 4:3, geen True Widescreenclaim.
- Display ambiguity → upstream safe layout + diagnostics.
- Patch precondition fail → enhancement uit, duidelijke fout.
- RA unavailable → game blijft offline speelbaar.
- OC unsafe/unavailable → 100%.
