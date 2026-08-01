# Performance and future 60fps compatibility

## Current camera gate

Smooth Orbit must add negligible CPU/GPU overhead.

Measure:

- input processing time;
- JNI call rate;
- protocol read cost;
- game-patch cost;
- frame deadline misses;
- audio underruns.

## Event-rate control

Do not call JNI excessively for unchanged values. Send when:

- right-stick value materially changes;
- R3 sequence changes;
- effective configuration changes;
- lifecycle requires neutralization.

## 60fps readiness

The camera protocol contains `yawUnitsPerTick`.

Automated simulation:

```text
30 updates × 1001 units
60 updates × 501 units
```

must produce near-equal one-second rotation.

This test does not claim the game itself runs at 60fps.
