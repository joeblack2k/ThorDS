# Current state at package creation

## Public superproject

```text
Repository: https://github.com/joeblack2k/ThorDS
Reference:  6eaf0df8cc435e3328aae248f8f5d5a5602f218b
Message:    test: add smooth camera input mapping
```

This is an ancestor reference, not a reset target.

## Public core

```text
Repository: https://github.com/joeblack2k/melonDS-android-lib
Reference:  3c54a9c8b5e6b0a928487597ee33dcf110d01c4e
Branch:     thords/arm9-overclock-foundation
```

## Existing profile

The exact European Enhanced profile already includes:

```text
Analog code SHA-256:
e68025c3aad3a47941ab2903dd9d212b91bafedff705ea6252677c27d07bdb1c

True Widescreen code SHA-256:
28445a89a887a556b4a0564e21f8ca579eeab437471bff1b38c681efd6a3bbc6
```

No 60 FPS enhancement is in the public profile.

## Existing 60 FPS groundwork

The public code already contains:

- `getSm64dsGameLoopTelemetryJson()`;
- `sampleSm64dsGameLoopCounter()`;
- one-second native windows;
- game-loop, cadence and stage-timer fields;
- ARM9 overclock telemetry.

Known limitations:

- main-loop counter is reset during scene initialization;
- reset handling was fixed;
- Castle Garden Original and Enhanced both showed ~60/61 loop samples;
- `stageTimer` was zero there;
- branch-helper Stage probes stayed zero;
- no product patch exists;
- M13 remains open.

## Preserve camera work

The latest public commits began the Smooth Orbit implementation. This 60 FPS
pass must build around it, not replace or revert it.
