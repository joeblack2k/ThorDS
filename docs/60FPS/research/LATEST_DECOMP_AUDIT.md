# Latest decomp audit

Audit reference:

```text
755f0be5b9658e5f75871c4138ddc0133a2c07c4
```

The current decomp has broader source coverage than the older ThorDS research
snapshot.

## Procedure

Compare the repository's existing pin and this audit pin for:

- VBlank handler;
- main loop;
- scene graph callbacks;
- Stage/entry Behavior and Render;
- every cadence write;
- every fixed-step candidate selected for patching.

Record:

```text
same bytes/semantics
readability-only change
symbol/address change
semantic change
new source coverage
```

Do not update the product pin just because the audit checkout is newer.
Update only in a bounded source-lock commit after reproduction.
