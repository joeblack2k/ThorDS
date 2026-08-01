# Performance budgets

## Telemetry overhead

- exact-profile only;
- disabled outside validation/product 60 FPS;
- no per-block logging;
- atomic increment only;
- aggregate once per second.

## Product

The lowest sustaining ARM9 ratio should provide margin, not merely average 60.

Track:

```text
semantic rate p1/min
ARM9 debt
deadline misses
audio underruns
thermal degradation
```

A nominal mean near 60 with recurring half-speed bursts is a failure.
