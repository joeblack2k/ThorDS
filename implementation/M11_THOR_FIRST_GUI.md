# M11 — Thor-first GUI

## Doel

De technische features als eenvoudige dual-screenproductervaring aanbieden zonder de algemene emulatorfunctionaliteit te verwijderen.

## Vereiste input

Resolved session state; profile cards; Thor display roles; RA policy; OC capability.

## Werk

1. Thor launcher top/bottom roles.
2. Game details/enhancement controls op bottom.
3. Play flow.
4. Active profile/mode indicators.
5. Lower-screen pause menu.
6. Safe mode/reset mapping.
7. Relauch-required dialogs.
8. Advanced settings bereikbaar maar secundair.
9. Accessibility.
10. non-Thor fallback.
11. error/recovery states.
12. About/licences/privacy.

## Tests

- first run;
- exact/unknown ROM cards;
- toggle conflicts;
- Play;
- pause/resume;
- safe mode;
- rotation/insets;
- screen reader basics;
- non-Thor/single display;
- state recreation.

## Bewijs

```text
docs/evidence/m11/launcher-top.png
docs/evidence/m11/launcher-bottom.png
docs/evidence/m11/profile-ui.png
docs/evidence/m11/pause-bottom.png
docs/evidence/m11/safe-mode.png
docs/evidence/m11/accessibility.md
```

## Exitgate

- one-click default play;
- no manual cheat/layout setup;
- mode truthfully shown;
- pause useful;
- safe recovery;
- general mode preserved.

## Richtcommit

```text
ui: deliver Thor-first enhancement experience
```
