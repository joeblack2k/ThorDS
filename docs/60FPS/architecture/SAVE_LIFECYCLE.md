# Save, state and lifecycle

## Saves

Normal game saves remain compatible between Original and Enhanced unless a
specific gameplay bug proves otherwise.

## Save states

Store or verify:

```text
profile integrity
60fps mode
runtime patch version/hash
ARM9 effective ratio
core revision
```

Reject incompatible state loads with a clear reason.

## State-load sequence

```text
pause
neutralize input/camera
validate timing metadata
load
reapply runtime codes
reset semantic telemetry windows
resume
```

## Lifecycle

Repeat effective configuration after:

- ROM reset;
- full relaunch;
- sleep/wake if core is recreated;
- process recreation;
- controller reconnect where relevant.

Do not repeatedly patch live code from an unsafe Android lifecycle callback.
