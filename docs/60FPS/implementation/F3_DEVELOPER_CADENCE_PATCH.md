# F3 — developer cadence patch

## First probe

Use the prepared guarded 2→1 write only in the exact Enhanced debug session.

## Add profile state

```text
60fps-dev-cadence
default false
experimental true
requires relaunch
```

## Telemetry

Automatically capture:

- cadence transitions;
- semantic counters;
- ARM9 telemetry;
- presenter/renderer counters;
- audio underruns.

## Exit

In a proven Original-30 gameplay checkpoint:

```text
Original: ~30 semantic updates/s
Probe:    ~60 semantic updates/s
```

If that does not happen, do not progress to timing fixes until the scheduler
model is corrected.
