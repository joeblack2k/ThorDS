# G8 — M12 stability and release

## Full combination

Test at least:

```text
Original + 4:3 + 100% + RA Off
Enhanced Analog + True WS + 100% + RA Casual
Enhanced Analog + True WS + validated OC + RA Casual
Enhanced Analog + True WS + validated OC + 60fps + RA Casual
Original + 4:3 + 100% + RA Hardcore
Safe mode recovery
```

## Required tests

- full Gradle unit and SPIR-V gate;
- clean install and upgrade from prior ThorDS data;
- 60-minute combined-feature run;
- save, exit, relaunch and save integrity;
- home/resume and 30-second sleep/wake;
- world/menu/transition and screen-swap stability;
- controller pipeline recreation;
- RA offline/reconnect where account is available;
- generic unknown-ROM and non-Thor-safe smoke;
- ROM pre/post hash;
- secret/history/private-path scans;
- submodule reachability;
- APK identity, permissions, SHA-256 and notices.

## Deliverables

```text
docs/project/RELEASE_NOTES.md
docs/project/KNOWN_ISSUES.md
docs/project/FINAL_REPORT.md
docs/evidence/release/
```

## Exit

All required full-target rows in `07_ACCEPTANCE_MATRIX.md` are PASS. Do not publish a public APK release containing unvalidated 60fps or OC labels. A source-only development milestone may be pushed earlier.

## Suggested commit

```text
release: complete ThorDS Enhanced green-pass evidence
```
