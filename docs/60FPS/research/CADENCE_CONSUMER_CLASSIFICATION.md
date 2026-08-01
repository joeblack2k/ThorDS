# Cadence consumer classification

Run `code/PY_SCAN_CADENCE_CONSUMERS.md`.

## Required classes

| Class | Typical form | Likely action |
|---|---|---|
| write/init | `cadence = 2` | patch/gate |
| delta timer | `timer -= cadence` | retain |
| delta interpolation | `step * cadence` | retain/verify |
| fixed timer | `timer--` | parity/fixed-point |
| fixed animation | `Advance()` each update | half-rate/fractional |
| fixed physics | constant integration step | derive 60 Hz step |
| message/HUD | frame counters | parity or delta |
| particles/effects | fixed spawn/update | parity/fractional |
| scene exception | title/star-select | leave 60 Hz |
| unknown | ambiguous | investigate/block |

## Deliverables

- machine-readable JSON;
- Markdown summary by category;
- unresolved list;
- exact source SHA.
