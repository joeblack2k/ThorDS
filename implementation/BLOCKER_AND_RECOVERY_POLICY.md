# Blocker- en herstelpolicy

## Blockertype

### Environment

Voorbeelden:

- ontbrekende SDK;
- Gradle download;
- Rust-toolchain;
- diskruimte.

Actie:

- exacte fout;
- minimaal benodigde wijziging;
- opnieuw bouwen;
- geen bronarchitectuur omzeilen.

### Source mismatch

- tag/commit niet correct;
- submodule ontbreekt;
- pad veranderd.

Actie:

- pin herstellen;
- source lock bijwerken;
- geen blind pad aanmaken.

### ROM mismatch

- geen ASMP;
- RA-hash onbekend;
- revision anders.

Actie:

- geen patch;
- Original/general mode;
- alle ROM-onafhankelijke taken afronden;
- blocker met identityrapport.

### Hardware

- ADB offline;
- second display ontbreekt;
- touch niet gekoppeld.

Actie:

- device state/logs;
- retry/restart ADB/app;
- upstream baseline vergelijken;
- geen hardcoded ID.

### Feature feasibility

- 2D/3D niet scheidbaar;
- overclock desync;
- 60fps timing fout.

Actie:

- debugcaptures;
- kleinste unsafe scene/functie;
- veilige fallback;
- featurestatus verlagen;
- v0.1 niet blokkeren door M13.

## Herstel na regressie

1. reproduceer;
2. `git bisect` of commitvergelijking;
3. behoud evidence;
4. revert kleinste veroorzaker;
5. herstel met test;
6. geen stapel hacks bovenop onbekende oorzaak.

## Partial completion

Een blocker in online RA mag niet stoppen:

- UI;
- policytests;
- profile engine;
- widescreen;
- analog;
- build/release prep.

Een blocker in OC >100% mag niet stoppen:

- 100%-plumbing;
- UI capability;
- telemetry;
- M12 release.

## Rapport

Gebruik `operations/BUG_REPORT_TEMPLATE.md` en vermeld:

- gate;
- reproduction;
- expected/actual;
- commit;
- device/build;
- logs;
- attempts;
- safe fallback;
- exact resume step.
