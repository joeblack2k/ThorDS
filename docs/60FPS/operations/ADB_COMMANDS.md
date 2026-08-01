# ADB commands

```bash
adb devices -l
adb install -r app/build/outputs/apk/gitHubProd/debug/app-gitHub-prod-debug.apk
adb shell am force-stop io.github.joeblack2k.thords.dev
adb shell monkey -p io.github.joeblack2k.thords.dev 1
```

Crash/ANR:

```bash
adb logcat -d -v brief   | rg -i 'FATAL EXCEPTION|ANR in|Process: io\.github\.joeblack2k\.thords'
```

Use explicit debug receiver components for background broadcasts.

Never place private ROM paths in committed command transcripts.
