# Cadence Audit Comparison

The product inventory uses the existing local EU decomp checkout at
`2d38fe9b825199deec408240849b64b91c965d85`. Its source-tree SHA-256 is recorded
in `cadence-consumers.json`.

The read-only comparison checkout uses audit commit
`755f0be5b9658e5f75871c4138ddc0133a2c07c4`.

| Checkout | Findings | Use |
|---|---:|---|
| Product pin | 192 | authoritative for generated product work |
| Audit pin | 194 | comparison only |

The two inventories are not interchangeable. The two-finding delta requires
manual review of the changed files before any cadence consumer is patched.
The product gate remains red while the inventory contains unresolved
`unknown` consumers.
