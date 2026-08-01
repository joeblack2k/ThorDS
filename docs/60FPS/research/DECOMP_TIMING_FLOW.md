# Decomp timing flow

## VBlank

```text
IRQ::VBlankHandler
  data_0209D514++
  if accumulator >= data_0208EE44:
      wake main game thread
      reset accumulator
      func_02019144()
  always:
      wake secondary thread
      func_02019100()
```

## Main loop

`func_020197B8` runs continuously and sleeps through `func_0201A4BC`.

Important phases include:

```text
func_02019390  active-object vtable slot 0
func_02019404  active-object vtable slot 1
main-loop counter increment
wait for cadence-gated wake
```

## Graph callbacks

```text
func_02019144 → active-object vtable slot 2
func_02019100 → active-object vtable slot 3
```

These are not automatically `Stage::Behavior` and `Stage::Render`.

## Semantic candidates

```text
Stage::Behavior       0x0202BBBC
Stage::Render         0x0202B8A4
dScEntry Behavior     0x0211A2B8
dScEntry Render       0x0211A26C
dScEntry Init         0x0211A410
```

Every mapping must be confirmed against the current EU overlay layout.
