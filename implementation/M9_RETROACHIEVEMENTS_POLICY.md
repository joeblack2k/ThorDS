# M9 — RetroAchievements toggle en Hardcore-gate

## Doel

De gebruikerskeuze Off/Casual behouden, terwijl Hardcore alleen met een volledig Original sessieplan beschikbaar is.

## Vereiste input

M4 session integrity; M6/M8 enhancements; existing RA integration.

## Werk

1. Voeg requested/effective policy resolver.
2. Koppel session plan integrity.
3. Toon conflict resolution.
4. Forceer Original+reset voor Hardcore.
5. Preserve Casual with enhancements.
6. Eigen User-Agent.
7. Enhancement summary in RA UI.
8. Test save-state/rewind/cheat gates.
9. Test offline/pending behavior.
10. Voer gecontroleerde Casual gameplayunlock uit indien accountstate dit verantwoord mogelijk maakt.

## Tests

- policy unit matrix;
- mode transitions;
- user agent;
- token redaction;
- original hash recognition;
- Casual set load under enhancements;
- Hardcore launch Original;
- load state blocked in Hardcore.

## Bewijs

```text
docs/evidence/m9/policy-tests.txt
docs/evidence/m9/user-agent-redacted.txt
docs/evidence/m9/casual-session.md
docs/evidence/m9/hardcore-gate.md
docs/evidence/m9/online-unlock-redacted.md
```

## Exitgate

- user controls RA Off/Casual;
- enhancements do not silently disable Casual;
- Hardcore impossible with enhancements;
- no secret leaks;
- normal RA flow.

## Richtcommit

```text
ra: enforce profile-aware casual and hardcore policy
```
