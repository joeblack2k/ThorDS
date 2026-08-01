# Game patch architecture

## Layers

```text
Profile resolver
  → exact identity/capabilities
  → curated runtime code
  → ARM7 VBlank Action Replay engine
  → guarded ARM9/overlay writes
  → SM64DS timing behavior
```

## Phase A — developer probe

A guarded cadence-variable write allows a quick falsifiable experiment.

It is not enough for release because:

- game initializers can rewrite the value;
- AR and ARM9 VBlank ordering may race;
- fixed-step consumers may break;
- performance may fall behind.

## Phase B — product patch

The source-derived product patch must include:

- stable cadence activation;
- scene reset/initializer handling;
- fixed-step corrections;
- exact overlay guards;
- optional diagnostic counters;
- deterministic source and generated output.

## Preferred expression

Use Action Replay when all changes are safe runtime memory/instruction writes.

Use a core feature only when the AR model cannot safely express the required
timing or code injection. Keep game semantics in the game patch where possible.
