# File-by-file plan

Luna must inspect current paths first. Expected files include:

## Superproject Kotlin

```text
app/src/main/java/me/magnum/melonds/domain/model/ControllerConfiguration.kt
app/src/main/java/me/magnum/melonds/domain/model/enhancement/CameraDpadHysteresis.kt
app/src/main/java/me/magnum/melonds/domain/model/enhancement/ProfileLaunchPlanner.kt
app/src/main/java/me/magnum/melonds/domain/model/enhancement/EnhancementProfile.kt
app/src/main/java/me/magnum/melonds/ui/emulator/input/InputProcessor.kt
app/src/main/java/me/magnum/melonds/MelonEmulator.kt
app/src/main/java/me/magnum/melonds/ui/emulator/EmulatorViewModel.kt
```

Expected new domain files:

```text
SmoothCameraMapping.kt
SmoothCameraProfileConfiguration.kt
Slot2CameraState.kt
```

## Superproject native/JNI

```text
app/src/main/cpp/MelonDSAndroidJNI.cpp
app/src/main/cpp/MelonDSAndroidInterface.*
```

## Core fork

```text
src/GBACart.h
src/GBACart.cpp
```

Plus the actual interface file that currently implements `setSlot2AnalogInput`.

## Profile

```text
app/src/main/assets/enhancement-profiles.json
```

## Patch tooling

Expected new files:

```text
tools/thords/camera/README.md
tools/thords/camera/sm64ds_eu_smooth_camera.s
tools/thords/camera/build_patch.py
tools/thords/camera/generate_ar.py
tools/thords/camera/verify_patch.py
tools/thords/camera/patch_manifest.json
```

## Tests

Expected additions:

```text
SmoothCameraMappingTest.kt
InputProcessorSmoothCameraInstrumentedTest.kt
native CartAnalog camera protocol tests
patch generator tests
profile resolver/runtime composition tests
```

## Documentation/evidence

```text
docs/project/STATUS.md
docs/project/WORKLOG.md
docs/evidence/camera/
```
