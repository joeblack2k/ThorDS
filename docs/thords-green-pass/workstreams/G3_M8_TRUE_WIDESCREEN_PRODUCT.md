# G3 — M8 product True Widescreen

## Goal

Convert the M7 developer probe into a normal exact-profile feature.

## Required implementation

- replace boolean developer semantics with a product presentation mode/capability;
- resolve True Widescreen through the existing profile plan;
- default on only for exact EU SM64DS + supported Thor + Vulkan structured compositor;
- keep a separate hidden developer diagnostic mode;
- apply the guarded game-side aspect/culling runtime code only for the exact profile;
- draw world at full width and 2D/UI in the centered 4:3 safe rect;
- implement scene classifier hysteresis and safe fallback;
- preserve capture-backed/previous-frame/brightness/protected-black behavior;
- keep lower screen 4:3;
- integrate safe mode and relaunch semantics;
- expose requested/effective mode to session status and UI;
- ensure OpenGL/software state says unavailable and uses 4:3.

## Product scene matrix

M7 remains Castle Garden scoped, but M8 must test representative classes:

- title/file select/menu;
- castle garden and lobby;
- course select/pause;
- world transition/painting/star collection;
- water/interior/fog/effects;
- a dense actor scene;
- screen swap and sleep/resume.

Unsafe scenes may use smooth 4:3 fallback. Normal gameplay must not spend most of its time in fallback.

## Performance

No per-frame allocation growth, pipeline recreation or classifier oscillation. Meet existing performance budgets.

## Exit

- normal UI toggle works;
- no developer extra required;
- 2D/glyph/bottom metrics green;
- scene matrix and 30-minute transition run green;
- safe 4:3 recovery green.

## Suggested commit

```text
render: ship SM64DS layer-aware true widescreen
```
