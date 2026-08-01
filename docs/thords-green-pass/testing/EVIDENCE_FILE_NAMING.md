# Evidence file naming

Use stable, descriptive names:

```text
docs/evidence/m6/analog-end-to-end.json
docs/evidence/m7/w01-reference-geometry.json
docs/evidence/m7/w03-side-landmark.csv
docs/evidence/m7/w04-hud-circle.json
docs/evidence/m7/w05-glyphs.json
docs/evidence/m7/w06-physical-bottom.json
docs/evidence/m7/w20-internal-transition.csv
docs/evidence/m8/product-scene-matrix.json
docs/evidence/m10/telemetry-100.csv
docs/evidence/m10/telemetry-ratios.csv
docs/evidence/m11/enhanced-ui.txt
docs/evidence/m9/ra-device-matrix.txt
docs/evidence/m13/unique-updates.csv
docs/evidence/release/final-soak.json
```

Each evidence file starts with:

```text
Date
Commit
APK SHA-256
Device class without serial
Scope
Method
Result
Limitations
Cleanup/safety
```
