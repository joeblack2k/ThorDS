# Teststrategie

## Testpiramide

### Pure unit tests

- profile parsing;
- identity matching;
- dependency/conflict resolution;
- radial analog;
- camera hysteresis;
- RA policy;
- safe rect geometry;
- scene classifier;
- IPS/BPS parser;
- User-Agent;
- config migrations.

### Native/unit

- AR code canonicalization;
- compositor sampling helpers;
- shader fixture outputs;
- OC ratio math/debt;
- save-state metadata;
- patch hashing.

### Android instrumentation

- database migrations;
- profile UI state;
- launcher flow;
- secondary display abstractions waar fakebaar;
- pause menu;
- safe mode;
- rotation/recreation.

### Physical Thor

- beide displays;
- touch;
- controller;
- real Vulkan;
- SM64DS gameplay;
- RA network;
- sleep/resume;
- performance;
- long run.

## Golden policy

Geen Nintendo-art in committed goldens.

Gebruik synthetische fixtures:

- gradient 3D world;
- checkerboard UI;
- perfecte cirkel;
- lettergrid;
- transparent overlay;
- 2D-only menu;
- capture transition.

Private local game screenshots dienen alleen als evidence.

## Baselinevergelijking

Iedere major feature vergelijkt met:

- rc5 unmodified;
- ThorDS Original 4:3;
- ThorDS Enhanced.

## Testidentiteit

Evidence bevat:

- commit;
- build variant;
- APK hash;
- device fingerprint redacted;
- renderer;
- profile/version;
- ROM identity hash;
- RA mode;
- OC ratio.

## Failure classification

```text
P0 crash/save corruption/ROM mutation/security
P1 unplayable controls/wrong screen/major WS distortion/RA policy violation
P2 scene fallback/performance/UX serious
P3 cosmetic/minor
```

Geen release met open P0/P1.

## Repeatability

Scripts onder `tools/thords/`:

- bootstrap checks;
- build;
- install;
- clear/launch;
- display dump;
- logcat capture;
- screenshot/layer capture;
- input diagnostics;
- performance export;
- secret scan;
- release evidence index.

## Testaccount/RA

Geen credentials in scripts. Login handmatig via app. Tests moeten ook zonder account grotendeels uitvoerbaar zijn.

## ROM

Test harness krijgt ROM URI/path via environment/local config dat gitignored is. Geen hardcoded privépad.
