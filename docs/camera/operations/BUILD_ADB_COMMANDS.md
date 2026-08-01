# Build and ADB command sheet

Use current repository commands when they differ.

## Host

```bash
git status --short --branch
git submodule status --recursive
git diff --check

CARGO=/tmp/thords-cargo ./gradlew --no-daemon   :app:testGitHubProdReleaseUnitTest   :app:assembleGitHubProdDebug
```

## Install

```bash
APK=app/build/outputs/apk/gitHubProd/debug/app-gitHub-prod-debug.apk
sha256sum "$APK"
adb install -r "$APK"
adb shell am force-stop io.github.joeblack2k.thords.dev
adb shell monkey -p io.github.joeblack2k.thords.dev 1
```

## Crash/ANR

```bash
adb logcat -d -v brief | rg -i   'FATAL EXCEPTION|ANR in|Process: io\.github\.joeblack2k\.thords'
```

## Device

```bash
adb devices -l
adb shell getprop ro.product.manufacturer
adb shell getprop ro.product.model
adb shell dumpsys input
```

Do not store the serial in public evidence.
