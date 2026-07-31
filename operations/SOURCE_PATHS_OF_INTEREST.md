# Waarschijnlijke bronpaden van belang

Deze lijst is een startpunt voor Luna. Verifieer ieder pad tegen tag `0.7.0.rc5`; oude paden uit eerdere releases zijn niet autoritatief.

## Android/productlaag

```text
app/build.gradle.kts
buildSrc/src/main/kotlin/AppConfig.kt
app/src/main/AndroidManifest.xml
app/src/main/java/me/magnum/melonds/
app/src/main/res/
```

Belangrijke onderwerpen:

```text
ui/emulator/EmulatorActivity.kt
ui/emulator/EmulatorViewModel.kt
ui/emulator/render/ExternalPresentation.kt
ui/emulator/model/VulkanPresentationConfig.kt
ui/emulator/input/InputProcessor.kt
impl/emulator/AndroidEmulatorManager.kt
impl/emulator/EmulatorSession.kt
impl/layout/SecondaryDisplaySelector.kt
impl/layout/DeviceLayoutDisplayMapper.kt
impl/layout/devicemapper/AynThorLayoutDisplayMapper.kt
domain/model/rom/config/RomGbaSlotConfig.kt
domain/model/Slot2AnalogMapping.kt
utils/RomProcessor.kt
```

## Native Android-frontend

```text
app/src/main/cpp/MelonDS.h
app/src/main/cpp/MelonDS.cpp
app/src/main/cpp/MelonDSAndroidJNI.cpp
app/src/main/cpp/MelonInstance.h
app/src/main/cpp/MelonInstance.cpp
app/src/main/cpp/Configuration.h
app/src/main/cpp/RomGbaSlotConfig.h
```

## Vulkan-presentatie

```text
app/src/main/cpp/renderer/VulkanCompositorShader.comp
app/src/main/cpp/renderer/VulkanSurfacePresenter.vert
app/src/main/cpp/renderer/VulkanSurfacePresenter.frag
app/src/main/cpp/renderer/VulkanOutput.*
app/src/main/cpp/renderer/VulkanSurfacePresenter.*
app/src/main/cpp/renderer/Vulkan*ShaderData.h
scripts/regenerate_vulkan_spirv.sh
```

Wijzig GLSL en de gegenereerde SPIR-V-headers nooit los van elkaar. Gebruik de bestaande regeneratie- en checktasks.

## Core-submodule

```text
melonDS-android-lib/src/NDS.h
melonDS-android-lib/src/NDS.cpp
melonDS-android-lib/src/ARM.*
melonDS-android-lib/src/ARMJIT*
melonDS-android-lib/src/GBACart.*
melonDS-android-lib/src/AREngine.*
melonDS-android-lib/src/GPU*
melonDS-android-lib/src/SPU*
```

De exacte submodulecommit uit rc5 blijft de autoriteit.

## RA

```text
app/src/main/cpp/retroachievements/
app/src/main/cpp/rcheevos/
app/src/main/java/me/magnum/melonds/common/retroachievements/
app/src/main/java/me/magnum/melonds/impl/retroachievements/
app/src/main/java/me/magnum/melonds/domain/model/retroachievements/
```

## Externe researchcheckouts

Aanbevolen gitignored locaties:

```text
tools/research/sm64ds-decomp/
tools/research/am64ds/
tools/research/phantom-hourglass-dpad/
```

Geen ROM, geëxtraheerde Nintendo-assets of proprietary toolchainbestand wordt gecommit.
