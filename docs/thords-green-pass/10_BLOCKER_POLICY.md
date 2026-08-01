# Blocker policy

## Real hard blockers

- ADB device is absent for a physical-only gate;
- required source repository or submodule commit is inaccessible;
- continuing risks ROM/save loss or credential disclosure;
- repository state cannot be reconciled without destructive history change;
- a required external account action cannot be performed without the owner.

## Not hard blockers

- a test is difficult;
- a long-standing emulator timing problem exists;
- a screenshot is inconclusive;
- a community patch is undocumented;
- the current implementation would require new instrumentation;
- one scene is inconvenient to reach;
- one ratio above 100% is unstable.

## Response to a blocker

1. preserve the current worktree;
2. isolate the failing subsystem;
3. record exact evidence;
4. finish all independent work;
5. change method or add instrumentation;
6. expose only validated capability;
7. never label blocked behavior green.

## Scope reduction rules

Valid:

- expose 125% and hide 150/175/200 when only 125% passes;
- use safe 4:3 fallback for a proven unsafe scene;
- keep 60fps as a required product mode after it passes; do not downgrade it
  to an optional or explicitly disabled status;
- defer an online unlock only when no authenticated account exists, while completing every offline/runtime/UI gate.

Invalid:

- call policy-only overclock complete;
- call presentation FPS a 60fps patch;
- call anamorphic stretch True Widescreen;
- remove an acceptance test to make status green.
