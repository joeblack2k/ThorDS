# M13 F4 fixed-step decomp deep dive

Date: 2026-08-02

Status: SOURCE AND FUNCTION-RANGE INVENTORY COMPLETE; SEMANTIC DISPOSITION OPEN

## Scope

- Exact profile: ASMP / revision 0.
- Pinned decomp source-tree SHA-256:
  `1a9a06cd51443f7f28fda8c35edbc276fc8a6074aaf2e9b49c4340e14d490499`.
- Source files scanned: 11,575.
- Candidate findings: 1,945 across 327 source files.
- Candidate source files mapped to exact ARM9/overlay function ranges: 327.
- Unresolved source files: 0.
- Binary relocations to `data_0208ee44`: 57.
- Unresolved cadence relocations: 0.

The source scan generates candidates; it does not claim that every candidate is
a timing bug. The binary map proves function ownership and image identity, not
the exact instruction corresponding to each source line.

## Candidate classes

| Candidate kind | Count |
|---|---:|
| Direct cadence-symbol references | 192 |
| Direct pointer/member state mutations | 1,718 |
| Timing or fixed-point helper mutations | 35 |

The direct-mutation set contains 227 explicit `++`, `--`, `+=` or `-=` steps.
The remaining state writes are retained because they may participate in
per-update transitions. A further 124,107 numeric-only source lines were
counted but excluded from the public candidate records to avoid unreviewable
noise.

## Timing domains

| Domain | Candidates | Current disposition |
|---|---:|---|
| World/player physics | 1,285 | Shared motion hooks are active; actor-specific steps remain open |
| Animation | 27 | `Animation::Advance` is corrected; other animation helpers remain open |
| Rotation | 29 | Coin spin is the first exact direct-step correction |
| Particle/effect | 140 | Shared tracker parity hook is active; spawn/effect paths remain open |
| Message/HUD | 81 | Open |
| Camera/cutscene | 65 | Open; must remain separate from ordinary camera render cadence |
| Audio trigger | 25 | Open |
| Timer/counter | 7 | Open |
| Scene/overlay | 92 | Open |
| Unclassified | 194 | Open and product-gating |

## First exact direct-step result

`Coin::Behavior` in overlay 2 increments the shared coin Y rotation by a fixed
`0xC00` on every behavior call. Cadence 1 therefore doubled visible coin spin.
The exact EU instruction is at `0x020B23B0`.

V10 changes only that immediate step to `0x600`, encoded as ARM word
`E2811C06` (the earlier `E2811B06` encoding was incorrect and represented
`0x1800`, so it was rejected). The instruction shape,
registers and control flow are unchanged. Two cadence-1 updates now produce the
same total rotation as one original cadence-2 update while still producing a
new visual angle every update.

## Staged disposition

1. World/player motion, collision and direct actor steps.
2. Animation, particle lifetime/spawn and direct rotations.
3. Timers, messages, HUD and audio triggers.
4. Camera, cutscene, scene-transition and minigame exceptions.

Each candidate must be proven delta-aware, patched with exact binary guards, or
left red. Static inventory alone cannot close F4-F7 or the M13 product gate.

Generated evidence:

- `docs/evidence/m13/f4-fixed-step-candidates.json`
- `docs/evidence/m13/f4-fixed-step-binary-map.json`

No ROM image, ROM range, save-state bytes, private path, device identifier or
private capture is included.
