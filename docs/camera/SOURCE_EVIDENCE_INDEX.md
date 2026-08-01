# Source evidence index

This dossier was built from the following verified public source observations.

## ThorDS

- Public main observed: `a7831c38c55e9eeef2376bb2390a99a108ab2bd0`.
- Current `InputProcessor` still calls `CameraDpadHysteresis` and maps `AXIS_Z/RZ` to DS directions.
- `ControllerConfiguration` carries transient `profileCameraEnabled`.
- Profile catalog contains exact ASMP/revision-0/RA-hash identity and canonical AM64DS code.
- Latest status marks M6 partial and M7 pass.

## Decomp

Pin: `2307f06d9ce10e114fa00d2e9318d5161aaed311`.

Recovered evidence supports:

- duplicated camera HUD buttons;
- touch-zone camera-bit construction;
- 45° digital yaw;
- recenter synthesis;
- recenter sound wrapper;
- separate bouncing-arrow renderer.

## Core

The existing fake `CartAnalog` exposes movement registers in mode 0/1 and currently has only X/Y state. Mode 2 is therefore the backward-compatible extension point.
