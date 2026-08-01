# TAS frame model

TAS documentation reports:

```text
DS/emulator cadence: about 59.826 FPS
most SM64DS gameplay: important addresses update every 2 frames
title and star select: exceptions
input counted when actual game frame completes
```

## Test implications

- Baseline must include an ordinary level, not only title/castle transitions.
- Expected Original semantic update rate is about 29.913/s.
- Expected 60 FPS semantic update rate is about 59.826/s.
- Input replay must be indexed to semantic update completion as well as DS
  VBlank to avoid comparing different input timing.
