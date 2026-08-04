# Debug Launch Reuse

Status: PASS for autonomous test setup

## Result

The debug launch command now reuses an already running identical ROM:

```text
action=launch_rom reused_current_rom uri=content://...
```

The second launch did not open the `Emulator running` confirmation dialog.

## Scope

- Exact SM64DS EU ROM identity.
- Vulkan runtime.
- Debug build installed on the AYN Thor.
- Release unit tests passed.
- Debug APK build passed.
- Worktree remained free of generated test files.

This change is debug-only. Normal user ROM launch behavior is unchanged.
