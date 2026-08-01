# F4 — timing-correct patch

## Transformation workflow

For every fixed-step consumer:

1. create deterministic Original replay;
2. identify state evolution per real second;
3. implement 60 Hz equivalent;
4. compare;
5. document tolerance;
6. add source and generated patch;
7. repeat stress scene.

## Patterns

- integer timer → parity/fixed-point;
- animation → fractional advance;
- acceleration/position → half-step integration;
- effect spawn → real-time rate gate;
- sound/event trigger → edge/debounce;
- scene init → preserve effective cadence.

## Deliverable

```text
tools/thords/60fps/patch/
  source assembly
  manifest
  generator
  verifier
```

The committed output is the generated curated code plus its SHA.
