# 60fps contract

## What counts

A validated 60fps mode produces approximately 60 **unique semantic SM64DS game updates** per real second.

Not sufficient:

- Android display refresh at 60/120 Hz;
- Vulkan presenter at 60 FPS;
- duplicated DS frames;
- interpolation;
- fast-forward;
- doubled physics or timers.

## Required measurements

For a ten-minute run:

```text
unique game updates
presented frames
duplicated frames
wall elapsed
game timer elapsed
animation/physics checkpoints
audio duration/underruns
effective ARM9 ratio
```

Drift target: ≤0.1%.

## Patch provenance

Every changed ARM instruction/data word must map to:

- source reference/community patch;
- exact EU address/precondition;
- decomp function or documented unresolved symbol;
- semantic reason;
- generated runtime patch hash.

## Stress

Chain Chomp, mountain, boss/effects, water, transitions, saves and character movement. The mode fails if any runs in slow motion even when a counter says 60.
