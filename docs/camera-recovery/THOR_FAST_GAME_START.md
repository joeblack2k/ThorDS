# AYN Thor Fast Game Start

Use this runbook whenever ThorDS must be restarted for a hardware test. It
preserves app data, ROM configuration, saves, and savestates.

## Rules

- Run only one ThorDS emulator process.
- Clear the old Android task before launching; force-stop alone can restore an
  old `EmulatorActivity` over the new session.
- Use display-targeted Android input, never raw `sendevent`.
- Use a 250 ms same-point swipe for DS touchscreen presses. An instantaneous
  injected tap can be shorter than the game reliably samples.
- Do not clear app data.

## Start One Clean Session

```bash
PKG=io.github.joeblack2k.thords.dev
ROM_LIST=me.magnum.melonds.ui.romlist.RomListActivity

adb shell am force-stop "$PKG"
adb shell am kill "$PKG"

if adb shell pidof "$PKG" >/dev/null 2>&1; then
  echo "Refusing start: old ThorDS process is still running" >&2
  exit 1
fi

adb shell am start -S -W -f 0x10008000 -n "$PKG/$ROM_LIST"
```

The `0x10008000` flags are `NEW_TASK | CLEAR_TASK`. A successful start reports
`LaunchState: COLD` and `Activity: ...RomListActivity`.

## Open SM64DS

Open the SM64DS ROM card on the upper/default display:

```bash
adb shell input touchscreen -d 0 tap 160 580
```

Wait until the SM64DS menu containing `VS`, `ADVENTURE`, and `REC ROOM` is
visible on the lower screen.

## Enter Adventure And File A

First confirm the current lower-display ID:

```bash
adb shell dumpsys input | rg 'Viewport EXTERNAL: displayId='
```

On the tested AYN Thor the lower display is currently display 4. If the reported
ID changes, substitute the new ID below.

Press `ADVENTURE`:

```bash
LOWER_DISPLAY_ID=4
adb shell input touchscreen -d "$LOWER_DISPLAY_ID" \
  swipe 620 890 620 890 250
```

This press maps to DS coordinate `(128,168)`. Wait for the file-select screen,
then press the existing `FILE A`:

```bash
adb shell input touchscreen -d "$LOWER_DISPLAY_ID" \
  swipe 215 415 215 415 250
```

Wait about six seconds. The upper screen should show Yoshi in Castle Garden.

## Verification

```bash
adb shell pidof "$PKG"
adb shell dumpsys activity activities |
  rg 'topResumedActivity|io.github.joeblack2k.thords.dev'
```

Acceptance:

- exactly one package PID;
- exactly one resumed `EmulatorActivity`;
- upper screen shows live Castle Garden gameplay;
- lower screen belongs to the same session;
- no stale white or black emulator surface overlays the game.

If entry fails, capture both displays and inspect the current activity before
trying another input. Do not repeat random coordinates, load an unrelated
savestate, clear app data, or start a second emulator.

## Proven Sequence

Verified on the physical AYN Thor on 2026-08-02:

1. Clean task start reached the ROM list.
2. SM64DS card opened the normal title menu.
3. The 250 ms `ADVENTURE` press reached file select.
4. The 250 ms `FILE A` press loaded Yoshi into Castle Garden.
