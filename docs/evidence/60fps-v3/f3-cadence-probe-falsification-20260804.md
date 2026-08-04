# F3 Cadence Probe Falsification

Status: PARTIAL

## Test

The exact EU profile was used:

`ASMP / revision 0 / RA hash ba3c4052e00c5cc31df5d5534c39de1b`

The developer-only `60fps-dev-cadence` enhancement was enabled through the
debug profile command. The command forced a relaunch. The probe was then
disabled and the ROM was relaunched again.

## Observed result

During the enabled session:

- runtime cadence remained `1`;
- most one-second windows reported `60/60` or `61/61` unique updates and
  emulator frames;
- one window reported `30` unique updates and `61` emulator frames;
- no stable 30-to-60 semantic transition was produced;
- the device showed a black presentation during part of the probe session.

The probe therefore does not prove a valid 60 FPS timing mode. It is not
accepted as a product patch.

## Recovery

The probe was disabled through the same exact-profile command. The ROM was
relaunched after disabling it. The normal developer state remains active, with
the probe disabled.

## Decision

F3 cadence probe: `PARTIAL`, falsified as a product solution.

F4 timing work must use a source-derived timing model and must not promote this
Action Replay probe.
