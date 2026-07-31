# File-by-file implementatieplan

De genoemde paden zijn gebaseerd op rc5 en moeten vóór edit worden geverifieerd.

## Productmetadata

Waarschijnlijk:

```text
buildSrc/src/main/kotlin/AppConfig.kt
app/build.gradle.kts
app/src/main/res/values/strings.xml
app/src/main/AndroidManifest.xml
```

Wijzig:

- applicationId;
- appnaam;
- version metadata;
- updaterpolicy;
- About source info.

## Profile engine — nieuwe packages

Voorgesteld:

```text
app/src/main/java/.../domain/model/enhancement/
app/src/main/java/.../domain/repositories/EnhancementProfileRepository.kt
app/src/main/java/.../domain/services/EnhancementProfileResolver.kt
app/src/main/java/.../impl/enhancement/
app/src/main/assets/enhancement-profiles/
```

Klassen:

- `EnhancementProfile`;
- `RomIdentityConstraint`;
- `EnhancementDefinition`;
- `ProfileCapability`;
- `ResolvedSessionPlan`;
- `EnhancementProfileCatalog`;
- `EmbeddedEnhancementProfileRepository`;
- `EnhancementPolicyResolver`.

## ROM identity

Bestaand:

```text
app/src/main/java/me/magnum/melonds/utils/RomProcessor.kt
app/src/main/java/.../domain/model/RomMetadata.kt
```

Uitbreiden zonder RA-hashlogica te breken:

- full SHA-256 service;
- gamecode/revision;
- stable profile key;
- identity cache.

## Patchengine

Nieuwe:

```text
.../enhancement/patch/ActionReplayPatchResolver.kt
.../enhancement/patch/BpsPatchApplier.kt
.../enhancement/patch/IpsPatchApplier.kt
.../enhancement/patch/PatchCacheManager.kt
```

Integratie:

```text
AndroidEmulatorManager.loadRom
MelonEmulator.setupCheats
```

Curated/user codes apart houden tot vlak vóór JNI.

## Analog

Bestaand:

```text
domain/model/Slot2AnalogMapping.kt
ui/emulator/input/InputProcessor.kt
domain/model/rom/config/RomGbaSlotConfig.kt
MelonEmulator.kt
native Slot-2 bridge/core
```

Wijzig:

- radial processor als testbare pure class;
- profile controller overlay;
- camera hysteresis;
- diagnostics.

## Display/Thor

Bestaand:

```text
impl/layout/devicemapper/AynThorLayoutDisplayMapper.kt
impl/layout/SecondaryDisplaySelector.kt
ui/emulator/EmulatorActivity.kt
ui/emulator/render/ExternalPresentation.kt
```

Toevoegen:

- physical role classifier;
- diagnostics;
- reset override;
- screen-specific defaults.

## True Widescreen

Bestaand:

```text
app/src/main/cpp/renderer/VulkanCompositorShader.comp
app/src/main/cpp/renderer/VulkanSurfacePresenter.frag
app/src/main/cpp/MelonInstance.cpp
app/src/main/java/.../VulkanPresentationConfig.kt
DSRenderer / Vulkan presentation bridge
```

Toevoegen:

- enum/mode;
- safe rect;
- world/UI dual UV;
- scene stats/classifier;
- debug layers;
- tests;
- SPIR-V regenerate.

## RA

Bestaand:

```text
impl/emulator/EmulatorSession.kt
RetroAchievementsRepository
EmulatorViewModel
RA UI components
rcheevos-api
```

Wijzig:

- session integrity input;
- effective mode conflict UI;
- own User-Agent;
- enhancement summary;
- policy tests.

## ARM9-OC

Android:

```text
domain/model/EmulatorConfiguration.kt
Renderer/Emulator configuration parcelables
MelonDSAndroidConfiguration.cpp
MelonEmulator.kt
AndroidEmulatorManager.kt
```

Core/submodule:

```text
NDS.h
NDS.cpp
ARM/JIT timing paths as selected by ADR
Savestate or frontend state metadata
```

Toevoegen:

- config;
- JNI;
- core API;
- telemetry;
- capability probe;
- tests.

## Thor-first GUI

Bestaand:

```text
ROM/library screens
rom details
EmulatorActivity
pause menu
settings fragments
Compose theme
```

Voeg profile sections toe zonder algemene settings te verwijderen.

## Tests

Nieuwe testpackages parallel aan features. Native shader/golden harness kan onder:

```text
tools/thords/
app/src/test/
app/src/androidTest/
app/src/main/cpp/tests/ or existing native test structure
```

Geen echte ROMfixtures.
