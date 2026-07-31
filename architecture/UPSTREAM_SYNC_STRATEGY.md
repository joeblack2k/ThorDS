# Upstream-syncstrategie

## Remotes

```text
origin         → joeblack2k/ThorDS
upstream       → SapphireRhodonite/melonDS-android
parent-upstream→ rafaelvcaetano/melonDS-android (optioneel)
```

## Branches

```text
thords/enhancement-platform-v1
research/arm9-overclock
research/sm64ds-60fps
sync/upstream-<version>
```

## Minimaliseren van conflicts

- nieuwe profile engine in eigen packages;
- UI-integratie via bestaande interfaces/ViewModels;
- compositorwijzigingen beperkt en featureflagged;
- core-OC in klein afzonderlijk patchset;
- geen massale namespace-rewrite;
- geen formatting-only sweep;
- geen dependencyupdates tijdens productmijlpalen.

## Upstreambijdragen

Generieke verbeteringen kunnen upstreamwaardig zijn:

- radial Slot-2 deadzone;
- profile-independent ARM9-OC als veilig bewezen;
- True Widescreen layer primitives;
- Thor display recovery;
- library config corruption fix.

ThorDS-specifiek blijven:

- gameprofilecatalogus;
- productlauncher;
- branding;
- policy defaults.

## Syncprocedure

1. maak clean branch;
2. fetch exact upstreamtag/commit;
3. inspect changelog en core-submodule;
4. merge/rebase in syncbranch;
5. build baseline;
6. run generic regressions;
7. run SM64DS full matrix;
8. update source lock;
9. merge pas na evidence.

## Submodules

Een upstream submodulepin is onderdeel van de productpin. Geen losse `git submodule update --remote` in productbranch.

## Patch carry

Houd per corewijziging:

- korte onderwerpcommit;
- test;
- rationale;
- upstream status;
- conflict notes.

Zo kan een latere coreupdate de OC-patch gecontroleerd herbasen.

## Version display

About toont zowel ThorDS- als upstream/coreversie. Bugreports bevatten beide, zodat issues niet aan verkeerde codebasis worden toegeschreven.
