# G6 — M9 RetroAchievements closeout

## Existing implementation

The pure policy resolver and pre-bootstrap launch gate already exist. Extend them; do not replace the RA client.

## Required work

- add per-ROM requested RA mode to the game UI;
- preserve Off/Casual/Hardcore intent through the complete session plan;
- ensure Off skips endpoint/session/native RA bootstrap;
- allow Casual with curated enhancements and original EU identity;
- block Hardcore + Enhanced before emulator/native side effects;
- implement Original + restart and Enhanced Casual recovery actions;
- ensure Hardcore Original blocks curated codes, user cheats, OC >100, rewind/load state and incompatible resume;
- centralize and test ThorDS User-Agent;
- preserve existing token storage and offline queue;
- surface effective mode and active enhancements in UI/pause status;
- verify no credential/token logging.

## Online gate

When an authenticated RA account is already configured:

- load game ID 9983;
- verify rich presence/set;
- select a simple unearned achievement;
- trigger it through normal gameplay;
- verify normal submission and account state.

Do not ask for credentials in logs or prompts. If no authenticated account exists, complete every mock/runtime/UI gate and record only the online unlock as `NOT_EXECUTED_USER_AUTH_REQUIRED`; do not fabricate it.

## Suggested commit

```text
ra: complete profile-aware user mode flow
```
