# Reproducible patch build tooling

## Directory

```text
tools/thords/camera/
```

## Build stages

1. Assemble ARM source.
2. Extract raw `.text`.
3. Disassemble generated code.
4. Verify alignment and branch targets.
5. Verify original EU hook words from the local ROM.
6. Emit guarded Action Replay words.
7. Canonicalize line endings and trailing newline.
8. Calculate SHA-256.
9. Update profile and manifest.
10. Run parser/profile tests.

## Suggested commands

Discover tools rather than hardcoding the host prebuilt folder.

```bash
clang --target=armv5te-none-eabi -march=armv5te -marm \
  -c sm64ds_eu_smooth_camera.s -o camera.o

llvm-objcopy -O binary --only-section=.text camera.o camera.bin
llvm-objdump -d camera.o > camera.disasm
```

## Generator requirements

`generate_ar.py` must:

- accept explicit hook address/range;
- accept expected original words;
- reject unaligned or oversized payloads;
- reject out-of-range branch targets;
- emit deterministic uppercase hex;
- include a final newline;
- print hashes and metadata, never ROM bytes.

## Verification

A second independent parser must decode the generated AR code and reconstruct the intended writes before the profile is accepted.
