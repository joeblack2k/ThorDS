# ARM9 overclock dependency

## Goal

Provide more ARM9 work budget inside the same DS wall-clock frame.

It is not:

- fast-forward;
- a higher DS clock domain exposed to game timers;
- a change to ARM7/audio/RTC/VBlank rates.

## Existing foundation

The core already records:

```text
percent
remainder
baseCycles
scaledCycles
system timestamp
ARM9 timestamp/target
ARM7 timestamp
frame count
```

## Validation order

```text
100% equivalence
125%
150%
175%
200%
```

Stop increasing once the lowest ratio sustains all stress scenes.

## Effective policy

- 60 FPS can request a ratio.
- The core reports actual effective ratio.
- Unsupported ratios fail closed to a known value.
- UI never claims a requested value is effective.
- timing mode and ratio are stored in save-state compatibility metadata.
- Hardcore forces 100%.
