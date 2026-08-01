# Camera state gating

Smooth Orbit must be active only when the normal player orbit camera owns the view.

## Allow

- standard outdoor/indoor third-person movement;
- ordinary swimming/flying/sliding only when the same normal orbit routine remains authoritative.

## Deny or fall through

- dialogue/talk camera;
- cutscenes;
- star-acquisition sequences;
- cannon aiming;
- first-person/look mode;
- doors and scripted transitions;
- camera tags or forced camera objects;
- recenter already in progress;
- any unknown state.

## Concrete recovered clues

The normal routine receives a camera-tag/state argument and maintains:

```text
camera flags at +0x154
camera tag pointer around +0x144
recenter-active at +0x1A0
```

Luna must prove the exact hook's live values. Do not assume that a source-level parameter remains in `r0` at the selected instruction.

## Fail-closed rule

If state classification is uncertain:

```text
do not apply analog delta
run original camera behavior
```
