# Finish definition

## Full target

The pass is complete only when the exact European SM64DS profile provides a reliable Thor experience with all of the following.

### Analog

- continuous magnitude and direction reach the game through the real profile path;
- right-stick camera works without stuck or conflicting D-pad state;
- Original disables the analog runtime code and Slot-2 addon cleanly;
- relaunch and controller reconnect preserve correct behavior.

### True Widescreen

- 3D world uses the full top display and shows extra horizontal FOV;
- 3D objects retain local shape;
- 2D HUD, text, icons and menus remain within 2% of the 4:3 reference geometry;
- lower screen remains 4:3 and touch-correct;
- unsafe scenes transition cleanly to 4:3 fallback;
- the option is part of the exact profile and no longer a developer-only launch extra.

### ARM9 overclock

- 100% is equivalent to the baseline;
- at least 125% is genuinely effective in the native core while DS wall time remains normal;
- audio, ARM7, IPC, GPU, RTC and timers remain correct;
- only individually passing ratios are exposed;
- save-state ratio mismatch is handled safely;
- Hardcore forces 100%.

### Enhanced UI

- exact SM64DS details expose Analog, True Widescreen, validated ARM9 ratios, 60fps and RA mode;
- current/effective state and relaunch requirements are truthful;
- one-click Play uses the resolved plan;
- general melonDS functionality and non-Thor fallback remain available.

### RetroAchievements

- Off starts without RA bootstrap;
- Casual works with enhancements and original EU identity;
- Hardcore + Enhanced fails closed and offers Original + restart;
- Hardcore Original blocks incompatible features;
- own User-Agent is used without credentials in logs;
- normal set load and a normal gameplay-triggered unlock are validated when an authenticated account is already available.

### 60fps

- 60 unique SM64DS gameplay updates per second, not duplicated presentation frames;
- normal wall-clock speed, physics, animation, timers and audio;
- no known slow-motion regressions in stress scenes;
- works with Analog and True Widescreen at a validated ARM9 ratio;
- remains a user toggle and defaults off.

### Release

- 60-minute combined-feature soak;
- save/relaunch and sleep/resume;
- clean source/history scan;
- full build and tests;
- final APK hash and final report;
- no false or partial green labels.
