# Cadence Initializer Writes

Source tree SHA-256:
`d7157768930c35f83d064bcc2baf74057a759768832e833165cc4218787dd524`

The pinned EU decomp contains 19 writes to `data_0208ee44`:

| File | Line | Value | Role |
|---|---:|---:|---|
| `src/func_0203506c.c` | 73 | `1` | boot/init |
| `src/_ZN5Stage13InitResourcesEv.cpp` | 362 | `2` | Stage initializer |
| `src/func_ov002_020f7780.c` | 23 | `3` | scene overlay |
| `src/func_ov003_020ada9c.cpp` | 59 | `2` | scene overlay |
| `src/func_ov003_020af8a0.c` | 423 | `1` | scene overlay |
| `src/func_ov003_020b0b3c.c` | 172 | `1` | scene overlay |
| `src/func_ov004_020b265c.c` | 69 | `1` | scene overlay |
| `src/func_ov005_020c1a20.c` | 248 | `1` | scene overlay |
| `src/func_ov006_020d5384.cpp` | 95 | `1` | scene overlay |
| `src/func_ov006_020de704.c` | 67 | `1` | scene overlay |
| `src/func_ov006_020e0308.cpp` | 107 | `1` | scene overlay |
| `src/func_ov006_020e7124.c` | 66 | `1` | scene overlay |
| `src/func_ov006_0210a708.c` | 67 | `1` | scene overlay |
| `src/func_ov006_0210bdb0.cpp` | 137 | `1` | scene overlay |
| `src/func_ov006_0210d1fc.cpp` | 171 | `1` | scene overlay |
| `src/func_ov006_02129268.c` | 35 | `1` | scene overlay |
| `src/func_ov006_0212b480.c` | 34 | `1` | scene overlay |
| `src/func_ov007_020cc4c0.cpp` | 49 | `1` | scene overlay |
| `src/func_ov075_0211a410.cpp` | 140 | `2` | entry/music initializer |

This report proves the write sites in the source inventory. It does not prove
that every write is active in ordinary gameplay, and it does not classify all
reads. The F2 gate remains open.

