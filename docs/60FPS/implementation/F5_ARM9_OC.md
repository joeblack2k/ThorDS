# F5 — ARM9 headroom

## Baseline

Reconfirm 100% equivalence first.

## Ascending validation

Test:

```text
125
150
175
200
```

For each ratio:

- effective ratio;
- scaled cycles/remainder;
- ARM9 target debt;
- ARM7 timestamp;
- frame deadlines;
- audio underruns;
- stress-scene semantic rate;
- thermal/soak behavior.

## Selection

Use the lowest fully sustaining ratio.

Do not expose higher ratios merely because they boot.

## Failure

If no ratio sustains the mode, timing/patch workload must be reduced or
optimized. Do not hide slow motion behind a nominal 60 FPS label.
