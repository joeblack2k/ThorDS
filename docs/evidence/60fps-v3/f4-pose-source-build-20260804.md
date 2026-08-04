# F4 Player Pose Source Build

Status: PASS for source and payload reproducibility only

## Inputs

- exact EU decomp: `docs/sm64ds-decomp`;
- compiler: pinned `2004/b56` `mwccarm`;
- launcher: Wine;
- checked-in source fragments:
  - `Player_AdvanceAnims.c`;
  - `ModelAnim_UpdateVerts.cpp`;
  - `BlendModelAnim_UpdateVerts.cpp`.

The source verifier matched the checked-in fragments against the designated
decomp `mods` files.

## Generated result

The checked-in relocation-aware builder completed successfully:

```text
payload_bytes=548
generated_lines=143
payload_sha256=7dbdad0f49cafa72691d8fa8c0fe06f7f4afe90dab016b2f4feb5e2460d80a19
```

The builder resolved local and external relocations. It did not copy code
words from an APK or an old profile.

## Product status

The generated pose payload is not enabled. F4 remains open because source
reproducibility does not prove half-step frame values, transform output
changes, interpolation execution, or visible player-pose improvement.
