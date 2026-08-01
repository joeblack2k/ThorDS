# F0 — baseline

## Record

- live superproject SHA;
- live core SHA;
- current decomp source lock;
- current APK SHA;
- current ROM identity;
- current profile/effective options;
- current ARM9 ratio;
- current Thor firmware/display state.

## Scene baselines

Capture text/JSON telemetry in:

```text
title
star select
castle grounds
Bob-omb Battlefield spawn
Chain Chomp
```

At least one scene must demonstrate Original ~30 semantic updates before any
60 FPS implementation can pass.

## Preserve

Do not overwrite private saves/states. Create clearly named private test
fixtures outside Git.
