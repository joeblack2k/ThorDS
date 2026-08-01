# Semantic update marker candidates

## Primary

Actual ARM9 block execution counters:

- main-loop slot-1 callback;
- Stage Behavior;
- Stage Render;
- entry Behavior/Render.

## Secondary

- main-loop counter in a proven 30 FPS gameplay checkpoint;
- player position/action/animation change rate;
- game-state hash excluding rendering-only memory;
- a patch-owned Behavior counter.

## Rejected as sole proof

- presenter FPS;
- `NDS::RunFrame()` count;
- VBlank count;
- Castle Garden main-loop count;
- inactive Stage timer;
- branch-helper target count;
- screenshot uniqueness alone.

## Cross-check rule

No one signal is enough. Final proof requires at least:

1. semantic function/counter rate;
2. deterministic gameplay-state changes;
3. wall-clock/physics parity.
