# M2 — Thor hardware proof

## Doel

Objectief vastleggen hoe de actuele Thor-firmware displays, touch, controller en lifecycle aan rc5 blootstelt.

## Vereiste input

M1 APK; ADB; `research/AYN_THOR_DISPLAY_AUDIT.md`; bestaande Thor mapper.

## Werk

1. Verzamel ADB display/window/devicegegevens.
2. Voeg debug display-role overlay/export toe indien upstreamdata onvoldoende is.
3. Laat beide physical panels hun role/id/metrics tonen.
4. Test top/bottom rendering en screen swap.
5. Test touchrect en letterboxbars.
6. Capture built-in controller events en descriptors.
7. Test background/sleep/resume/display listener.
8. Documenteer feitelijke afmetingen en refresh rates.
9. Definieer role classifier en fallback, maar productiseer hem pas na bewijs.

## Tests

- cold launch;
- activity op normale display;
- secondary Presentation;
- touch grid 3×3;
- controller buttons/axes;
- screen swap;
- sleep 30 s;
- resume;
- 5 relaunches;
- unexpected presentation dismissal where testable.

## Bewijs

```text
docs/evidence/m2/dumpsys-display.txt
docs/evidence/m2/window-displays.txt
docs/evidence/m2/device-properties.txt
docs/evidence/m2/display-role-screens/
docs/evidence/m2/touch-grid.json
docs/evidence/m2/controller-events-redacted.json
docs/evidence/m2/lifecycle-logcat.txt
```

## Exitgate

- fysieke rollen ondubbelzinnig;
- touch klopt;
- controller gevonden;
- lifecycle herstelt;
- geen hardcoded display-ID nodig.

## Richtcommit

```text
test: prove AYN Thor dual-display behavior
```
