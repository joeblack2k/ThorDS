/goal

Voer een strenge onafhankelijke review uit van de huidige ThorDS Enhanced-branch.

Lees eerst:

- `03_NON_NEGOTIABLES.md`;
- `04_DEFINITION_OF_DONE.md`;
- `testing/RELEASE_ACCEPTANCE_MATRIX.md`;
- `docs/project/STATUS.md`;
- `docs/project/FINAL_REPORT.md`;
- alle evidence-indexen.

Review:

1. source pins en submodules;
2. ROM-/secretveiligheid;
3. profile exact matching;
4. curated/user patchscheiding;
5. analog math en camera;
6. True Widescreen layer logic en fallback;
7. shader synchronization/generated files;
8. RA Off/Casual/Hardcore policy;
9. User-Agent;
10. ARM9 timing en 100%-equivalence;
11. Thor lifecycle/touch;
12. save/migrations;
13. performance/leaks;
14. licenses/updater;
15. evidenceclaims.

Reproduceer belangrijke tests. Markeer per finding severity, file/line, gevolg en fix. Accepteer geen “waarschijnlijk”. Maak geen push. Wijzig alleen code wanneer de opdracht expliciet review + fix vraagt.
