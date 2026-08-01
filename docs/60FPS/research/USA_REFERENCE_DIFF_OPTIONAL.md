# Optional USA Rev1 reference diff

## Inputs

Local/private only:

```text
original USA Rev1 ROM
locally patched reference ROM
```

## Procedure

1. Verify both hashes.
2. Parse NDS sections.
3. Diff contiguous ranges.
4. Identify ARM9/overlay/NitroFS changes.
5. Map runtime addresses to the USA decomp/symbols.
6. Translate semantics, not raw addresses, to EU.
7. Compare with EU cadence consumer inventory.
8. delete/private-store local outputs.

## Public outputs

Allowed:

- source hashes;
- changed range offsets/sizes;
- section names;
- function names;
- classification;
- ThorDS-authored equivalent patch.

Do not publish bytes or ROM-derived payload.
