# Bronpins

## Verplichte pinnen

| Component | Pin | Gebruik |
|---|---|---|
| MelonDualDS Android | tag `0.7.0.rc5` | productbasis |
| MelonDualDS tagcommit | `9b28076281545a1e08dccee0b3f925febb8933ac` | HEAD-verificatie |
| SM64DS decomp research | `2d38fe9b825199deec408240849b64b91c965d85` | EU-patchanalyse |
| AM64DS | `d3ae02560c32c402672036677e06e0df6e692fd1` (`analog`) | analogcode/protocol |
| RetroAchievements set | game ID `9983` | SM64DS-set |
| SM64DS EU RA hash | `ba3c4052e00c5cc31df5d5534c39de1b` | profielmatch |
| Librashader | `76462c030b75c4f2d56e5386c3d4d7d1128318b8` | rc5 builddependency |

## Submodulepins

De Androidtag is de autoriteit voor:

- `melonDS-android-lib`;
- Oboe;
- faad2;
- enet.

Na `git submodule update --init --recursive` schrijft Luna:

```bash
git submodule status --recursive
```

naar:

```text
docs/evidence/m0/submodules.txt
```

De exacte hashes worden vervolgens gekopieerd naar:

```text
docs/project/SOURCE_LOCK.md
```

## Waarom de SM64DS-decomppin afzonderlijk is

De decomp is een researchdependency, geen runtimebibliotheek. Hij mag worden gekloond onder:

```text
tools/research/sm64ds-decomp/
```

of buiten de productbron als worktree/cache. Geen ROM-assets of buildoutputs uit die repository worden gecommit.

## Pinverificatie

```bash
git rev-parse HEAD
git describe --tags --exact-match
git submodule status --recursive
```

Verwacht voor Android HEAD:

```text
9b28076281545a1e08dccee0b3f925febb8933ac
0.7.0.rc5
```

## Afwijkingen

Een pin mag alleen wijzigen wanneer:

1. baselinebuild onmogelijk is door een aantoonbare upstreambug;
2. een specifieke latere commit de blocker oplost;
3. de diff beperkt en geïnspecteerd is;
4. een ADR de reden en risico’s bevat;
5. alle regressietests opnieuw draaien;
6. de nieuwe exacte SHA wordt vastgelegd.

“Nieuwste main” is nooit op zichzelf een reden.
