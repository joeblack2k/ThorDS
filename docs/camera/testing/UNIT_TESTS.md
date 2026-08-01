# Unit and host tests

## Kotlin mapping

Test:

- exact center;
- inside deadzone;
- boundary;
- cardinal inputs;
- diagonals;
- saturation;
- exponent;
- inversion;
- symmetry.

## InputProcessor

Test:

- right-stick axes are reserved;
- generic mapper does not receive them;
- left Slot-2 movement still works;
- R3 increments once;
- key repeat does not increment;
- release sends neutral;
- reconnect starts neutral;
- Original does not reserve camera axes.

## Core

Test mode 0/1 regression plus mode 2.

## Profile

Test exact identity and runtime-code ordering.

## Patch tooling

Test deterministic generation, guard mismatch, branch boundaries and canonical SHA.

## Frame-rate model

Simulate 30 and 60 updates and compare total angular displacement over ten seconds.
