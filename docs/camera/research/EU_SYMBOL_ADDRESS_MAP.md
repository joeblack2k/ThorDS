# European symbol and address map

Pinned decomp: see `08_SOURCE_PINS.md`.

| Semantic item | EU symbol/address |
|---|---|
| Normal orbit routine | `func_0200bb28`, `0x0200BB28` |
| Stage camera touch input | `Stage::CheckCameraInput`, `0x02024C70` |
| Bouncing arrows renderer | `Stage::RenderBouncingArrows`, `0x02023BE0` |
| HUD camera buttons | `HUD::RenderCameraButtons`, overlay 2, `0x020FC04C` |
| Existing AM64DS HUD guard | `0x020FC0C0` |
| Global camera pointer | `data_0209F318` |
| Camera flags | `camera + 0x154` |
| Current yaw | `camera + 0x180` |
| Base yaw | `camera + 0x182` |
| Yaw offset | `camera + 0x184` |
| Repeat timer | `camera + 0x196` |
| Recenter target | `camera + 0x19E` |
| Recenter active | `camera + 0x1A0` |

## Warning

Source addresses are semantic anchors. Runtime overlay addresses and instruction words must be verified against the exact local EU ROM and actual loaded overlay before use.
