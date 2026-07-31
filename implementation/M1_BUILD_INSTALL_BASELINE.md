# M1 — Build- en installatiebaseline

## Doel

De ongewijzigde rc5-basis bouwen en op de Thor starten voordat productcode wordt toegevoegd.

## Vereiste input

M0 groen; Java/SDK/NDK/CMake/Rust; ADB-device online.

## Werk

1. Leg toolchainversies vast.
2. Voer Gradle dependency/config checks uit.
3. Bouw `:app:assembleGitHubProdDebug`.
4. Hash APK.
5. Installeer met ADB.
6. Start app.
7. Voeg/importeer de ROM alleen via bestaande flow waar nodig.
8. Start SM64DS of een veilige DS-baselinetest zonder enhancements.
9. Verzamel logcat en performancebaseline.
10. Verander geen productbehavior om een upstreambaselineprobleem te verbergen.

## Tests

- Gradle build;
- bestaande unit tests;
- APK install;
- package launch;
- 10 minuten emulatie;
- audio, save en touch smoke;
- renderer smoke voor Vulkan en veilige fallback indien beschikbaar.

## Bewijs

```text
docs/evidence/m1/toolchain.txt
docs/evidence/m1/build.log
docs/evidence/m1/apk-sha256.txt
docs/evidence/m1/install.txt
docs/evidence/m1/baseline-logcat.txt
docs/evidence/performance/baseline.json
```

## Exitgate

- clean baseline APK;
- Thor launch;
- geen crash/ANR;
- upstream behavior vastgelegd;
- baseline commit/tag onaangetast buiten projectdocs/ignores.

## Richtcommit

```text
build: verify MelonDualDS rc5 Android baseline
```
