# Shell — ADB timing runner

Save as `tools/thords/60fps/run_thor_timing_window.sh`.

```bash
#!/usr/bin/env bash
set -euo pipefail

PACKAGE="${PACKAGE:-io.github.joeblack2k.thords.dev}"
DURATION="${DURATION:-15}"
LABEL="${LABEL:?Set LABEL}"
OUT="${OUT:?Set OUT}"

mkdir -p "$OUT"
adb get-state >/dev/null

adb logcat -c

# Launch/load the private checkpoint using the repository's current debug
# receiver. Do not hardcode a ROM path here.
# Example:
# adb shell am broadcast -n "$PACKAGE/...DebugCommandReceiver" \
#   -a "$PACKAGE.LOAD_STATE" ...

sleep 2
adb logcat -v epoch \
  | stdbuf -oL grep -E \
    'SM64DS semantic window|SM64DS game-loop window|ARM9Overclock' \
  > "$OUT/${LABEL}.log" &
LOGGER_PID=$!

cleanup() {
  kill "$LOGGER_PID" 2>/dev/null || true
  wait "$LOGGER_PID" 2>/dev/null || true
}
trap cleanup EXIT

sleep "$DURATION"
cleanup
trap - EXIT

python3 tools/thords/60fps/analyze_semantic_telemetry.py \
  "$OUT/${LABEL}.log" \
  --output "$OUT/${LABEL}.summary.json"

adb logcat -d -v brief \
  | grep -E 'FATAL EXCEPTION|ANR in|Process: io.github.joeblack2k.thords' \
  > "$OUT/${LABEL}.failures.txt" || true

if [[ -s "$OUT/${LABEL}.failures.txt" ]]; then
  echo "ThorDS crash/ANR evidence found" >&2
  exit 1
fi
```

The actual launch/checkpoint command must use current debug APIs and must keep
private paths out of public logs.
