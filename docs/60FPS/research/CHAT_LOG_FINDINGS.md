# Findings from the supplied execution log

## Work already completed

- loop counter sampler;
- reset/wrap correction;
- one-second native windows;
- cadence value;
- Stage timer candidate;
- Original/Enhanced comparison;
- failed branch-monitor and JIT-branch experiments;
- public research commits;
- ARM9 overclock foundation.

## Important negative results

- launch/scene resets invalidate naïve unsigned deltas;
- Castle Garden Original and Enhanced both produce ~60/61 loop counts;
- Stage timer is zero in that checkpoint;
- `MonitorARM9Jump()` does not see the target on the active JIT path;
- ARM64 generated branch helpers also produced zero Stage calls.

## Correct continuation

Use actual `ARMv5::Execute` JIT dispatch/block entry, not branch helpers.
If semantic function entry still cannot be observed, use a guarded game-side
counter.

## Workstream interruption

The log switched to the Smooth Orbit Camera task before the JIT block-entry
probe was completed. This dossier resumes M13 without undoing camera work.
