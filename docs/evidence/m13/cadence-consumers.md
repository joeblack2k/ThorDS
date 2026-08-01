# Cadence Consumer Inventory

Symbol: `data_0208ee44`
Source tree SHA-256: `8259f0a50f8cbc26e136f12bd162217fbac4eeaccc3b75c94239e8ef149701d9`
Findings: `192`

| File | Line | Function | Kind | Category | Source |
|---|---:|---|---|---|---|
| `_ZN3HUD13UpdateVsTimerEv.cpp` | 8 | `` | read | message/HUD | `extern int data_0208ee44;` |
| `_ZN3HUD13UpdateVsTimerEv.cpp` | 29 | `HUD::UpdateVsTimer` | read | message/HUD | `data_ov002_02111188 = data_ov002_02111188 - data_0208ee44;` |
| `_ZN3HUD17UpdateHealthMeterEv.cpp` | 17 | `_ZN6Player7IsInAirEv` | read | message/HUD | `extern int data_0208ee44;` |
| `_ZN3HUD17UpdateHealthMeterEv.cpp` | 29 | `HUD::UpdateHealthMeter` | read | message/HUD | `*(unsigned short *)((((int)((char *)this)) + 0x6a) & 0xFFFFFFFFFFFFFFFFLL) -= data_0208ee44;` |
| `_ZN3HUD17UpdateHealthMeterEv.cpp` | 31 | `HUD::UpdateHealthMeter` | read | message/HUD | `*(unsigned short *)((((int)((char *)this)) + 0x6c) & 0xFFFFFFFFFFFFFFFFLL) -= data_0208ee44;` |
| `_ZN3HUD19RenderCameraButtonsEv.cpp` | 38 | `RenderCameraButtons` | read | message/HUD | `extern int data_0208ee44;` |
| `_ZN3HUD19RenderCameraButtonsEv.cpp` | 52 | `HUD::RenderCameraButtons` | read | message/HUD | `data_ov002_02111180 = t - data_0208ee44;` |
| `_ZN3IRQ13VBlankHandlerEv.c` | 4 | `` | read | unknown | `extern int data_0208ee44;` |
| `_ZN3IRQ13VBlankHandlerEv.c` | 16 | `_ZN3IRQ13VBlankHandlerEv` | read | unknown | `if ((data_0209d514 >= data_0208ee44) && (data_0209d4f0 != 0))` |
| `_ZN5Stage13UpdateMessageEv.cpp` | 13 | `` | read | message/HUD | `extern s32 data_0208ee44;` |
| `_ZN5Stage13UpdateMessageEv.cpp` | 46 | `Stage::UpdateMessage` | read | message/HUD | `data_0209d67c -= data_0208ee44;` |
| `_ZN5Stage13UpdateMessageEv.cpp` | 55 | `Stage::UpdateMessage` | read | message/HUD | `data_0209d67c -= data_0208ee44;` |
| `_ZN5Stage19RenderVsModeNewStarEv.cpp` | 9 | `` | read | unknown | `extern int data_0208ee44;` |
| `_ZN5Stage19RenderVsModeNewStarEv.cpp` | 28 | `func_02012790` | read | timer | `data_0209f308 = timer - data_0208ee44;` |
| `_ZN5Stage20RenderBouncingArrowsEv.cpp` | 5 | `` | read | unknown | `extern int data_0208ee44;` |
| `_ZN5Stage20RenderBouncingArrowsEv.cpp` | 17 | `_ZN5Stage20RenderBouncingArrowsEv` | read | unknown | `if (data_0208ee44 == 1) {` |
| `_ZN5Stage8BehaviorEv.cpp` | 35 | `IsLevelTinyHugeIslandOutside` | read | unknown | `extern s32 data_0208ee44;` |
| `_ZN5Stage8BehaviorEv.cpp` | 164 | `Stage::Behavior` | read | unknown | `data_0209f304 = data_0209f304 - data_0208ee44;` |
| `_ZN5Stage9LC_RenderEv.cpp` | 6 | `` | read | unknown | `extern int data_0208ee44;` |
| `_ZN5Stage9LC_RenderEv.cpp` | 31 | `_ZN5Stage20RenderBouncingArrowsEv` | read | unknown | `data_0209f2a8 = data_0209f2a8 + data_0208ee44;` |
| `_ZN5Stage9LC_UpdateEv.cpp` | 30 | `_ZN5Sound22StopLoadedMusic_Layer1Ej` | read | unknown | `extern int data_0208ee44;` |
| `_ZN5Stage9LC_UpdateEv.cpp` | 67 | `Stage::LC_Update` | read | unknown | `data_0209f244 = data_0209f244 - data_0208ee44;` |
| `_ZN5Stage9LC_UpdateEv.cpp` | 72 | `Stage::LC_Update` | read | unknown | `data_0209f22c = data_0209f22c - data_0208ee44;` |
| `_ZN5Stage9LC_UpdateEv.cpp` | 188 | `Stage::LC_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9LC_UpdateEv.cpp` | 191 | `Stage::LC_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9LC_UpdateEv.cpp` | 209 | `Stage::LC_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9LC_UpdateEv.cpp` | 212 | `Stage::LC_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9LC_UpdateEv.cpp` | 231 | `Stage::LC_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9LC_UpdateEv.cpp` | 234 | `Stage::LC_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9LC_UpdateEv.cpp` | 257 | `Stage::LC_Update` | read | unknown | `data_0209f2b8 = data_0209f2b8 - data_0208ee44;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 69 | `_ZN5Sound22StopLoadedMusic_Layer1Ej` | read | unknown | `extern int data_0208ee44;          /* frame tick */` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 138 | `Stage::PS_Update` | read | unknown | `data_0209f210 = data_0209f210 - data_0208ee44;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 143 | `Stage::PS_Update` | read | unknown | `data_0209f2e4 = data_0209f2e4 - data_0208ee44;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 148 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0209f244 - data_0208ee44;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 166 | `Stage::PS_Update` | read | unknown | `data_0209f2cc = data_0209f2cc - data_0208ee44;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 171 | `Stage::PS_Update` | read | unknown | `data_0209f23c = data_0209f23c - data_0208ee44;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 176 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0209f22c - data_0208ee44;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 255 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 257 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 276 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 279 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 297 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 300 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 318 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 320 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 374 | `Stage::PS_Update` | read | unknown | `*p = (u8)(data_0208ee44 * 3);` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 391 | `Stage::PS_Update` | read | unknown | `data_0209f210 = (u8)(data_0208ee44 * 3);` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 461 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 464 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 482 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 485 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 503 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 506 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 525 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 528 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 583 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 586 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 604 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 607 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 625 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 628 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 742 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 757 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 785 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 799 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 827 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 841 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 902 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 904 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 992 | `Stage::PS_Update` | read | unknown | `data_0209f210 = data_0208ee44 * 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 1054 | `Stage::PS_Update` | read | unknown | `data_0209f210 = data_0208ee44 * 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 1100 | `Stage::PS_Update` | read | unknown | `data_0209f2cc = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 1135 | `Stage::PS_Update` | read | unknown | `data_0209f2cc = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 1176 | `Stage::PS_Update` | read | unknown | `data_0209f2cc = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 1221 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 1224 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 1250 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 1292 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 1295 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 1313 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 1316 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 1335 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 1338 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 1361 | `Stage::PS_Update` | read | unknown | `data_0209f2b8 = data_0209f2b8 - data_0208ee44;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 1409 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 1411 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 1431 | `Stage::PS_Update` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9PS_UpdateEv.cpp` | 1433 | `Stage::PS_Update` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9VE_UpdateEv.cpp` | 10 | `` | read | unknown | `extern int data_0208ee44;` |
| `_ZN5Stage9VE_UpdateEv.cpp` | 30 | `_ZN5Stage9VE_UpdateEv` | read | unknown | `data_0209f244 = t - data_0208ee44;` |
| `_ZN5Stage9VE_UpdateEv.cpp` | 37 | `_ZN5Stage9VE_UpdateEv` | read | unknown | `data_0209f22c = t2 - data_0208ee44;` |
| `_ZN5Stage9VE_UpdateEv.cpp` | 61 | `_ZN5Stage9VE_UpdateEv` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9VE_UpdateEv.cpp` | 64 | `_ZN5Stage9VE_UpdateEv` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN5Stage9VE_UpdateEv.cpp` | 77 | `_ZN5Stage9VE_UpdateEv` | read | unknown | `data_0209f244 = data_0208ee44 << 2;` |
| `_ZN5Stage9VE_UpdateEv.cpp` | 81 | `_ZN5Stage9VE_UpdateEv` | read | unknown | `data_0209f22c = data_0208ee44 << 3;` |
| `_ZN7Message6UpdateEv.cpp` | 33 | `` | read | message/HUD | `extern s32 data_0208ee44;` |
| `_ZN7Message6UpdateEv.cpp` | 122 | `Message::Update` | read | message/HUD | `if (data_020a0db0 & (0x10 / data_0208ee44)) {` |
| `_ZN7Message6UpdateEv.cpp` | 156 | `Message::Update` | read | message/HUD | `if (data_020a0db0 & (0x10 / data_0208ee44)) {` |
| `_ZN7Message6UpdateEv.cpp` | 236 | `Message::Update` | read | message/HUD | `if (data_020a0db0 & (0x10 / data_0208ee44)) {` |
| `_ZN7Message6UpdateEv.cpp` | 269 | `Message::Update` | read | message/HUD | `if (!(data_020a0db0 & (0x10 / data_0208ee44))) {` |
| `_ZN7Minimap6RenderEv.cpp` | 79 | `Render` | read | unknown | `extern "C" int data_0208ee44;` |
| `_ZN7Minimap6RenderEv.cpp` | 180 | `Minimap::Render` | read | unknown | `*(u8 *)(((int)((char*)this + i) + 0x22e) & 0xFFFFFFFFFFFFFFFF) += data_0208ee44;` |
| `_ZN7Minimap8BehaviorEv.cpp` | 76 | `` | read | unknown | `extern s32  data_0208ee44;` |
| `_ZN7Minimap8BehaviorEv.cpp` | 146 | `_ZN7Minimap8BehaviorEv` | read | unknown | `F254 -= data_0208ee44;` |
| `func_02005418.c` | 19 | `` | read | unknown | `extern int data_0208ee44;` |
| `func_02005418.c` | 44 | `dScBoot_c_Behavior` | read | unknown | `r4 = data_0208ee44;` |
| `func_02005a58.c` | 23 | `func_0201a244` | read | unknown | `extern char data_0208ee44;` |
| `func_02005a58.c` | 91 | `dScBoot_c_InitResources` | read | unknown | `*(int*)(&data_0208ee44) = 1;` |
| `func_020199a4.c` | 9 | `func_020132d8` | read | unknown | `extern int data_0208ee44;` |
| `func_020199a4.c` | 24 | `func_020199a4` | read | unknown | `int dt = data_0208ee44;` |
| `func_02019ac4.c` | 16 | `func_02011db4` | read | unknown | `extern int data_0208ee44;` |
| `func_02019ac4.c` | 39 | `func_02019ac4` | read | unknown | `delta = data_0208ee44;` |
| `func_02020768.c` | 4 | `func_0202043c` | read | unknown | `extern int data_0208ee44;` |
| `func_02020768.c` | 15 | `func_02020768` | read | unknown | `*acc += data_0208ee44;` |
| `func_020326ac.c` | 25 | `` | read | unknown | `extern s32 data_0208ee44;` |
| `func_020326ac.c` | 96 | `func_020326ac` | read | unknown | `if (data_020a0db0 & (0x10 / data_0208ee44)) {` |
| `func_020326ac.c` | 123 | `func_020326ac` | read | unknown | `if (data_020a0db0 & (0x10 / data_0208ee44)) {` |
| `func_020326ac.c` | 198 | `func_020326ac` | read | unknown | `if (data_020a0db0 & (0x10 / data_0208ee44)) {` |
| `func_020326ac.c` | 219 | `func_020326ac` | read | unknown | `if (!(data_020a0db0 & (0x10 / data_0208ee44))) {` |
| `func_02034b40.c` | 18 | `_ZN3OAM4LoadEv` | read | unknown | `extern int data_0208ee44;` |
| `func_02034b40.c` | 62 | `func_02034b40` | read | unknown | `*(int*)(((long long)(int)(self + 8)) & 0xFFFFFFFFFFFFFFFFLL) += (data_0208ee44 << 10);` |
| `func_02034b40.c` | 69 | `func_02034b40` | read | unknown | `*(unsigned char*)(((long long)(int)(self + 0xc)) & 0xFFFFFFFFFFFFFFFFLL) -= data_0208ee44;` |
| `func_0203506c.c` | 22 | `func_0201a244` | read | unknown | `extern u8 data_0208ee44[];` |
| `func_0203506c.c` | 77 | `dScMB_c_InitResources` | write | unknown | `*(int *)&data_0208ee44 = 1;` |
| `func_ov001_020ab550.c` | 1 | `` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov001_020ab550.c` | 13 | `func_ov001_020ab550` | read | scene-specific | `*p = *p - data_0208ee44;` |
| `func_ov002_020f7780.c` | 3 | `` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov002_020f7780.c` | 23 | `func_ov002_020f7780` | write | scene-specific | `data_0208ee44 = 3;` |
| `func_ov003_020ada9c.cpp` | 20 | `func_ov003_020ad6ec` | read | scene-specific | `extern u8 data_0208ee44[];` |
| `func_ov003_020ada9c.cpp` | 62 | `LoadOBJPltt` | write | scene-specific | `*(int*)data_0208ee44 = 2;` |
| `func_ov003_020ae358.c` | 11 | `func_ov003_020adec0` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov003_020ae358.c` | 32 | `func_ov003_020ae358` | read | scene-specific | `*(unsigned char*)(c + 0x118) = (unsigned char)(data_0208ee44 * 6);` |
| `func_ov003_020ae358.c` | 55 | `func_ov003_020ae358` | read | scene-specific | `*(unsigned char*)(c + 0x118) = (unsigned char)(data_0208ee44 * 3);` |
| `func_ov003_020af038.cpp` | 26 | `m_1c` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov003_020af038.cpp` | 49 | `m_1c` | read | scene-specific | `*(unsigned char*)(((long long)(int)(c + 0x119)) & 0xFFFFFFFFFFFFFFFFLL) -= data_0208ee44;` |
| `func_ov003_020af038.cpp` | 71 | `m_1c` | read | scene-specific | `U8(0x118) = (unsigned char)(data_0208ee44 * 3);` |
| `func_ov003_020af038.cpp` | 76 | `m_1c` | read | scene-specific | `U8(0x118) = (unsigned char)(data_0208ee44 * 6);` |
| `func_ov003_020af038.cpp` | 104 | `m_1c` | read | scene-specific | `U8(0x117) = (unsigned char)(data_0208ee44 * 3);` |
| `func_ov003_020af038.cpp` | 158 | `m_1c` | read | scene-specific | `U8(0x117) = (unsigned char)(data_0208ee44 * 3);` |
| `func_ov003_020af8a0.c` | 66 | `_ZN8SaveData19IsCharacterUnlockedEj` | read | scene-specific | `extern s32 data_0208ee44;` |
| `func_ov003_020af8a0.c` | 423 | `func_ov003_020af8a0` | write | scene-specific | `data_0208ee44 = 1;` |
| `func_ov003_020b0894.c` | 19 | `_ZN5Sound22StopLoadedMusic_Layer1Ej` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov003_020b0894.c` | 63 | `func_ov003_020b0894` | read | scene-specific | `self[0x93] = (unsigned char)(data_0208ee44 << 3);` |
| `func_ov003_020b0894.c` | 66 | `func_ov003_020b0894` | read | scene-specific | `self[0x92] = (unsigned char)(data_0208ee44 << 4);` |
| `func_ov003_020b0894.c` | 85 | `func_ov003_020b0894` | read | scene-specific | `self[0x93] = (unsigned char)(data_0208ee44 << 3);` |
| `func_ov003_020b0894.c` | 88 | `func_ov003_020b0894` | read | scene-specific | `self[0x92] = (unsigned char)(data_0208ee44 << 4);` |
| `func_ov003_020b0894.c` | 95 | `func_ov003_020b0894` | read | scene-specific | `self[0x93] -= data_0208ee44;` |
| `func_ov003_020b0894.c` | 100 | `func_ov003_020b0894` | read | scene-specific | `self[0x92] -= data_0208ee44;` |
| `func_ov003_020b0b3c.c` | 30 | `_ZN3GXS10LoadBGPlttEPKvjj` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov003_020b0b3c.c` | 175 | `dScGameOver_c_InitResources` | write | scene-specific | `data_0208ee44 = 1;` |
| `func_ov004_020b0620.cpp` | 43 | `m70` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov004_020b0620.cpp` | 106 | `m70` | read | scene-specific | `_Z14ApproachLinearRiii((int *)(self + 0xac), *(int *)(self + 0xa8), data_0208ee44);` |
| `func_ov004_020b265c.c` | 32 | `func_ov004_020b0aa0` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov004_020b265c.c` | 75 | `func_ov004_020b265c` | write | scene-specific | `data_0208ee44 = 1;` |
| `func_ov005_020c1a20.c` | 36 | `_ZN3GXS11LoadOBJPlttEPKvjj` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov005_020c1a20.c` | 251 | `dScMiniGm_c_InitResources` | write | scene-specific | `data_0208ee44 = 1;` |
| `func_ov006_020d5384.cpp` | 77 | `func_ov006_020d3ba0` | read | scene-specific | `extern s32 data_0208ee44;` |
| `func_ov006_020d5384.cpp` | 100 | `func_ov006_020d3ba0` | write | scene-specific | `data_0208ee44 = 1;` |
| `func_ov006_020de704.c` | 20 | `func_ov004_020adc1c` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov006_020de704.c` | 70 | `dScMgCoin_c_InitResources` | write | scene-specific | `data_0208ee44 = 1;` |
| `func_ov006_020e0308.cpp` | 57 | `m18` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov006_020e0308.cpp` | 109 | `dScMgCup_c_InitResources` | write | scene-specific | `data_0208ee44 = 1;` |
| `func_ov006_020e7124.c` | 31 | `_ZN3GXS15SetGraphicsModeEi` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov006_020e7124.c` | 70 | `func_ov006_020e7124` | write | scene-specific | `data_0208ee44 = 1;` |
| `func_ov006_0210a708.c` | 32 | `func_ov004_020b0aa0` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov006_0210a708.c` | 72 | `func_ov006_0210a708` | write | scene-specific | `data_0208ee44 = 1;` |
| `func_ov006_0210bdb0.cpp` | 30 | `RandomIntInternal` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov006_0210bdb0.cpp` | 140 | `m48` | write | scene-specific | `data_0208ee44 = 1;` |
| `func_ov006_0210d1fc.cpp` | 35 | `_ZN3G2x13SetBlendAlphaEPVttttt` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov006_0210d1fc.cpp` | 174 | `m48` | write | scene-specific | `data_0208ee44 = 1;` |
| `func_ov006_0212551c.cpp` | 12 | `` | read | scene-specific | `extern int data_0208ee44[];` |
| `func_ov006_0212551c.cpp` | 45 | `dScMgBSC_c_InitResources` | read | scene-specific | `data_0208ee44[0] = 1;` |
| `func_ov006_02129268.c` | 31 | `func_ov006_02126a98` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov006_02129268.c` | 40 | `dScMgSnowball_c_InitResources` | write | scene-specific | `data_0208ee44 = 1;` |
| `func_ov006_0212b480.c` | 26 | `_ZN3GXS11LoadOBJPlttEPKvjj` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov006_0212b480.c` | 37 | `dScMgFlower_c_InitResources` | write | scene-specific | `data_0208ee44 = 1;` |
| `func_ov007_020cc4c0.cpp` | 26 | `LoadInitialGroup` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov007_020cc4c0.cpp` | 49 | `LoadInitialGroup` | write | scene-specific | `data_0208ee44 = 1;` |
| `func_ov075_02117b58.cpp` | 6 | `_ZN5Sound22StopLoadedMusic_Layer1Ej` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov075_02117b58.cpp` | 11 | `_ZN5Sound22StopLoadedMusic_Layer1Ej` | read | scene-specific | `*(int*)(((int)c + 0x264) & 0xFFFFFFFFFFFFFFFF) -= data_0208ee44;` |
| `func_ov075_02118a84.c` | 11 | `func_ov075_02118bf8` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov075_02118a84.c` | 42 | `func_ov075_02118a84` | read | scene-specific | `*p = *p - data_0208ee44;` |
| `func_ov075_021190a4.c` | 18 | `func_02020124` | read | scene-specific | `extern int data_0208ee44[];` |
| `func_ov075_021190a4.c` | 41 | `func_ov075_021190a4` | read | scene-specific | `*q = *q - data_0208ee44[0];` |
| `func_ov075_021194e4.cpp` | 21 | `RenderSub` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov075_021194e4.cpp` | 67 | `RenderSub` | read | scene-specific | `*(int*)(((long long)((int)c + 0x270)) & 0xFFFFFFFFFFFFFFFFLL) -= data_0208ee44;` |
| `func_ov075_021199d8.cpp` | 21 | `RenderSub` | read | scene-specific | `extern int data_0208ee44;` |
| `func_ov075_021199d8.cpp` | 68 | `RenderSub` | read | scene-specific | `data_0208ee44;` |
| `func_ov075_02119d78.c` | 2 | `func_ov075_0211a194` | read | scene-specific | `extern int data_0208ee44[];` |
| `func_ov075_02119d78.c` | 6 | `func_ov075_02119d78` | read | scene-specific | `*p = *p - data_0208ee44[0];` |
| `func_ov075_0211a410.cpp` | 50 | `_ZN5Sound22LoadAndSetMusic_Layer1Ei` | read | scene-specific | `extern s32 data_0208ee44;` |
| `func_ov075_0211a410.cpp` | 145 | `_ZN5Sound22LoadAndSetMusic_Layer1Ei` | write | scene-specific | `data_0208ee44 = 2;` |
