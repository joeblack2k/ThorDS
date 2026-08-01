# Mijlpaalplan

## Overzicht

| M | Titel | Releasegate |
|---|---|---:|
| M0 | Workspace baseline | ja |
| M1 | Build/install baseline | ja |
| M2 | Thor hardware proof | ja |
| M3 | Rebrand en safe defaults | ja |
| M4 | Enhancement Profile Engine | ja |
| M5 | SM64DS EU identity | ja |
| M6 | Analog controls | ja |
| M7 | True Widescreen spike | technische gate |
| M8 | True Widescreen product | ja |
| M9 | RetroAchievements policy | ja |
| M10 | ARM9 overclock foundation | ja, plumbing; >100% conditioneel |
| M11 | Thor-first GUI | ja |
| M12 | Stabiliteit/release | ja |
| M13 | 60fps productfeature | verplicht; volledige implementatie en acceptatie |

## Regels per mijlpaal

Iedere mijlpaal levert:

```text
baseline
implementation
automated tests
physical test where relevant
evidence
status update
worklog
ADR when architectural
commit
```

Geen mijlpaal is groen op basis van alleen code review.

## Gates

### Gate 1 — upstream reality

M0–M2 bewijzen dat:

- bronpin klopt;
- build werkt;
- Thor displays werken.

### Gate 2 — safe enhancement launch

M3–M5 bewijzen dat:

- fork eigen productidentiteit heeft;
- profile engine geen verkeerde ROM patcht;
- Original altijd beschikbaar is.

### Gate 3 — playable SM64DS

M6–M9 bewijzen:

- controls;
- True Widescreen;
- RA policy.

### Gate 4 — productrelease

M10–M12 bewijzen:

- OC future path;
- goede Thor-UX;
- stabiliteit.

## M13-status

M13 mag op drie manieren eindigen:

1. `RESEARCH_READY` — diff/decomp/testharness klaar;
2. `EXPERIMENTAL` — 60fps toggle werkt maar niet releasewaardig;
3. `VALIDATED` — volledige matrix groen.

Alleen status 3 mag default of productclaim worden. V0.1 kan met status 1 releasen.
