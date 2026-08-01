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
runtime-patch investigation. It is stronger than presenter FPS or
`NDS::RunFrame()` counts.

## What this does not prove

It does not yet prove that changing the value produces correct 60fps
gameplay. The remaining work must establish:

- every initialization/scene path that can overwrite the tick;
- the exact mapping between the IRQ scene-graph slots and the active
  `Stage`/overlay object in the EU binary;
- the exact legal runtime patch representation and original-word
  preconditions;
- semantic unique-update accounting;
- normal wall-clock, timers, physics, animation and audio;
- stress-scene and combined-feature behavior.

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

The active-object connection between the IRQ graph callbacks and these
behavior/render entries is not fully proven by the public decomp. Therefore
the callback slots are valid instrumentation targets, but not yet valid
replacement targets for a product runtime patch.
