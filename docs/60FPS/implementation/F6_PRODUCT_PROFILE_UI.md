# F6 — product profile and UI

## Profile

Add `60fps` only to exact `sm64ds.eu.thor-enhanced`.

Fields:

```text
defaultEnabled
experimental/validated
required capabilities
required effective ARM9 ratio
conflict RA_HARDCORE
requires relaunch
runtime code and SHA
provenance
```

## UI

Show:

```text
60 FPS requested
60 FPS effective
status: unavailable / experimental / validated
effective ARM9
reason
```

## Recovery

Safe Mode and Original must always boot without the 60 FPS code.

## Migration

Bump profile/catalog version only as required. Existing analog/widescreen
preferences must remain.
