# M13 F2 Semantic Consumer Review

Source tree SHA-256: `1a9a06cd51443f7f28fda8c35edbc276fc8a6074aaf2e9b49c4340e14d490499`

## Cadence consumers and runtime markers

The reviewed EU ARM9 cadence consumers and semantic markers are:

| Address | Function | Evidence | Disposition |
|---|---|---|---|
| `0x020199A4` | `func_020199a4` | Special-state infinite loop reached conditionally from Stage/transition code; its local countdown consumes `data_0208ee44` | not the ordinary gameplay scheduler; rejected as a Castle Garden marker |
| `0x02019AC4` | `func_02019ac4` | Consumes `data_0208ee44` as a timer delta | transition/timer consumer |
| `0x020242C8` | `Stage::LC_Update` | Uses cadence for scene transition timers and fixed-step scene values | downstream scene consumer |
| `0x0202BBBC` | `Stage::Behavior` | Uses cadence for behavior timer state | downstream behavior consumer |
| `0x0202E3D4` | `Scene::BeforeBehavior` | Exact function entry at both the product and audit decomp pins | ordinary scene behavior marker |
| `0x0202E3A4` | `Scene::BeforeRender` | Exact function entry at both the product and audit decomp pins | ordinary scene render marker |
| `0x020326AC` | `func_020326ac` | Uses cadence for message/HUD timing | downstream UI consumer |
| `0x02034B40` | `func_02034b40` | Uses cadence for OAM/render timing | downstream render consumer |

The `0x020199A4` marker was removed after static review and a zero-count Castle
Garden run. The scene markers were added after the existing indices so prior
counter arrays remain comparable.

## Review result

- The 192 inventory findings are downstream reads, scheduler/timer reads,
  render/UI reads, scene behavior reads or scene-specific overlay reads.
- The 18 writes are initialization or scene/overlay transition assignments.
- No source-level candidate is promoted to an F4 patch by this review.
- A paired Castle Garden run proved that the guarded cadence probe changes
  Stage and Scene behavior/render from approximately 30/s to approximately
  60/s. The owner's physical witness simultaneously found approximately 2x
  gameplay speed.
- F3 therefore proves a real semantic cadence transition, while F4 remains red
  for fixed-step movement, physics, animation, particles, timers and audio.

## Gate

This closes the rejected-marker correction and the F3 semantic-transition
question. It does not prove timing parity, physics/animation/audio equivalence
or a product patch. ROM bytes, save states and private device captures are not
included.
