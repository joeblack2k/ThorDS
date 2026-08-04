# F8 Build, Install, and Two-Screen Runtime Check

Status: PASS for the bounded runtime flow

## Build

The required commands passed:

```text
:app:testGitHubProdReleaseUnitTest
:app:assembleGitHubProdDebug
```

The debug APK was installed on the AYN Thor device.

## Two-screen flow

Before navigation input:

- upper display: SM64DS title screen with `Touch To Start`;
- lower display: SM64DS touch menu with `Adventure`.

The autonomous route then completed:

1. lower `Adventure` touch;
2. lower `File A` selection;
3. upper Yoshi gameplay;
4. lower castle map;
5. direct `B` input.

## Runtime result

- exact EU ASMP revision 0 ROM;
- Vulkan renderer;
- developer cadence probe disabled;
- normal Enhanced windows: `29-30` semantic updates/s;
- no `FATAL EXCEPTION`;
- no `ANR in` entry.

This is a build, installation, two-screen flow, and basic gameplay check. It
does not prove the final 60 FPS product gate.
