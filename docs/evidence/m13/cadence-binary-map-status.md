# Cadence Binary Map Status

Status: OPEN

The source candidate inventory is current for the local EU decomp sidecar.
The supplied binary mapper cannot run in the clean lead yet because it
requires these extracted files:

```text
extracted/arm9_dec.bin
extracted/overlays/overlay_0002.bin
```

The sidecar currently contains `extracted/arm9.bin` and many overlay images,
but not the required decoded ARM9 image and overlay 2 image.

This does not authorize guessed address mapping or a runtime patch. The
source-level inventory remains valid candidate evidence. The next source
action is semantic classification of the cadence consumers. Binary mapping
will resume when the exact extracted images are available locally.

No ROM, save, save state, or private device capture is stored here.

