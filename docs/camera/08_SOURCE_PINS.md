# Source pins

## ThorDS

```text
Repository: https://github.com/joeblack2k/ThorDS
Known main: a7831c38c55e9eeef2376bb2390a99a108ab2bd0
Relevant input commit: e95699aae96d0d5a86bdf332a514650a9619b4f9
```

The live worktree wins over these reference SHAs.

## SM64DS decomp

```text
Repository: https://github.com/tangosdev/sm64ds-decomp
Commit: 2307f06d9ce10e114fa00d2e9318d5161aaed311
Region: Europe
```

Required files:

```text
src/func_0200bb28.c
src/_ZN5Stage16CheckCameraInputEv.cpp
src/_ZN5Stage10CheckInputEv.cpp
src/_ZN3HUD19RenderCameraButtonsEv.cpp
src/_ZN5Stage20RenderBouncingArrowsEv.cpp
src/func_02012790.c
src/_ZN6Camera8BehaviorEv.cpp
config/arm9/symbols.txt
config/arm9/overlays/ov002/symbols.txt
```

## AM64DS

```text
Repository: https://github.com/LRFLEW/AM64DS_DeSmuME
Branch: analog
Reference commit: d3ae02560c32c402672036677e06e0df6e692fd1
```

Required files:

```text
README.md
PATCHES.md
```

The canonical European runtime payload remains identified by:

```text
e68025c3aad3a47941ab2903dd9d212b91bafedff705ea6252677c27d07bdb1c
```

## melonDS Android core

Current superproject metadata refers to:

```text
https://github.com/SapphireRhodonite/melonDS-android-lib.git
branch GBARumble_PR
```

Luna must read the exact submodule SHA from the worktree. Never substitute a branch HEAD for the pinned submodule object.
