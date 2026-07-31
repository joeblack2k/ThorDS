# Luna Xhigh-werkregels

## Gedrag

- Programmeer en test; schrijf niet opnieuw alleen een voorstel.
- Gebruik dit dossier als contract.
- Inspecteer echte bron vóór paden/namen aannemen.
- Vraag niet naar informatie die ROMheader, ADB, Git of source kan leveren.
- Werk zelfstandig door niet-geblokkeerde taken.
- Claim niets zonder bewijs.

## Contextbeheer

Bij iedere fase:

1. lees alleen relevante docs via `05_PROJECT_MAP.md`;
2. vat actieve constraints in worklog samen;
3. houd een korte status;
4. maak ADR bij architectuurkeuze;
5. commit na groene gate.

## Codekwaliteit

- kleine modules;
- bestaande architectuur volgen;
- dependency injection waar repository dat gebruikt;
- pure logic apart testbaar;
- immutable session plan;
- expliciete errors;
- bounded IO;
- no per-frame allocations;
- comments verklaren “waarom”, niet triviale syntax.

## Buildcadans

- na model/APIwijziging compile;
- na native bridge wijziging Android build;
- na shader wijziging SPIR-V check/regenerate;
- na core wijziging native + Android + physical smoke;
- geen urenlange ongeteste branch.

## ADB

- fysieke Thor regelmatig;
- clear logcat per test;
- package/commit in evidence;
- geen credentials in command history/log;
- save backup vóór destructive test.

## Git

- ROM scan vóór stage;
- staged diff lezen;
- targeted add;
- geen push;
- geen force reset van userwork;
- submodule SHA bewust.

## UI

- resolved state tonen;
- geen toggle die runtime niet uitvoert;
- disabled reason zichtbaar;
- safe mode altijd bereikbaar;
- no Material/debug-placeholder als eindproduct voor hoofdflow.

## Research

- primaire sources eerst;
- communitypatch als input, niet waarheid;
- decomp voor semantics;
- exacte pins;
- bevinding in docs/research.

## Stopcriteria

Stop niet omdat 60fps onopgelost is. Lever v0.1 stabiel en M13 foundation.

Stop wel met releaseclaim bij:

- ROM in Git/artifact;
- savecorruptie;
- wrong display;
- HUD stretch;
- Hardcore policy bypass;
- secret leak;
- P0/P1 open.
