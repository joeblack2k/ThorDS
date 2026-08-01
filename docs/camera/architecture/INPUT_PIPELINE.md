# Input pipeline and ownership

## Existing problem

The current path processes ordinary configured axes and then processes the right stick again as a profile camera. That can allow a previously saved right-stick mapping to coexist with the automatic camera mapping.

## New ordering

```text
1. Validate controller source/device.
2. Resolve profile-owned axes.
3. Process left Slot-2 movement.
4. Process smooth-camera right stick.
5. Exclude profile-owned axes from generic axis-to-button mapping.
6. Process all remaining user mappings.
```

## Ownership rules

When Smooth Orbit is effective:

- `AXIS_Z` and `AXIS_RZ` are reserved by default on Thor;
- a verified fallback axis pair may replace them;
- trigger axes must never be auto-selected as camera axes;
- R3 is reserved for recenter;
- the stored user mapping is not deleted;
- Original mode restores the stored mappings.

## Lifecycle rules

`releaseAllInputs()` must:

- release ordinary button ownership;
- send camera X/Y = 0;
- not reset the monotonic recenter sequence during the same emulation session.

A full profile/session teardown must:

- send flags = 0;
- send X/Y = 0;
- create a new sequence domain for the next ROM boot.

A controller reconnect must begin neutral.
