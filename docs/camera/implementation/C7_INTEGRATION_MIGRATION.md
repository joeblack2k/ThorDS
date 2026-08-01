# C7 — integration and migration

## Enhancement ID

Prefer retaining `right-stick-camera` as the stored ID and changing its implementation/display name. This minimizes preference migration.

If the schema or existing user data makes that unsafe:

1. add `smooth-orbit-camera`;
2. migrate `right-stick-camera=true` to the new ID once;
3. mark the old ID deprecated;
4. test old and fresh preference stores.

## Profile version

Increment the exact Enhanced profile version when semantics change.

## Effective session state

Latch:

```text
profile
smooth camera requested
smooth camera effective
sensitivity
deadzone
update rate
yawUnitsPerTick
RA mode
```

Do not recompute midway through a session without a defined live-update contract.

## Compatibility matrix

Test these combinations:

```text
Original
Enhanced analog only
Enhanced smooth camera
Enhanced smooth camera + True Widescreen
Enhanced smooth camera + RA Casual
Safe Mode
Hardcore request
```
