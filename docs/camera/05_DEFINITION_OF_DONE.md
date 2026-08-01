# Definition of done

The camera workstream is green only when all sections below pass.

## Functional

- Right-stick yaw is continuous.
- Angular speed scales monotonically with deflection.
- Center drift is zero within tolerance.
- Full-deflection speed meets the configured target.
- No 45-degree steps occur in ordinary right-stick movement.
- R3 recenters once.
- D-pad fallback works while right stick is neutral.
- Right-stick Y does not trigger zoom or DS buttons.
- No touch-camera buttons or bouncing camera arrows appear in Enhanced.
- Ordinary yaw produces no camera sound.
- Original profile restores original touch arrows, sounds and button camera.

## Context safety

- Dialogue and cutscenes ignore smooth camera.
- Cannon and first-person ignore smooth camera.
- Scripted level cameras remain authoritative.
- Camera collision remains effective.
- Swimming, flying and sliding remain usable.
- Pause, sleep, reconnect and save/load return to neutral input.

## Integration

- Enhanced + True Widescreen passes.
- Enhanced + RA Off passes.
- Enhanced + RA Casual passes.
- Hardcore forces Original.
- No other DS game receives the protocol patch.
- No ROM mismatch receives the patch.

## Engineering

- Kotlin tests pass.
- Native protocol tests pass.
- Patch generator is reproducible.
- Original-word guards pass for the exact EU ROM.
- Mismatch tests fail closed.
- Clean clone can resolve the core fork submodule.
- APK builds and installs.
- 60-minute final soak passes without crash, ANR, stuck input or drift.

## Evidence

- Text/JSON telemetry demonstrates speed, symmetry, recenter count and legacy-bit inactivity.
- Public evidence contains no private capture, ROM path, ROM byte or device serial.
- Superproject and core SHAs are pushed.
