# Save-, sleep- en resumetests

## Native save

1. backup existing save;
2. collect progress;
3. wait for flush;
4. close app;
5. relaunch;
6. verify progress.

Run Original and Enhanced; runtime patches share save.

## Forced stop

After confirmed flush:

```bash
adb shell am force-stop <package>
```

Relaunch and verify.

## Android background

- Home 30 s;
- Home 5 min;
- return.

## Sleep

- sleep 30 s;
- sleep 5 min;
- wake/unlock;
- both displays;
- audio;
- input;
- save.

## Display recreation

- Presentation dismissed/background;
- restore;
- no black bottom;
- touch correct.

## Save states

Casual:

- save/load same profile/OC;
- RA runtime state;
- screen transition.

Mismatch:

- different profile patch set;
- different OC;
- different core;
- clear rejection/warning.

Hardcore:

- state creation optional debug;
- load impossible.

## Cache/profile update

- save backup;
- profileversion increment fixture;
- no save deletion;
- cache invalidates independently.

## Corruption recovery

Synthetic truncated save:

- no overwrite of last good backup;
- user-visible error;
- app remains usable.

## Evidence

Hashes of saves may be local evidence; do not commit actual user save unless user explicitly chooses. Use synthetic saves in CI.
