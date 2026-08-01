# Build commands

Use live equivalents when paths change.

```bash
git status --short --branch
git submodule status --recursive
git diff --check
git -C melonDS-android-lib diff --check

CARGO=/tmp/thords-cargo ./gradlew --no-daemon   :app:testGitHubProdReleaseUnitTest   :app:assembleGitHubProdDebug
```

For Vulkan source changes also run the repository SPIR-V regeneration/check
tasks.

Record APK SHA-256.
