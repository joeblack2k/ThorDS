# File-by-file plan

Inspect live paths first.

## Core

```text
melonDS-android-lib/src/NDS.h
melonDS-android-lib/src/NDS.cpp
melonDS-android-lib/src/ARM.cpp
melonDS-android-lib/src/ARMJIT.cpp
```

Expected new:

```text
melonDS-android-lib/src/Sm64dsSemanticMonitor.h
melonDS-android-lib/src/Sm64dsSemanticMonitor.cpp
```

## Native bridge

```text
app/src/main/cpp/MelonInstance.h
app/src/main/cpp/MelonInstance.cpp
app/src/main/cpp/MelonDS.h
app/src/main/cpp/MelonDS.cpp
app/src/main/cpp/MelonDSAndroidJNI.cpp
```

## Kotlin/profile

```text
app/src/main/java/me/magnum/melonds/MelonEmulator.kt
app/src/main/java/.../enhancement/EnhancementProfile.kt
app/src/main/java/.../enhancement/ProfileLaunchPlanner.kt
app/src/main/java/.../enhancement/SessionPlanBuilder.kt
app/src/main/assets/enhancement-profiles.json
ROM details/settings UI
pause/session status
```

## Tools

```text
tools/thords/60fps/
```

## Evidence

```text
docs/evidence/m13/
docs/project/
docs/research/
```
