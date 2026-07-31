# Evidencevereisten

## Directory

```text
docs/evidence/
├── m0 ... m13
├── performance
├── release
└── private (gitignored)
```

## Iedere claim bevat

- commit SHA;
- build variant;
- APK SHA-256;
- device model/build;
- profile/version;
- ROM identity hash;
- renderer;
- RA mode;
- OC;
- timestamp;
- command/test;
- result;
- artifactlink.

## Screenshots

Gamebeeld blijft private tenzij publicatie gekozen. Committed evidence gebruikt:

- diagrams;
- redacted metadata;
- synthetic golden outputs;
- measurements.

## Logs

- preserve raw private;
- create redacted public summary;
- mention filter;
- no cherry-picked success-only logs.

## True Widescreen

Per scene:

- 4:3 reference;
- 3D layer;
- UI layer;
- final;
- classifier JSON;
- geometry measures.

## Analog

- raw/processed sample;
- device descriptor redacted;
- gameplay checklist;
- no need to record user video publicly.

## RA

- mode;
- game id/hash;
- event;
- submission redacted;
- no credentials.

## OC

- raw CSV;
- selected strategy;
- wallclock;
- audio;
- capability result.

## Release index

`docs/evidence/release/INDEX.md` lists every gate and exact evidencepath.

## Honesty

When evidence cannot be collected:

```text
NOT TESTED
BLOCKED
PARTIAL
```

Niet `PASS`.
