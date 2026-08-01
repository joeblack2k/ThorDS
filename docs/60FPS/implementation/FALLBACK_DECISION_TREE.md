# Fallback decision tree

```text
Does JIT block-entry count a semantic target?
├─ yes → use Route A
└─ no
   ├─ does compile metadata show target inside a block?
   │  ├─ yes → inject exact instruction callback
   │  └─ no
   │     ├─ verify address/overlay/vtable target
   │     └─ use game-side counter patch
```

```text
Does cadence probe change known gameplay from ~30 to ~60?
├─ yes → continue consumer/timing corrections
└─ no
   ├─ inspect AR ordering
   ├─ inspect scene initializer/reset
   ├─ patch VBlank/initializer source
   └─ remeasure
```

```text
Does 60 FPS sustain at 100% ARM9?
├─ yes → keep 100
└─ no → 125 → 150 → 175 → 200
```

```text
Does a stress failure change game speed?
├─ CPU debt/slow motion → headroom/optimization
└─ object too fast/slow → fixed-step semantic patch
```
