# EU Cadence Write Candidate Inventory

Symbol: `data_0208ee44`
Source tree SHA-256: `1a9a06cd51443f7f28fda8c35edbc276fc8a6074aaf2e9b49c4340e14d490499`
Candidate writes: `18`

This is a source-level candidate list, not an authorized patch list. The
decompilation contains type and symbol ambiguities, including pointer casts and
overlay-local declarations. Each candidate still needs binary-address and
runtime-scene confirmation before it can be patched.

| File | Line | Function | Category | Source |
|---|---:|---|---|---|
| `src/func_0203506c.c` | 77 | `dScMB_c_InitResources` | boot/init | `*(int *)&data_0208ee44 = 1;` |
| `src/func_ov002_020f7780.c` | 23 | `func_ov002_020f7780` | scene-specific | `data_0208ee44 = 3;` |
| `src/func_ov003_020ada9c.cpp` | 62 | `LoadOBJPltt` | scene-specific | `*(int*)data_0208ee44 = 2;` |
| `src/func_ov003_020af8a0.c` | 423 | `func_ov003_020af8a0` | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov003_020b0b3c.c` | 175 | `dScGameOver_c_InitResources` | boot/init | `data_0208ee44 = 1;` |
| `src/func_ov004_020b265c.c` | 75 | `func_ov004_020b265c` | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov005_020c1a20.c` | 251 | `dScMiniGm_c_InitResources` | boot/init | `data_0208ee44 = 1;` |
| `src/func_ov006_020d5384.cpp` | 100 | `func_ov006_020d3ba0` | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov006_020de704.c` | 70 | `dScMgCoin_c_InitResources` | boot/init | `data_0208ee44 = 1;` |
| `src/func_ov006_020e0308.cpp` | 109 | `dScMgCup_c_InitResources` | boot/init | `data_0208ee44 = 1;` |
| `src/func_ov006_020e7124.c` | 70 | `func_ov006_020e7124` | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov006_0210a708.c` | 72 | `func_ov006_0210a708` | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov006_0210bdb0.cpp` | 140 | `m48` | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov006_0210d1fc.cpp` | 174 | `m48` | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov006_02129268.c` | 40 | `dScMgSnowball_c_InitResources` | boot/init | `data_0208ee44 = 1;` |
| `src/func_ov006_0212b480.c` | 37 | `dScMgFlower_c_InitResources` | boot/init | `data_0208ee44 = 1;` |
| `src/func_ov007_020cc4c0.cpp` | 49 | `LoadInitialGroup` | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov075_0211a410.cpp` | 145 | `_ZN5Sound22LoadAndSetMusic_Layer1Ei` | scene-specific | `data_0208ee44 = 2;` |
