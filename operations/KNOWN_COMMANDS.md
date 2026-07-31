# Bekende commando’s

Dit is een startlijst. Luna past task-/packagepaden aan op de echte branch.

## Git

```bash
git rev-parse HEAD
git describe --tags --exact-match
git status --short
git submodule status --recursive
git diff --check
```

## Build

```bash
./gradlew :app:assembleGitHubProdDebug
./gradlew test
./gradlew lint
./gradlew checkVulkanSpirv
```

## APK

```bash
find app/build/outputs -name '*.apk' -print
sha256sum <apk>
apksigner verify --verbose <apk>
```

## ADB

```bash
adb devices -l
adb install -r -d <apk>
adb shell dumpsys display
adb shell dumpsys window displays
adb logcat -c
adb logcat -v threadtime
```

## ROM safety

```bash
git check-ignore -v <rom>
git ls-files | grep -Ei '\.(nds|srl)$'
find . -type f -size +64M -print
```

## Source research

```bash
git -C tools/research/sm64ds-decomp rev-parse HEAD
grep -R "0x1555" tools/research/sm64ds-decomp/src
```

## Evidence

```bash
find docs/evidence -type f -print | sort
```
