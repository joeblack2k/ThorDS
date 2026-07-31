/goal

Herstel het ThorDS Enhanced-project vanaf de huidige repositorystate.

1. Lees `docs/project/STATUS.md`, `docs/project/WORKLOG.md`, open ADR’s, de actieve mijlpaal en `implementation/BLOCKER_AND_RECOVERY_POLICY.md`.
2. Inspecteer Git HEAD/status/submodules en bescherm de lokale ROM.
3. Reproduceer de laatst gemelde blocker exact.
4. Zoek de kleinste root cause; stapel geen workaround op een onbekende fout.
5. Behoud alle groene mijlpalen en user saves.
6. Revert alleen gerichte veroorzakende wijzigingen wanneer nodig.
7. Bouw en test na iedere fix.
8. Gebruik de fysieke Thor via ADB voor hardwaregerelateerde problemen.
9. Werk evidence/status/worklog bij.
10. Commit de herstelwijziging gericht.
11. Ga daarna verder met de eerstvolgende niet-groene gate.

Geen push. Geen ROM of credentials. Geen voltooiingsclaim zonder tests.
