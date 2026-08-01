# C4 — exact European runtime patch

## Inputs

- exact local EU ROM;
- decomp pin;
- current AM64DS runtime composition;
- C0 disassembly/liveness proof;
- protocol v1.

## Generated source

The injected/replacement code must live as checked-in ARM assembly source. Do not hand-maintain only opaque Action Replay words.

## Build

Use a discovered local ARM assembler, preferably NDK LLVM tools or `arm-none-eabi-*`.

Output:

```text
camera_patch.bin
camera_patch.disasm
camera_patch.ar.txt
patch_manifest.json
```

Generated binary files may remain build artifacts; source and public-safe manifests are committed.

## Manifest

Record:

- source pin;
- exact game identity;
- hook address/range;
- expected original words;
- generated replacement words;
- binary SHA-256;
- AR canonical SHA-256;
- protocol version;
- assembler version;
- branch targets;
- patch strategy.

## Runtime composition

The profile code must be:

- exact-match only;
- guarded;
- ordered after canonical analog movement;
- compatible with True Widescreen;
- disabled in Original/Safe Mode/Hardcore.

## Mismatch tests

Change one of:

- game code;
- revision;
- RA hash;
- expected instruction word;
- protocol version.

The patch must not activate.
