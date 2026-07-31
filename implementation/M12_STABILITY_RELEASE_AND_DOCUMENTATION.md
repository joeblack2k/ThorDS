# M12 — Stabiliteit, release en documentatie

## Doel

Alle releasegates uitvoeren, APK opleveren en het project overdraagbaar maken.

## Vereiste input

M0–M11 status; release matrix; clean branch.

## Werk

1. Full test matrix.
2. 60-minute run.
3. save/relaunch.
4. sleep/resume.
5. renderer/profile switches.
6. secret/ROM scan.
7. license/notices.
8. APK debug/release where signing available.
9. hash/signature.
10. clean git.
11. final docs.
12. known issues.
13. source ROM post-hash.
14. complete final report.

## Tests

Alle files onder `testing/`, plus install on clean app data where safe, upgrade/migration test, non-Thor smoke if environment available.

## Bewijs

```text
docs/evidence/release/
app/build/outputs/...apk
docs/project/RELEASE_NOTES.md
docs/project/KNOWN_ISSUES.md
docs/project/FINAL_REPORT.md
```

## Exitgate

- `04_DEFINITION_OF_DONE.md`;
- release acceptance all pass or explicit non-release;
- no ROM/secrets;
- APK hash;
- physical Thor proof;
- honest 60fps status.

## Richtcommit

```text
release: complete ThorDS Enhanced v0.1 evidence
```
