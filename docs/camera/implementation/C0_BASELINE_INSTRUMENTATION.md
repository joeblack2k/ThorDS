# C0 — baseline and instrumentation

## Goal

Prove the current behavior before replacing it.

## Required baseline trace

Inject deterministic right-stick values:

```text
-1.00, -0.75, -0.50, -0.25, 0.00, +0.25, +0.50, +0.75, +1.00
```

Record:

- D-pad bits produced;
- camera yaw before/after;
- maximum per-update yaw delta;
- repeat timing;
- recenter bit;
- arrow render counts;
- sound trigger.

## Expected current signature

The current digital path should show:

- no movement below threshold;
- `0x200` or `0x100` beyond threshold;
- target changes near `0x2000`;
- repeated steps after the timer.

Treat this as a hypothesis until trace output proves it.

## Hook proof

Produce:

```text
docs/evidence/camera/eu-camera-hook-liveness.md
```

It must contain:

- exact ROM identity;
- function address;
- instruction range;
- register liveness;
- stack impact;
- overwritten instructions;
- return/fallthrough addresses;
- patch size;
- expected-word guards;
- state gates.
