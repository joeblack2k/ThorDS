# Source evidence index

## ThorDS

- Repository: `https://github.com/joeblack2k/ThorDS`
- Reference: `6eaf0df8cc435e3328aae248f8f5d5a5602f218b`
- Exact profile contains analog and True Widescreen but no 60 FPS enhancement.
- Current native sampler reads `0x020A0DB0`, `0x0208EE44` and `0x0209F304`.
- Current status records M13 implementation/validation open.

## Core

- Repository: `https://github.com/joeblack2k/melonDS-android-lib`
- Reference: `3c54a9c8b5e6b0a928487597ee33dcf110d01c4e`
- JIT dispatch is in `ARMv5::Execute`.
- ARM9 overclock telemetry foundation is present.

## Decomp

- Audit reference: `755f0be5b9658e5f75871c4138ddc0133a2c07c4`
- VBlank handler and main-loop functions are source-covered.
- `data_0208EE44` has many main/overlay consumers.

## Supplied chat log

The log records:

- counter-reset fix;
- stable 60/61 loop windows;
- Original/Enhanced equality;
- inactive Stage timer;
- failed branch/JIT helper probes;
- intended JIT block-entry continuation;
- hard requirement decision.

## Public behavioral references

- TASVideos SM64DS resource page:
  `https://tasvideos.org/GameResources/DS/SuperMario64DS`
- AYN Thor community trial:
  `https://www.reddit.com/r/AynThor/comments/1sxe0px/`
- 3DS community guide:
  `https://www.reddit.com/r/3dshomebrew/comments/1au3w8p/`
- community video:
  `https://www.youtube.com/watch?v=yJXEAIOFcNU`
