# Timing parity

## Ten-minute measurements

- real monotonic duration;
- game timer;
- countdown;
- cutscene duration;
- door/star transition;
- animation cycles;
- platform cycles.

## Tolerance

Primary wall-clock drift:

```text
<= 0.1% over 10 minutes
```

Use tighter tolerances where deterministic integer values permit it.

## Fractional refresh

Use 59.826098 Hz as the expected DS cadence, not exactly 60.000.
