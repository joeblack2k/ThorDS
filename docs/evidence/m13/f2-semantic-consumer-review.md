# M13 F2 Semantic Consumer Review

Source tree SHA-256: `1a9a06cd51443f7f28fda8c35edbc276fc8a6074aaf2e9b49c4340e14d490499`

## Primary runtime path

The primary EU ARM9 cadence consumer is:

| Address | Function | Evidence | Disposition |
|---|---|---|---|
| `0x020199A4` | `func_020199a4` | Reads `data_0208ee44` into `dt`, decrements the local scheduler countdown, then calls `func_02019AC4` when the countdown expires | primary scheduler/semantic boundary |
| `0x02019AC4` | `func_02019ac4` | Consumes `data_0208ee44` as a timer delta | downstream timer consumer |
| `0x020242C8` | `Stage::LC_Update` | Uses cadence for scene transition timers and fixed-step scene values | downstream scene consumer |
| `0x0202BBBC` | `Stage::Behavior` | Uses cadence for behavior timer state | downstream behavior consumer |
| `0x020326AC` | `func_020326ac` | Uses cadence for message/HUD timing | downstream UI consumer |
| `0x02034B40` | `func_02034b40` | Uses cadence for OAM/render timing | downstream render consumer |

`func_020199A4` is the first candidate that can establish whether the
cadence value is being consumed at the semantic scheduler boundary. The
downstream reads must not be independently rewritten without proving that the
primary scheduler and fixed-step consumers require it.

## Review result

- The 192 inventory findings are downstream reads, scheduler/timer reads,
  render/UI reads, scene behavior reads or scene-specific overlay reads.
- The 18 writes are initialization or scene/overlay transition assignments.
- No source-level candidate is promoted to an F4 patch by this review.
- The next runtime experiment should count entry to `0x020199A4` and correlate
  it with the existing semantic window, a known 30 FPS baseline and the
  Enhanced cadence probe.

## Gate

This closes the source-level primary-consumer mapping only. It does not prove
60fps timing parity, physics/animation/audio equivalence or a product patch.
ROM bytes, savestates and private device captures are not included.
