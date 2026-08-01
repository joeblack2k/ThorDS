# Automated test matrix

## Unit

- profile schema/resolution and persistence;
- analog radial mapping and camera source ownership;
- widescreen geometry and classifier hysteresis;
- UI requested/effective state;
- RA mode matrix and bootstrap ordering;
- OC ratio validation, config, debt and state metadata;
- 60fps patch generation and unique-update accounting.

## Native

- 100% core equivalence;
- rational ratio arithmetic;
- scheduler/event ordering;
- JIT/interpreter behavior where supported;
- save-state ratio mismatch;
- renderer shader/golden tests;
- capture ring ordering and no stale frame.

## Android integration

- process recreation;
- exact ROM card and settings persistence;
- launch plan atomicity;
- pause menu options preserved;
- unknown ROM and non-Thor fallback;
- manifest/package/updater policy.

## Full build

```bash
./gradlew --no-daemon \
  :app:regenerateVulkanSpirv \
  :app:checkVulkanSpirv \
  :app:testGitHubProdReleaseUnitTest \
  :app:assembleGitHubProdDebug
```
