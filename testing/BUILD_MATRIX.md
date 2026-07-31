# Buildmatrix

## Verplicht

| Variant | ABI | Doel |
|---|---|---|
| GitHub Prod Debug | arm64-v8a | actieve ontwikkeling/Thor |
| GitHub Prod Release | arm64-v8a + upstream supported | release wanneer signing beschikbaar |

## Checks

```bash
./gradlew clean
./gradlew :app:assembleGitHubProdDebug
./gradlew test
./gradlew lint
./gradlew checkVulkanSpirv
```

Pas tasks aan op echte rc5-namen; noteer afwijking.

## Native modes

- Vulkan enabled;
- OpenGL/native 4:3 fallback;
- software smoke indien upstream ondersteunt;
- JIT enabled;
- interpreter smoke voor OC semantics waar praktisch.

## Toolchain matrix

Primaire pin:

```text
JDK 21
NDK 28.0.13004108
CMake 3.22.1
compileSdk 36
Rust stable/pinned dependency checkout
```

## Reproducibility

Twee opeenvolgende builds hoeven door timestamps/signing niet byte-identiek te zijn, maar moeten:

- dezelfde source lock;
- dezelfde version metadata;
- dezelfde embedded profilecatalogus;
- dezelfde generated shader hashes;
- functioneel dezelfde testresultaten.

## Failures

- generated shader stale → fail;
- submodule dirty → fail;
- profile catalog validation fail → fail;
- ROM in inputs/artifacts → fail;
- release debug receiver/exported component → fail;
- missing notices → release fail.

## APK inspectie

```bash
apkanalyzer manifest application-id <apk>
apkanalyzer manifest version-name <apk>
unzip -l <apk> | grep -Ei '\.(nds|srl)$'
apksigner verify --verbose <apk>
sha256sum <apk>
```
