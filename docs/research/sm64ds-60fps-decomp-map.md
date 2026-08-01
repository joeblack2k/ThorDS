# SM64DS EU 60fps decomp map

Date: 2026-08-01
Profile: `ASMP`, revision `0`
Source: pinned local SM64DS decomp checkout

## Confirmed frame-schedule path

The European decomp identifies the following game-side path:

1. `IRQ::VBlankHandler` at `0x0201a534` increments `data_0209d514`.
2. When that counter reaches `data_0208ee44`, the handler wakes the game
   thread, resets the counter, and calls `func_02019144`.
3. `func_02019144` performs the render-frame OAM/display submission.
4. The handler always wakes `data_0209d4fc` and calls `func_02019100`, the
   lag-frame callback path.
5. `data_0208ee44` is the game-side frame tick used by update code, not an
   Android presenter value.
6. The normal scene object uses `_ZTV5Stage`; its semantic behavior entry is
   `Stage::Behavior` at `0x0202bbbc` (vtable slot 6), while its render entry is
   `Stage::Render` at `0x0202b8a4` (vtable slot 9).
7. The IRQ callbacks use different vtable slots: `func_02019144` dispatches
   slot 2 and `func_02019100` dispatches slot 3. They are scene-graph
   callbacks, not direct calls to `Stage::Behavior`.
8. The `dScEntry_c` overlay supplies `Behavior` at `0x0211a2b8` and `Render`
   at `0x0211a26c`; these are the strongest entry-scene candidates for
   semantic update and render work.
9. `Scene::BeforeBehavior` at `0x0202e3d4` and `Scene::BeforeRender` at
   `0x0202e3a4` retain the same addresses and semantics at both the product pin
   and audit commit `755f0be5b9658e5f75871c4138ddc0133a2c07c4`.
10. A paired Castle Garden run measured Stage and Scene behavior/render at
    approximately 30/s with cadence 2 and approximately 60/s with cadence 1.
    The cadence probe therefore reaches the semantic scene path.

Source references:

- `tools/research/sm64ds-decomp/src/_ZN3IRQ13VBlankHandlerEv.c`
- `tools/research/sm64ds-decomp/src/func_02019100.c`
- `tools/research/sm64ds-decomp/src/func_02019144.c`
- `tools/research/sm64ds-decomp/symbols/verified.tsv`

## Initialization evidence

- `func_02005a58.c` initializes the boot resource path with
  `data_0208ee44 = 1`.
- `dScEntry_c::InitResources` at `0x0211a410` sets
  `data_0208ee44 = 2` for the normal entry path.
- Multiple scene/overlay initializers restore the value to `1`, so a
  one-time write at boot is not sufficient evidence for a stable 60fps mode.
- Update code consumes the same value as a delta, including stage, message,
  HUD and particle paths.

Relevant references:

- `tools/research/sm64ds-decomp/src/func_02005a58.c`
- `tools/research/sm64ds-decomp/src/func_ov075_0211a410.cpp`
- `tools/research/sm64ds-decomp/src/func_020199a4.c`
- `tools/research/sm64ds-decomp/src/_ZN7Message6UpdateEv.cpp`
- `tools/research/sm64ds-decomp/src/_ZN5Stage8BehaviorEv.cpp`
- `tools/research/sm64ds-decomp/src/func_ov075_0211a2b8.cpp`
- `tools/research/sm64ds-decomp/src/func_ov075_0211a26c.cpp`
- `tools/research/sm64ds-decomp/config/arm9/relocs.txt`
- `tools/research/sm64ds-decomp/symbols/verified.tsv`
- `tools/research/sm64ds-decomp/src/_ZN5Stage9PS_UpdateEv.cpp`

## What this proves

This is a concrete EU game-side timing hook and a viable target for the M13
runtime-patch investigation. The paired Stage/Scene counters prove that the
developer cadence probe creates real semantic updates rather than duplicated
presentation frames.

## What this does not prove

It does not prove correct-speed 60fps gameplay. The owner observed
approximately 2x gameplay speed while the cadence probe was active. The
remaining work must establish:

- every initialization/scene path that can overwrite the tick;
- the exact mapping between the IRQ scene-graph slots and the active
  `Stage`/overlay object in the EU binary;
- the exact legal runtime patch representation and original-word
  preconditions;
- semantic unique-update accounting;
- normal wall-clock, timers, physics, animation and audio;
- stress-scene and combined-feature behavior.

`func_020199a4` at `0x020199a4` is not the ordinary gameplay scheduler. It is
a special-state infinite loop reached conditionally from Stage/transition
paths. Its zero count in Castle Garden is expected and it is not retained as a
semantic marker.

No ROM bytes, patched ROM, save, save state or guessed Action Replay payload
is included in this repository.

## Dispatch boundary

The current evidence supports this distinction:

```text
VBlank
 ├─ conditional func_02019144 -> active-object vtable slot 2
 │                              -> Scene::GraphCallback2 layer
 └─ unconditional func_02019100 -> active-object vtable slot 3
                                   -> Scene::GraphCallback3 layer

semantic behavior:
  Stage::Behavior slot 6 -> 0x0202bbbc
  dScEntry_c::Behavior    -> 0x0211a2b8

semantic render:
  Stage::Render slot 9    -> 0x0202b8a4
  dScEntry_c::Render      -> 0x0211a26c
```

The paired runtime counters now prove the active Castle Garden scene reaches
the Stage and Scene behavior/render entries at the expected cadence. This does
not make any callback slot a valid replacement target for a product runtime
patch; fixed-step consumers and exact original-word guards remain unresolved.
