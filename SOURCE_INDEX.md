# Bronnenindex

Alle bronnen zijn gecontroleerd of vastgelegd op **31 juli 2026**. Luna moet relevante bronnen opnieuw lokaal inspecteren na de repositorybootstrap en exacte commits in `docs/project/SOURCE_LOCK.md` vastleggen.

## Primaire bronrepositories

### MelonDualDS

- Repository: https://github.com/SapphireRhodonite/melonDS-android
- Basistag: https://github.com/SapphireRhodonite/melonDS-android/releases/tag/0.7.0.rc5
- Tagcommit: https://github.com/SapphireRhodonite/melonDS-android/commit/9b28076281545a1e08dccee0b3f925febb8933ac
- Thor mapper:
  https://github.com/SapphireRhodonite/melonDS-android/blob/0.7.0.rc5/app/src/main/java/me/magnum/melonds/impl/layout/devicemapper/AynThorLayoutDisplayMapper.kt
- Slot-2 config:
  https://github.com/SapphireRhodonite/melonDS-android/blob/0.7.0.rc5/app/src/main/java/me/magnum/melonds/domain/model/rom/config/RomGbaSlotConfig.kt
- Vulkan compositor:
  https://github.com/SapphireRhodonite/melonDS-android/blob/0.7.0.rc5/app/src/main/cpp/renderer/VulkanCompositorShader.comp
- Vulkan presenter:
  https://github.com/SapphireRhodonite/melonDS-android/blob/0.7.0.rc5/app/src/main/cpp/renderer/VulkanSurfacePresenter.frag

### AM64DS

- Repository/branch: https://github.com/LRFLEW/AM64DS_DeSmuME/tree/analog
- Onderzoekspin: `d3ae02560c32c402672036677e06e0df6e692fd1`
- Patchcodes: https://github.com/LRFLEW/AM64DS_DeSmuME/blob/analog/PATCHES.md
- Slot-2-implementatie:
  https://github.com/LRFLEW/AM64DS_DeSmuME/blob/analog/desmume/src/addons/slot2_analog.cpp

### SM64DS-decomp

- Repository: https://github.com/tangosdev/sm64ds-decomp
- Onderzoekspin: `2d38fe9b825199deec408240849b64b91c965d85`
- Clipper/aspectcode:
  https://github.com/tangosdev/sm64ds-decomp/blob/main/src/_ZN7ClipperC1Ev.c
- 3D-init:
  https://github.com/tangosdev/sm64ds-decomp/blob/main/src/Initialise3dGraphics.cpp

### melonDS core

- Repository: https://github.com/melonDS-emu/melonDS
- Gebruik in implementatie de submodulecommit die exact door rc5 wordt vastgelegd.
- Timingstructuur:
  https://github.com/melonDS-emu/melonDS/blob/master/src/NDS.h
- Scheduler:
  https://github.com/melonDS-emu/melonDS/blob/master/src/NDS.cpp

## Praktijkbewijs

- AYN Thor Redditprototype:
  https://www.reddit.com/r/AynThor/comments/1sxe0px/mario_64_ds_analog_mod_widescreen_mod_60fps_mod/
- Dit is bewijs dat analog + widescreen + dual screen praktisch werken, maar ook bewijs dat de gevonden 60fps-patch slowdown kan veroorzaken.

## RetroAchievements

- SM64DS gamepagina:
  https://retroachievements.org/game/9983
- Ondersteunde hashes:
  https://retroachievements.org/game/9983/hashes
- Emulatorondersteuning:
  https://docs.retroachievements.org/general/emulator-support-and-issues.html
- Hardcore-eisen:
  https://docs.retroachievements.org/general/hardcore-compliance-requirements.html

## Toekomstige Zelda-profielen

- Phantom Hourglass D-padpatch:
  https://github.com/StraDaMa/Legend-of-Zelda-Phantom-Hourglass-D-Pad-Patch
- Phantom Hourglass decomp:
  https://github.com/zeldaret/ph
- Spirit Tracks decomp:
  https://github.com/zeldaret/st

## Bronkwaliteit

Gebruik deze prioriteit:

1. vastgepinde sourcecode en lokale build;
2. officiële RetroAchievements-documentatie;
3. originele patchrepository/source;
4. fysieke Thor-metingen;
5. communityposts als praktijkbewijs;
6. video’s en losse binaries alleen als onderzoekssignaal, nooit als onbegrepen productbron.
