# Core fork and submodule publication

The smooth protocol modifies `melonDS-android-lib`, which is a submodule.

## Required repository topology

```text
ThorDS superproject
origin   joeblack2k/ThorDS
upstream SapphireRhodonite/melonDS-android

Core submodule
origin   joeblack2k/melonDS-android-lib
upstream SapphireRhodonite/melonDS-android-lib
```

## Procedure

1. Read the exact current submodule SHA.
2. Verify GitHub authentication is `joeblack2k`.
3. Inspect whether the fork exists.
4. Create it only when absent.
5. Branch from the exact pinned object.
6. Commit only protocol/core tests.
7. Push the core branch.
8. Change `.gitmodules` deliberately.
9. Update the submodule pointer.
10. Test a clean recursive clone.

## Never

- point the superproject at an unpushed object;
- use branch HEAD instead of the pinned object;
- force-push upstream history;
- combine unrelated melonDS changes.
