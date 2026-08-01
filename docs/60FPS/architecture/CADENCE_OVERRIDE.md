# Cadence override design

## Developer probe

```text
5208EE44 00000002
0208EE44 00000001
D0000000 00000000
D2000000 00000000
```

Meaning:

- when the exact current value is 2;
- write 1;
- end condition.

## Mandatory observations

Log every value transition with:

```text
scene/checkpoint
old value
new value
source: game or AR
VBlank sequence
main-loop sequence
```

## Product choices

Choose one after measurement:

### Patch every initializer/write

Best provenance and deterministic state, but potentially many overlay sites.

### Hook VBlank comparison and maintain delta

Centralized, but must preserve scene-specific exceptions and original words.

### Runtime AR force plus initializer guards

Simpler, but only acceptable if ordering and all transitions are proven.

## Title/star-select gate

Already-60 scenes must remain unchanged. Do not apply a 2→1 semantic patch
where cadence is already 1.
