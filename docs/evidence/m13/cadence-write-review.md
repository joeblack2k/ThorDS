# EU Cadence Write Candidate Review

Source tree SHA-256: `1a9a06cd51443f7f28fda8c35edbc276fc8a6074aaf2e9b49c4340e14d490499`

## Disposition

The 18 source-level writes to `data_0208ee44` are initialization or
scene/overlay-transition assignments. They are not, by themselves, fixed-step
gameplay consumers. No write is promoted to an F4 patch manifest.

| Candidates | Observed role | Disposition | Reason |
|---|---|---|---|
| `func_0203506c.c:77` | Main scene resource initialization, writes `1` | retain/verify | Boot/init value; not a per-update consumer |
| `func_ov002_020f7780.c:23` | Overlay-specific mode transition, writes `3` | retain/verify | Scene-specific branch; requires overlay/runtime confirmation |
| `func_ov003_020ada9c.cpp:62` | Overlay resource/music initialization, writes `2` | retain/verify | Initialization assignment |
| `func_ov003_020af8a0.c:423` | Overlay-specific transition, writes `1` | retain/verify | Scene-specific assignment |
| `func_ov003_020b0b3c.c:175` | Game-over scene initialization, writes `1` | retain/verify | Scene lifecycle value |
| `func_ov004_020b265c.c:75` | Overlay-specific transition, writes `1` | retain/verify | Scene-specific assignment |
| `func_ov005_020c1a20.c:251` | Minigame scene initialization, writes `1` | retain/verify | Scene lifecycle value |
| `func_ov006_020d5384.cpp:100` | Overlay-specific transition, writes `1` | retain/verify | Scene-specific assignment |
| `func_ov006_020de704.c:70` | Minigame resource initialization, writes `1` | retain/verify | Initialization assignment |
| `func_ov006_020e0308.cpp:109` | Minigame cup initialization, writes `1` | retain/verify | Initialization assignment |
| `func_ov006_020e7124.c:70` | Overlay-specific transition, writes `1` | retain/verify | Scene-specific assignment |
| `func_ov006_0210a708.c:72` | Overlay-specific transition, writes `1` | retain/verify | Scene-specific assignment |
| `func_ov006_0210bdb0.cpp:140` | Minigame transition, writes `1` | retain/verify | Scene-specific assignment |
| `func_ov006_0210d1fc.cpp:174` | Minigame transition, writes `1` | retain/verify | Scene-specific assignment |
| `func_ov006_02129268.c:40` | Snowball minigame initialization, writes `1` | retain/verify | Initialization assignment |
| `func_ov006_0212b480.c:37` | Flower minigame initialization, writes `1` | retain/verify | Initialization assignment |
| `func_ov007_020cc4c0.cpp:49` | Overlay group loading, writes `1` | retain/verify | Resource/scene transition |
| `func_ov075_0211a410.cpp:145` | Sound/overlay transition, writes `2` | retain/verify | Audio/scene transition; not a gameplay timestep |

## Gate

This review closes the source-level disposition for the write list only. F2
semantic review remains open until the read/consumer paths are matched to
binary addresses and validated in a known 30 FPS baseline and the Enhanced
60fps state. No cadence write is authorized for a product patch by this file.

ROM bytes, savestates and private device captures are not included.
