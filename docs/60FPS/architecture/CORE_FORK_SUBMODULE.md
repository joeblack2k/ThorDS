# Core fork and submodule

## Current topology

```text
ThorDS origin:
https://github.com/joeblack2k/ThorDS

Core origin:
https://github.com/joeblack2k/melonDS-android-lib

Core branch:
thords/arm9-overclock-foundation
```

## Core changes in this pass

Likely:

- semantic execution monitor;
- interpreter/JIT parity;
- telemetry snapshot;
- additional overclock validation/fixes.

## Publication order

1. branch from exact current gitlink;
2. make bounded core commit;
3. test from superproject;
4. push core;
5. verify remote object;
6. update superproject gitlink;
7. clean recursive clone test;
8. push superproject.

Never leave a public superproject pointing to a local-only object.
