# F4 Deterministic Tool Self-Tests

Status: PASS for tool integrity only

The following checked-in tests passed:

```text
test_map_fixed_step_candidates.py
test_scan_fixed_step_consumers.py
test_coin_timestep.py
test_patch.py
```

Observed results:

- fixed-step mapping: `mapped=2`, `ok=true`;
- fixed-step scanner: `findings=4`, `ok=true`;
- coin timestep: source step `0x0C00`, cadence-1 step `0x0600`;
- patch model: `PASS`.

These tests prove deterministic tool behavior and the corrected coin opcode.
They do not prove product timing parity, player pose interpolation, audio
parity, or the final 60 FPS product gate.
