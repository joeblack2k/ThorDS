# Frame pacing

## Target cadence

The DS refresh reference is approximately:

```text
59.826098 Hz
```

Do not hardcode 60.000 in drift calculations.

## Measure separately

- semantic update timestamps;
- render callback timestamps;
- presenter timestamps;
- VBlank timestamps;
- audio buffer/underrun timing.

## Acceptance

- no 30/60 alternation after validation;
- no repeated identical game-state frame counted as unique;
- stable update intervals;
- no periodic stutter caused by fractional scheduling;
- no long-term drift from assuming exactly 60 Hz.

## Presentation

True Widescreen's layer-aware compositor remains unchanged. It receives the
new game states; it is not responsible for creating 60 FPS.
