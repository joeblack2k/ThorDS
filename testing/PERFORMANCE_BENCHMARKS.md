# Performancebenchmarks

## Scenarios

### P-1 Baseline 4:3

- rc5;
- Vulkan;
- chosen internal scale;
- SM64DS BOB.

### P-2 ThorDS Original

Same configuration, product overhead.

### P-3 Enhanced analog

Analog code/input only.

### P-4 True Widescreen

Full feature.

### P-5 OC ratios

100–200 where enabled.

### P-6 Long run

60 minutes with transitions.

## Metrics

Host:

- CPU total/per-thread;
- GPU/frame time;
- memory PSS;
- thermal state;
- battery current if accessible without privilege;
- Android jank.

Emulator:

- emulated frames;
- presented frames;
- unique game updates;
- frame deadline misses;
- queue depth;
- audio underruns;
- shader compile;
- ARM9/ARM7 timing;
- scene classifier.

## Sampling

- warm-up 2 minutes;
- measure 5 minutes;
- identical save/position when possible;
- at least 3 runs for noisy metrics;
- report median/P95/P99.

## Known stress locations

- BOB Chain Chomp;
- mountain;
- King Bob-omb;
- water/effects;
- crowded castle;
- minigame.

## Output

CSV raw plus JSON summary.

```text
scenario
commit
apk hash
profile
renderer
scale
display modes
OC
mean/P95/P99
thermal start/end
```

## Pass

Feature overhead within budget and no realtime slowdown. Absolute 60fps is not expected for original 30fps gameplay; distinguish present FPS from unique updates.
