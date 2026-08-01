# Current state at dossier creation

## ThorDS public main

Observed:

```text
a7831c38c55e9eeef2376bb2390a99a108ab2bd0
render: close SM64DS structured widescreen proof gates
```

Do not reset to it. It is a known ancestor.

## Existing input implementation

The current public code still contains:

```text
ControllerConfiguration.profileCameraEnabled
CameraDpadHysteresis
CameraDpadInputState
InputProcessor.updateProfileCamera()
```

`InputProcessor` reads:

```text
MotionEvent.AXIS_Z
MotionEvent.AXIS_RZ
```

and emits:

```text
Input.LEFT / RIGHT / UP / DOWN
```

The relevant lifecycle hardening was introduced by:

```text
e95699aae96d0d5a86bdf332a514650a9619b4f9
input: harden and trace Slot-2 analog lifecycle
```

That work must be preserved where it remains useful:

- source-aware D-pad ownership;
- neutralization;
- instrumented analog sweep;
- controller lifecycle tests.

The digital camera synthesis itself must be replaced.

## Existing profile

The exact Enhanced profile already contains:

- canonical European AM64DS movement code;
- profile ID `right-stick-camera`;
- True Widescreen development code;
- RA Off/Casual allowance.

The existing AM64DS payload hash is:

```text
e68025c3aad3a47941ab2903dd9d212b91bafedff705ea6252677c27d07bdb1c
```

Preserve canonical provenance. Do not casually edit its words.

## Current project status

M6 remains partial because physical gameplay scenarios are still open. M7 proof work has advanced beyond the old dossier. The camera workstream should be a new bounded camera pass, not a reset of M6/M7.
