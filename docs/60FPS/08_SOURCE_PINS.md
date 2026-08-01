# Source pins

## ThorDS

```text
https://github.com/joeblack2k/ThorDS
reference ancestor 6eaf0df8cc435e3328aae248f8f5d5a5602f218b
```

## Core

```text
https://github.com/joeblack2k/melonDS-android-lib
reference gitlink 3c54a9c8b5e6b0a928487597ee33dcf110d01c4e
```

The live gitlink wins when newer.

## SM64DS decomp

Use the repository's current pinned checkout for product reproducibility.

Also create a gitignored read-only audit checkout at:

```text
https://github.com/tangosdev/sm64ds-decomp
commit 755f0be5b9658e5f75871c4138ddc0133a2c07c4
```

This newer audit reference provides broad/full source coverage. Compare the
timing functions before changing the project's source lock.

Required semantic files include:

```text
src/_ZN3IRQ13VBlankHandlerEv.c
src/func_02019100.c
src/func_02019144.c
src/func_02019390.c
src/func_02019404.c
src/func_020197b8.c
src/func_0201a4bc.c
src/func_020190b8.c
src/func_ov075_0211a410.cpp
src/func_ov075_0211a2b8.cpp
src/func_ov075_0211a26c.cpp
src/_ZN5Stage8BehaviorEv.cpp
src/_ZN5Stage6RenderEv.cpp
config/arm9/symbols.txt
config/arm9/overlays/ov075/symbols.txt
```

## Community reference

```text
YouTube ID: yJXEAIOFcNU
author attribution: gamemasterplc
known ROM: USA revision 1.1
```

Treat it as untrusted optional research.
