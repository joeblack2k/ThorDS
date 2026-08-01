# Semantic update tests

## Host

Feed synthetic PC sequences into the monitor and verify exact counters.

## JIT

Run a synthetic ARM9 block whose start PC is a target and prove one dispatch
increments once.

## Interpreter

Execute the same target and prove parity.

## Game

For each checkpoint, record:

```text
main loop
slot1
cadence render
lag callback
Stage/entry behavior
Stage/entry render
game-state unique count
```

## Acceptance

Ordinary scene:

```text
Original behavior/update ~29.913/s
60 FPS behavior/update   ~59.826/s
```

Already-60 scene:

```text
Original ~59.826/s
60 FPS   ~59.826/s
```
