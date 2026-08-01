# F1 — semantic telemetry

## Core

Add:

```text
Sm64dsSemanticMonitor
NDS::ConfigureSm64dsSemanticMonitor
NDS::ObserveSm64dsArm9Execution
NDS::GetSm64dsSemanticSnapshot
```

## Hooks

- JIT block dispatch in `ARMv5::Execute`;
- interpreter instruction execution;
- optional JIT compile membership diagnostics.

## Superproject

Expose snapshot through:

```text
MelonInstance
MelonDSAndroid
JNI
MelonEmulator
debug command / one-second logger
```

## Tests

- disabled zero overhead behavior;
- exact target normalization;
- ARM/Thumb bit handling;
- counter reset/window;
- wrong identity disabled;
- JIT/interpreter parity using synthetic PC sequences.

## Thor gate

At least one nonzero relevant counter in a stable gameplay scene.
If all semantic targets remain zero, use the game-side fallback.
