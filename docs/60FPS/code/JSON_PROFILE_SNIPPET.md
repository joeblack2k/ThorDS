# JSON — profile snippet

Insert only after the runtime code is generated and verified.

```json
{
  "id": "60fps",
  "displayName": "60 FPS",
  "defaultEnabled": false,
  "kind": "ACTION_REPLAY",
  "requiredCapabilities": [
    "NDS_EMULATION",
    "ACTION_REPLAY",
    "SM64DS_SEMANTIC_TELEMETRY",
    "ARM9_OVERCLOCK_VALIDATED"
  ],
  "requires": [
    "analog",
    "true-widescreen"
  ],
  "conflicts": [
    "RA_HARDCORE"
  ],
  "requiresRelaunch": true,
  "experimental": true,
  "minimumArm9Percent": 125,
  "provenance": "ThorDS source-derived SM64DS Europe 60 FPS patch",
  "runtimeCode": {
    "id": "sm64ds.eu.60fps.v1",
    "codeWords": [],
    "codeSha256": "REPLACE_AFTER_GENERATION",
    "expectedOriginalWords": []
  }
}
```

Do not mark `defaultEnabled=true` or remove `experimental` until the complete
gate passes.
