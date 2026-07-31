# SM64DS Europe Widescreen Symbol Map

Status: M7 research, not a production patch list.

Research source:

- `tangosdev/sm64ds-decomp` at `2d38fe9b825199deec408240849b64b91c965d85`
- local ASMP revision 0 ROM with RetroAchievements system hash
  `ba3c4052e00c5cc31df5d5534c39de1b`

The decomp is local under `tools/research/`, which is ignored. No game assets,
decompressed module, or extracted ROM data is part of this repository.

## Semantically proven sites

| Runtime address | Module | Symbol or role | Original word | Candidate word | Status |
|---|---|---|---|---|---|
| `0x0200D03C` | main ARM9 | `func_0200cf40`: initializes view-object aspect state | `0x00001555` | `0x00001C72` | candidate |
| `0x0200F64C` | main ARM9 | `Initialise3dGraphics`: default 3D perspective | `0x00001555` | `0x00001C72` | candidate |
| `0x02015774` | main ARM9 | `Clipper::Clipper`: initializes `Clipper::aspectRatio` | `0x00001555` | `0x00001C72` | candidate |
| `0x020C025C` | ARM9 overlay 6 | `Camera_UpdateMatrices`: shared gameplay projection and clipper aspect literal | `0x00001555` | `0x00001C72` | developer probe candidate |
| `0x0209F318 -> camera + 0xF8` | main RAM indirect | live `Camera::Render` aspect field after camera initialization | `0x00001555` | `0x00001C72` | Castle Garden save-state probe |

`Camera_UpdateMatrices` is a byte-matched overlay-6 function at
`0x020C0134`, size `0x130`; its local code range SHA-256 is
`0d72fe606146861bdaca6c07087235f6f052a8dcb3bab6a047b76d964649f6e8`.
The function's PC-relative load at `0x020C0204` resolves to the literal at
`0x020C025C`; that word is passed to both the perspective and clipper calls.
The next function begins at `0x020C0264`, so `0x020C026C` is not an aspect
literal.

`Camera::Render` consumes the live aspect field at `camera + 0xF8`, while
`func_0200cf40` initializes that field from the main ARM9 literal at
`0x0200D03C`. The Castle Garden checkpoint is loaded after initialization, so
the developer probe also follows the exact camera pointer stored at
`0x0209F318` and writes only `camera + 0xF8` when that pointer is non-zero.

The overlay is compressed in the cartridge and reloaded at runtime. A one-shot
ROM-file write is therefore invalid. Any future Action Replay implementation
must verify the original runtime word and safely reapply only while the exact
overlay is resident.

## Excluded candidates

The decomp has other `0x1555` occurrences. They are not automatically camera
or culling state. Menu-specific perspectives, UI transforms, bounds and
unclassified overlay literals remain excluded until scene evidence shows that
they are needed.

## M7 decision

The single overlay-6 candidate is sufficient to test a developer-only
projection diagnostic. It is not enough to ship a Europe true-widescreen
profile: culling, scene coverage, HUD ownership, transitions and reload
behavior still require the M7 scene matrix.
