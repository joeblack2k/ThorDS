# Model decision

## Primary model: Terra Xhigh

Use **Terra Xhigh** for this continuation pass.

The remaining work is implementation-heavy and crosses:

- Kotlin session/profile state;
- Android dual-display UI;
- Vulkan C++ presentation and shaders;
- JNI/native configuration;
- melonDS scheduler/core timing;
- decomp-based runtime patch analysis;
- physical ADB validation.

Luna Xhigh performed the long bootstrap, safety work, profile architecture and detailed evidence collection well. The current log also shows that Luna became anchored to repeated M7 evidence refinements while M8, actual ARM9 runtime, the full GUI and 60fps remained unimplemented. The next pass needs a fresh implementation-first model with the existing evidence retained, not discarded.

## Roles

```text
Terra Xhigh  primary implementation and integration owner
Luna Xhigh   optional final release/evidence audit after implementation
Sol          optional bounded reviewer/test generator, not primary owner
```

Do not switch the primary model mid-workstream. A side model may review a completed diff but may not redefine the product contract or replace source inspection.

## Why not Sol as primary

This is not a small isolated patch. Sol is appropriate for a narrow code review, one failing test, one parser fix or one research side thread. It is not the preferred owner for an end-to-end Vulkan + emulator-core + Android UI + physical hardware closure run.
