# Pose Relocation Audit

The first source-to-object inspection found a required local relocation:

```text
ModelAnim::UpdateVerts
  relocation: +0x18
  target: _Z20update_temporal_poseP9ModelAnim
```

The helper is a separate compiled function in the same translation unit. An
extractor that copies only `ModelAnim::UpdateVerts` produces an incomplete
payload. It must copy and relocate both functions together.

The public function also contains external relocations for the model update
methods and `func_0204531c`. The relocation builder must resolve each target
from the compiled object symbol table and reject every unresolved symbol.

The incomplete first builder was removed. No payload was generated or enabled.

