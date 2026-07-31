# ADB-testharness

## Device selecteren

```bash
adb devices -l
```

Wanneer meerdere devices:

```bash
export ANDROID_SERIAL=<serial>
```

Schrijf serial niet ongeredigeerd in publiek evidence.

## Inventaris

```bash
adb shell getprop ro.product.manufacturer
adb shell getprop ro.product.model
adb shell getprop ro.build.version.release
adb shell getprop ro.build.fingerprint
adb shell dumpsys display
adb shell dumpsys window displays
adb shell wm size
adb shell wm density
adb shell input devices
```

## Install

```bash
adb install -r -d path/to/app-gitHub-prod-debug.apk
```

Voor clean-state test:

```bash
adb shell pm clear io.github.joeblack2k.thords.dev
```

Gebruik alleen na savebackup/expliciete testplanning.

## Launch

```bash
adb shell monkey \
  -p io.github.joeblack2k.thords.dev \
  -c android.intent.category.LAUNCHER 1
```

Of resolved activity:

```bash
adb shell cmd package resolve-activity --brief \
  io.github.joeblack2k.thords.dev
```

## Logcat

```bash
adb logcat -c
adb logcat -v threadtime \
  ThorDS:D MelonDS:D AndroidRuntime:E *:S
```

Voor complete capture mag bredere filter, maar scrub later.

## Screenshots

Android `screencap` kan alleen het primaire framebuffer opleveren afhankelijk van firmware. Voeg daarom app-level capture per surface toe.

```bash
adb exec-out screencap -p > primary.png
```

App debugcommands moeten bieden:

```text
capture main surface
capture secondary surface
capture Vulkan layer <name>
dump session plan
dump scene stats
dump input stats
```

Gebruik broadcast/intent uitsluitend debugbuild met signature/debug protection.

## Input

```bash
adb shell getevent -lt
```

Voor ingebouwde controller kunnen MotionEvents beter app-side gelogd worden.

## Lifecycle

```bash
adb shell input keyevent KEYCODE_HOME
adb shell input keyevent KEYCODE_SLEEP
adb shell input keyevent KEYCODE_WAKEUP
```

Gebruik passende unlock zonder credentials te loggen.

## Performance

```bash
adb shell dumpsys gfxinfo <package> reset
# test
adb shell dumpsys gfxinfo <package>
adb shell dumpsys meminfo <package>
adb shell top -H -p <pid>
```

Combineer met interne frame telemetry.

## Crash/ANR

```bash
adb shell dumpsys activity exit-info <package>
adb bugreport <private-output.zip>
```

Bugreport blijft private en wordt gescrubd.

## Script exitcodes

Iedere harnesscommand:

- nonzero bij failure;
- timeout;
- package check;
- evidence path;
- redaction;
- no silent retry loop.
