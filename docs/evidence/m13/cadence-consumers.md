# Cadence Consumer Inventory

Symbol: `data_0208ee44`
Source tree SHA-256: `d7157768930c35f83d064bcc2baf74057a759768832e833165cc4218787dd524`
Findings: `194`

This is a source inventory, not a complete semantic classification. The
scanner reports candidate categories only. Fixed-step and unknown consumers
remain open for F2 and keep the product gate red.

| File | Line | Function | Kind | Category | Source |
|---|---:|---|---|---|---|
| `src/_ZN3HUD13UpdateVsTimerEv.cpp` | 8 | `` | read | message/HUD | `extern int data_0208ee44;` |
| `src/_ZN3HUD13UpdateVsTimerEv.cpp` | 29 | `HUD::UpdateVsTimer` | read | message/HUD | `data_ov002_02111188 = data_ov002_02111188 - data_0208ee44;` |
| `src/_ZN3HUD17UpdateHealthMeterEv.cpp` | 17 | `_ZN6Player7IsInAirEv` | read | message/HUD | `extern int data_0208ee44;` |
| `src/_ZN3HUD17UpdateHealthMeterEv.cpp` | 29 | `HUD::UpdateHealthMeter` | read | message/HUD | `*(unsigned short *)((((int)((char *)this)) + 0x6a)) -= data_0208ee44;` |
| `src/_ZN3HUD17UpdateHealthMeterEv.cpp` | 31 | `HUD::UpdateHealthMeter` | read | message/HUD | `*(unsigned short *)((((int)((char *)this)) + 0x6c)) -= data_0208ee44;` |
| `src/_ZN3HUD19RenderCameraButtonsEv.cpp` | 35 | `RenderCameraButtons` | read | message/HUD | `extern int data_0208ee44;` |
| `src/_ZN3HUD19RenderCameraButtonsEv.cpp` | 49 | `HUD::RenderCameraButtons` | read | message/HUD | `data_ov002_02111180 = t - data_0208ee44;` |
| `src/_ZN3IRQ13VBlankHandlerEv.c` | 3 | `` | read | scheduler | `extern int data_0208ee44;` |
| `src/_ZN3IRQ13VBlankHandlerEv.c` | 15 | `_ZN3IRQ13VBlankHandlerEv` | read | scheduler | `if ((data_0209d514 >= data_0208ee44) && (data_0209d4f0 != 0))` |
| `src/_ZN5Stage13InitResourcesEv.cpp` | 65 | `InitResources` | read | scene-update | `extern s32 data_0208ee44;` |
| `src/_ZN5Stage13InitResourcesEv.cpp` | 362 | `if` | write | scene-update | `data_0208ee44 = 2;` |
| `src/_ZN5Stage13UpdateMessageEv.cpp` | 8 | `` | read | message/HUD | `extern s32 data_0208ee44;` |
| `src/_ZN5Stage13UpdateMessageEv.cpp` | 41 | `Stage::UpdateMessage` | read | message/HUD | `data_0209d67c -= data_0208ee44;` |
| `src/_ZN5Stage13UpdateMessageEv.cpp` | 50 | `Stage::UpdateMessage` | read | message/HUD | `data_0209d67c -= data_0208ee44;` |
| `src/_ZN5Stage19RenderVsModeNewStarEv.cpp` | 6 | `` | read | render/OAM | `extern int data_0208ee44;` |
| `src/_ZN5Stage19RenderVsModeNewStarEv.cpp` | 25 | `func_02012790` | read | render/OAM | `data_0209f308 = timer - data_0208ee44;` |
| `src/_ZN5Stage20RenderBouncingArrowsEv.cpp` | 5 | `` | read | render/OAM | `extern int data_0208ee44;` |
| `src/_ZN5Stage20RenderBouncingArrowsEv.cpp` | 17 | `_ZN5Stage20RenderBouncingArrowsEv` | read | render/OAM | `if (data_0208ee44 == 1) {` |
| `src/_ZN5Stage8BehaviorEv.cpp` | 29 | `IsLevelTinyHugeIslandOutside` | read | scene-update | `extern s32 data_0208ee44;` |
| `src/_ZN5Stage8BehaviorEv.cpp` | 158 | `Stage::Behavior` | read | scene-update | `data_0209f304 = data_0209f304 - data_0208ee44;` |
| `src/_ZN5Stage9LC_RenderEv.cpp` | 6 | `` | read | render/OAM | `extern int data_0208ee44;` |
| `src/_ZN5Stage9LC_RenderEv.cpp` | 31 | `_ZN5Stage20RenderBouncingArrowsEv` | read | render/OAM | `data_0209f2a8 = data_0209f2a8 + data_0208ee44;` |
| `src/_ZN5Stage9LC_UpdateEv.cpp` | 24 | `_ZN5Sound22StopLoadedMusic_Layer1Ej` | read | scene-update | `extern int data_0208ee44;` |
| `src/_ZN5Stage9LC_UpdateEv.cpp` | 61 | `Stage::LC_Update` | read | scene-update | `data_0209f244 = data_0209f244 - data_0208ee44;` |
| `src/_ZN5Stage9LC_UpdateEv.cpp` | 66 | `Stage::LC_Update` | read | scene-update | `data_0209f22c = data_0209f22c - data_0208ee44;` |
| `src/_ZN5Stage9LC_UpdateEv.cpp` | 182 | `Stage::LC_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9LC_UpdateEv.cpp` | 185 | `Stage::LC_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9LC_UpdateEv.cpp` | 203 | `Stage::LC_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9LC_UpdateEv.cpp` | 206 | `Stage::LC_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9LC_UpdateEv.cpp` | 225 | `Stage::LC_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9LC_UpdateEv.cpp` | 228 | `Stage::LC_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9LC_UpdateEv.cpp` | 251 | `Stage::LC_Update` | read | scene-update | `data_0209f2b8 = data_0209f2b8 - data_0208ee44;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 63 | `_ZN5Sound22StopLoadedMusic_Layer1Ej` | read | scene-update | `extern int data_0208ee44;          /* frame tick */` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 132 | `Stage::PS_Update` | read | scene-update | `data_0209f210 = data_0209f210 - data_0208ee44;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 137 | `Stage::PS_Update` | read | scene-update | `data_0209f2e4 = data_0209f2e4 - data_0208ee44;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 142 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0209f244 - data_0208ee44;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 160 | `Stage::PS_Update` | read | scene-update | `data_0209f2cc = data_0209f2cc - data_0208ee44;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 165 | `Stage::PS_Update` | read | scene-update | `data_0209f23c = data_0209f23c - data_0208ee44;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 170 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0209f22c - data_0208ee44;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 249 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 251 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 270 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 273 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 291 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 294 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 312 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 314 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 368 | `Stage::PS_Update` | read | scene-update | `*p = (u8)(data_0208ee44 * 3);` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 385 | `Stage::PS_Update` | read | scene-update | `data_0209f210 = (u8)(data_0208ee44 * 3);` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 455 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 458 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 476 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 479 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 497 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 500 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 519 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 522 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 577 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 580 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 598 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 601 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 619 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 622 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 736 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 751 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 779 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 793 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 821 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 835 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 896 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 898 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 986 | `Stage::PS_Update` | read | scene-update | `data_0209f210 = data_0208ee44 * 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 1048 | `Stage::PS_Update` | read | scene-update | `data_0209f210 = data_0208ee44 * 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 1094 | `Stage::PS_Update` | read | scene-update | `data_0209f2cc = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 1129 | `Stage::PS_Update` | read | scene-update | `data_0209f2cc = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 1170 | `Stage::PS_Update` | read | scene-update | `data_0209f2cc = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 1215 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 1218 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 1244 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 1286 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 1289 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 1307 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 1310 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 1329 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 1332 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 1355 | `Stage::PS_Update` | read | scene-update | `data_0209f2b8 = data_0209f2b8 - data_0208ee44;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 1403 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 1405 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 1425 | `Stage::PS_Update` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9PS_UpdateEv.cpp` | 1427 | `Stage::PS_Update` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9VE_UpdateEv.cpp` | 8 | `` | read | scene-update | `extern int data_0208ee44;` |
| `src/_ZN5Stage9VE_UpdateEv.cpp` | 28 | `_ZN5Stage9VE_UpdateEv` | read | scene-update | `data_0209f244 = t - data_0208ee44;` |
| `src/_ZN5Stage9VE_UpdateEv.cpp` | 35 | `_ZN5Stage9VE_UpdateEv` | read | scene-update | `data_0209f22c = t2 - data_0208ee44;` |
| `src/_ZN5Stage9VE_UpdateEv.cpp` | 59 | `_ZN5Stage9VE_UpdateEv` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9VE_UpdateEv.cpp` | 62 | `_ZN5Stage9VE_UpdateEv` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN5Stage9VE_UpdateEv.cpp` | 75 | `_ZN5Stage9VE_UpdateEv` | read | scene-update | `data_0209f244 = data_0208ee44 << 2;` |
| `src/_ZN5Stage9VE_UpdateEv.cpp` | 79 | `_ZN5Stage9VE_UpdateEv` | read | scene-update | `data_0209f22c = data_0208ee44 << 3;` |
| `src/_ZN7Message6UpdateEv.cpp` | 27 | `` | read | message/HUD | `extern s32 data_0208ee44;` |
| `src/_ZN7Message6UpdateEv.cpp` | 116 | `Message::Update` | read | message/HUD | `if (data_020a0db0 & (0x10 / data_0208ee44)) {` |
| `src/_ZN7Message6UpdateEv.cpp` | 150 | `Message::Update` | read | message/HUD | `if (data_020a0db0 & (0x10 / data_0208ee44)) {` |
| `src/_ZN7Message6UpdateEv.cpp` | 230 | `Message::Update` | read | message/HUD | `if (data_020a0db0 & (0x10 / data_0208ee44)) {` |
| `src/_ZN7Message6UpdateEv.cpp` | 263 | `Message::Update` | read | message/HUD | `if (!(data_020a0db0 & (0x10 / data_0208ee44))) {` |
| `src/_ZN7Minimap6RenderEv.cpp` | 72 | `Render` | read | render/OAM | `extern "C" int data_0208ee44;` |
| `src/_ZN7Minimap6RenderEv.cpp` | 173 | `Minimap::Render` | read | render/OAM | `*(u8 *)(((int)((char*)this + i) + 0x22e)) += data_0208ee44;` |
| `src/_ZN7Minimap8BehaviorEv.cpp` | 76 | `` | read | render/OAM | `extern s32  data_0208ee44;` |
| `src/_ZN7Minimap8BehaviorEv.cpp` | 146 | `_ZN7Minimap8BehaviorEv` | read | render/OAM | `F254 -= data_0208ee44;` |
| `src/func_02005418.c` | 17 | `` | read | scene-update | `extern int data_0208ee44;` |
| `src/func_02005418.c` | 42 | `func_02005418` | read | scene-update | `r4 = data_0208ee44;` |
| `src/func_02005a58.c` | 24 | `func_0201a244` | read | boot/init | `extern char data_0208ee44;` |
| `src/func_02005a58.c` | 88 | `func_02005a58` | read | boot/init | `*(int*)(&data_0208ee44) = 1;` |
| `src/func_020199a4.c` | 9 | `func_020132d8` | read | scheduler | `extern int data_0208ee44;` |
| `src/func_020199a4.c` | 24 | `func_020199a4` | read | scheduler | `int dt = data_0208ee44;` |
| `src/func_02019ac4.c` | 16 | `func_02011db4` | read | timer | `extern int data_0208ee44;` |
| `src/func_02019ac4.c` | 39 | `func_02019ac4` | read | timer | `delta = data_0208ee44;` |
| `src/func_02020768.c` | 4 | `func_0202043c` | read | timer | `extern int data_0208ee44;` |
| `src/func_02020768.c` | 15 | `func_02020768` | read | timer | `*acc += data_0208ee44;` |
| `src/func_020326ac.c` | 19 | `` | read | message/HUD | `extern s32 data_0208ee44;` |
| `src/func_020326ac.c` | 90 | `func_020326ac` | read | message/HUD | `if (data_020a0db0 & (0x10 / data_0208ee44)) {` |
| `src/func_020326ac.c` | 117 | `func_020326ac` | read | message/HUD | `if (data_020a0db0 & (0x10 / data_0208ee44)) {` |
| `src/func_020326ac.c` | 192 | `func_020326ac` | read | message/HUD | `if (data_020a0db0 & (0x10 / data_0208ee44)) {` |
| `src/func_020326ac.c` | 213 | `func_020326ac` | read | message/HUD | `if (!(data_020a0db0 & (0x10 / data_0208ee44))) {` |
| `src/func_02034b40.c` | 14 | `_ZN3OAM4LoadEv` | read | render/OAM | `extern int data_0208ee44;` |
| `src/func_02034b40.c` | 58 | `func_02034b40` | read | render/OAM | `*(int*)(((long long)(int)(self + 8))) += (data_0208ee44 << 10);` |
| `src/func_02034b40.c` | 65 | `func_02034b40` | read | render/OAM | `*(unsigned char*)(((long long)(int)(self + 0xc))) -= data_0208ee44;` |
| `src/func_0203506c.c` | 18 | `func_0201a244` | read | boot/init | `extern u8 data_0208ee44[];` |
| `src/func_0203506c.c` | 73 | `func_0203506c` | write | boot/init | `*(int *)&data_0208ee44 = 1;` |
| `src/func_ov001_020ab550.c` | 1 | `` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov001_020ab550.c` | 13 | `func_ov001_020ab550` | read | scene-specific | `*p = *p - data_0208ee44;` |
| `src/func_ov002_020f7780.c` | 3 | `` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov002_020f7780.c` | 23 | `func_ov002_020f7780` | write | scene-specific | `data_0208ee44 = 3;` |
| `src/func_ov003_020ada9c.cpp` | 17 | `func_ov003_020ad6ec` | read | scene-specific | `extern u8 data_0208ee44[];` |
| `src/func_ov003_020ada9c.cpp` | 59 | `LoadOBJPltt` | write | scene-specific | `*(int*)data_0208ee44 = 2;` |
| `src/func_ov003_020ae358.c` | 11 | `func_ov003_020adec0` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov003_020ae358.c` | 32 | `func_ov003_020ae358` | read | scene-specific | `*(unsigned char*)(c + 0x118) = (unsigned char)(data_0208ee44 * 6);` |
| `src/func_ov003_020ae358.c` | 55 | `func_ov003_020ae358` | read | scene-specific | `*(unsigned char*)(c + 0x118) = (unsigned char)(data_0208ee44 * 3);` |
| `src/func_ov003_020af038.cpp` | 26 | `m_1c` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov003_020af038.cpp` | 49 | `m_1c` | read | scene-specific | `*(unsigned char*)(((long long)(int)(c + 0x119)) & 0xFFFFFFFFFFFFFFFFLL) -= data_0208ee44;` |
| `src/func_ov003_020af038.cpp` | 71 | `m_1c` | read | scene-specific | `U8(0x118) = (unsigned char)(data_0208ee44 * 3);` |
| `src/func_ov003_020af038.cpp` | 76 | `m_1c` | read | scene-specific | `U8(0x118) = (unsigned char)(data_0208ee44 * 6);` |
| `src/func_ov003_020af038.cpp` | 104 | `m_1c` | read | scene-specific | `U8(0x117) = (unsigned char)(data_0208ee44 * 3);` |
| `src/func_ov003_020af038.cpp` | 158 | `m_1c` | read | scene-specific | `U8(0x117) = (unsigned char)(data_0208ee44 * 3);` |
| `src/func_ov003_020af8a0.c` | 66 | `_ZN8SaveData19IsCharacterUnlockedEj` | read | scene-specific | `extern s32 data_0208ee44;` |
| `src/func_ov003_020af8a0.c` | 423 | `func_ov003_020af8a0` | write | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov003_020b0894.c` | 19 | `_ZN5Sound22StopLoadedMusic_Layer1Ej` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov003_020b0894.c` | 63 | `func_ov003_020b0894` | read | scene-specific | `self[0x93] = (unsigned char)(data_0208ee44 << 3);` |
| `src/func_ov003_020b0894.c` | 66 | `func_ov003_020b0894` | read | scene-specific | `self[0x92] = (unsigned char)(data_0208ee44 << 4);` |
| `src/func_ov003_020b0894.c` | 85 | `func_ov003_020b0894` | read | scene-specific | `self[0x93] = (unsigned char)(data_0208ee44 << 3);` |
| `src/func_ov003_020b0894.c` | 88 | `func_ov003_020b0894` | read | scene-specific | `self[0x92] = (unsigned char)(data_0208ee44 << 4);` |
| `src/func_ov003_020b0894.c` | 95 | `func_ov003_020b0894` | read | scene-specific | `self[0x93] -= data_0208ee44;` |
| `src/func_ov003_020b0894.c` | 100 | `func_ov003_020b0894` | read | scene-specific | `self[0x92] -= data_0208ee44;` |
| `src/func_ov003_020b0b3c.c` | 27 | `_ZN3GXS10LoadBGPlttEPKvjj` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov003_020b0b3c.c` | 172 | `func_ov003_020b0b3c` | write | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov004_020b0620.cpp` | 43 | `m70` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov004_020b0620.cpp` | 106 | `m70` | read | scene-specific | `_Z14ApproachLinearRiii((int *)(self + 0xac), *(int *)(self + 0xa8), data_0208ee44);` |
| `src/func_ov004_020b265c.c` | 26 | `FreeGfxSlotsById` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov004_020b265c.c` | 69 | `func_ov004_020b265c` | write | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov005_020c1a20.c` | 33 | `_ZN3GXS11LoadOBJPlttEPKvjj` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov005_020c1a20.c` | 248 | `func_ov005_020c1a20` | write | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov006_020d5384.cpp` | 72 | `func_ov006_020d3ba0` | read | scene-specific | `extern s32 data_0208ee44;` |
| `src/func_ov006_020d5384.cpp` | 95 | `func_ov006_020d3ba0` | write | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov006_020de704.c` | 17 | `func_ov004_020adc1c` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov006_020de704.c` | 67 | `func_ov006_020de704` | write | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov006_020e0308.cpp` | 55 | `m18` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov006_020e0308.cpp` | 107 | `func_ov006_020e0308` | write | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov006_020e7124.c` | 27 | `_ZN3GXS15SetGraphicsModeEi` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov006_020e7124.c` | 66 | `func_ov006_020e7124` | write | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov006_0210a708.c` | 27 | `FreeGfxSlotsById` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov006_0210a708.c` | 67 | `func_ov006_0210a708` | write | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov006_0210bdb0.cpp` | 27 | `RandomIntInternal` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov006_0210bdb0.cpp` | 137 | `m48` | write | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov006_0210d1fc.cpp` | 32 | `_ZN3G2x13SetBlendAlphaEPVttttt` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov006_0210d1fc.cpp` | 171 | `m48` | write | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov006_0212551c.cpp` | 11 | `` | read | scene-specific | `extern int data_0208ee44[];` |
| `src/func_ov006_0212551c.cpp` | 44 | `func_ov006_0212551c` | read | scene-specific | `data_0208ee44[0] = 1;` |
| `src/func_ov006_02129268.c` | 26 | `func_ov006_02126a98` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov006_02129268.c` | 35 | `func_ov006_02129268` | write | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov006_0212b480.c` | 23 | `_ZN3GXS11LoadOBJPlttEPKvjj` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov006_0212b480.c` | 34 | `func_ov006_0212b480` | write | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov007_020cc4c0.cpp` | 26 | `LoadInitialGroup` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov007_020cc4c0.cpp` | 49 | `LoadInitialGroup` | write | scene-specific | `data_0208ee44 = 1;` |
| `src/func_ov075_02117b58.cpp` | 6 | `_ZN5Sound22StopLoadedMusic_Layer1Ej` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov075_02117b58.cpp` | 11 | `_ZN5Sound22StopLoadedMusic_Layer1Ej` | read | scene-specific | `*(int*)(((int)c + 0x264)) -= data_0208ee44;` |
| `src/func_ov075_02118a84.c` | 11 | `func_ov075_02118bf8` | read | scene-specific | `extern int data_0208ee44;` |
| `src/func_ov075_02118a84.c` | 42 | `func_ov075_02118a84` | read | scene-specific | `*p = *p - data_0208ee44;` |
| `src/func_ov075_021190a4.c` | 18 | `func_02020124` | read | scene-specific | `extern int data_0208ee44[];` |
| `src/func_ov075_021190a4.c` | 41 | `func_ov075_021190a4` | read | scene-specific | `*q = *q - data_0208ee44[0];` |
| `src/func_ov075_021194e4.cpp` | 21 | `RenderSub` | read | render/OAM | `extern int data_0208ee44;` |
| `src/func_ov075_021194e4.cpp` | 67 | `RenderSub` | read | render/OAM | `*(int*)(((long long)((int)c + 0x270))) -= data_0208ee44;` |
| `src/func_ov075_021199d8.cpp` | 21 | `RenderSub` | read | render/OAM | `extern int data_0208ee44;` |
| `src/func_ov075_021199d8.cpp` | 68 | `RenderSub` | read | render/OAM | `data_0208ee44;` |
| `src/func_ov075_02119d78.c` | 2 | `func_ov075_0211a194` | read | scene-specific | `extern int data_0208ee44[];` |
| `src/func_ov075_02119d78.c` | 6 | `func_ov075_02119d78` | read | scene-specific | `*p = *p - data_0208ee44[0];` |
| `src/func_ov075_0211a410.cpp` | 45 | `_ZN5Sound22LoadAndSetMusic_Layer1Ei` | read | scene-specific | `extern s32 data_0208ee44;` |
| `src/func_ov075_0211a410.cpp` | 140 | `_ZN5Sound22LoadAndSetMusic_Layer1Ei` | write | scene-specific | `data_0208ee44 = 2;` |
