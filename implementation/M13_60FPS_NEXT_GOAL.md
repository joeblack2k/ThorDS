# M13 — 60fps — volgende doelstelling

## Doel

Een correcte 60fps-SM64DS-implementatie ontwikkelen op basis van decompanalyse en ARM9-headroom, zonder de instabiele communitybinary blind te distribueren.

## Vereiste input

M12 release of equivalent stable base; M10 OC capability; community patch reference; decomp.

## Werk

1. Leg exacte communitypatch/sourcevideo vast.
2. Verkrijg legaal lokale patched testcopy.
3. Binary diff original/patched.
4. Map wijzigingen naar decomp functions.
5. Classificeer frame skip, delta, physics, animation, input, audio.
6. Bouw reproduceerbare source patch.
7. Maak runtime AR/BPS output met hashes.
8. Combineer met validated ARM9 OC.
9. Meet unique gameplay updates.
10. Stress all levels/mechanics.
11. RA Casual policy.
12. Versie/provenance.

## Tests

- 60 unique updates/s;
- exactly real-time over 10 min;
- no object double speed;
- no timer drift;
- audio normal;
- no slow motion in known stress scenes;
- save/RA;
- 60 min stability;
- comparisons at 100/OC ratios.

## Bewijs

```text
docs/research/60fps-patch-diff/
docs/research/60fps-decomp-map.md
docs/evidence/m13/timing/
docs/evidence/m13/stress-scenes/
docs/project/adr/ADR-sm64ds-60fps.md
```

## Exitgate

`VALIDATED` uitsluitend wanneer complete matrix groen is. Anders blijft toggle hidden/experimental en v0.1 productstatus onveranderd.

## Richtcommit

```text
research: establish reproducible SM64DS 60fps path
```
