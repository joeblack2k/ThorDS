# File and component map

Extend existing code. Confirm paths in the live checkout before editing.

## Profile and policy

```text
app/src/main/assets/enhancement-profiles.json
app/src/main/java/me/magnum/melonds/domain/model/enhancement/
app/src/main/java/me/magnum/melonds/domain/model/retroachievements/RetroAchievementsPolicy.kt
app/src/main/java/me/magnum/melonds/ui/emulator/EmulatorViewModel.kt
app/src/main/java/me/magnum/melonds/impl/emulator/EmulatorSession.kt
```

## Analog/controller

```text
app/src/main/java/me/magnum/melonds/domain/model/Slot2AnalogMapping.kt
app/src/main/java/me/magnum/melonds/domain/model/ControllerConfiguration.kt
app/src/main/java/me/magnum/melonds/ui/emulator/input/InputProcessor.kt
app/src/debug/java/me/magnum/melonds/debug/DebugCommandReceiver.kt
```

## True Widescreen

```text
app/src/main/java/me/magnum/melonds/ui/emulator/model/VulkanPresentationConfig.kt
app/src/main/java/me/magnum/melonds/ui/emulator/render/VulkanFrameRenderCoordinator.kt
app/src/main/java/me/magnum/melonds/ui/emulator/EmulatorActivity.kt
app/src/main/cpp/MelonDSAndroidJNI.cpp
app/src/main/cpp/renderer/VulkanSurfacePresenter.{h,cpp,vert,frag}
app/src/main/cpp/renderer/VulkanCompositorShader.comp
app/src/main/cpp/renderer/VulkanOutput.{h,cpp}
```

## ARM9 runtime

Frontend/native bridge candidates:

```text
app/src/main/cpp/Configuration.h
app/src/main/cpp/EmulatorArgsBuilder.cpp
app/src/main/cpp/MelonDS.{h,cpp}
app/src/main/cpp/MelonInstance.{h,cpp}
app/src/main/cpp/MelonDSAndroidJNI.cpp
app/src/main/java/me/magnum/melonds/MelonEmulator.kt
app/src/main/java/me/magnum/melonds/domain/model/EmulatorConfiguration.kt
```

Core candidates in the `melonDS-android-lib` submodule:

```text
src/NDS.{h,cpp}
src/ARM.{h,cpp}
src/ARMJIT*
src/DMA.cpp
src/GPU3D.cpp
src/Savestate*
```

Do not directly expose mutable core fields to Kotlin. Add a small explicit API.

## UI

Reuse the current ROM details, settings, ViewModel and pause-menu flows. Likely paths:

```text
app/src/main/java/me/magnum/melonds/ui/romdetails/
app/src/main/java/me/magnum/melonds/ui/romlist/
app/src/main/java/me/magnum/melonds/ui/emulator/
app/src/main/res/values/strings.xml
app/src/main/res/xml/
```

Do not create a second independent launcher state store.

## Research and tooling

```text
tools/research/sm64ds-decomp/
tools/thords/
docs/project/adr/
docs/evidence/
```

Tools and extracted binaries remain gitignored unless they are original distributable scripts or redacted metadata.
