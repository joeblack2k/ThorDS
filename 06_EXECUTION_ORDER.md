# Uitvoeringsvolgorde

## Fase A — betrouwbaarheid van de basis

1. **M0 Workspace baseline**
   - ROM herkennen en beschermen;
   - repository exact bootstrapen;
   - bronpins registreren.
2. **M1 Build/install baseline**
   - ongewijzigde rc5 bouwen;
   - APK op Thor installeren;
   - algemene emulator starten.
3. **M2 Thor-hardwareproof**
   - displays, touch, controller, lifecycle en renderer vastleggen.

Geen productrefactor vóór M1 en M2 groen zijn. Anders is niet duidelijk of regressies uit upstream of ons werk komen.

## Fase B — productfundering

4. **M3 Rebrand en veilige Thor-defaults**
5. **M4 Enhancement Profile Engine**
6. **M5 Exacte SM64DS EU-identiteit**

Deze fase levert nog geen True Widescreen, maar wel een veilige manier om per ROM patches en configuratie te activeren.

## Fase C — speelbaarheid

7. **M6 Analog Controls**
8. **M7 True Widescreen spike**
9. **M8 True Widescreen product**
10. **M9 RetroAchievements-policy**

M7 mag tijdelijk diagnostische beelden en developeropties gebruiken. M8 verwijdert de spikeachtige workaround en voldoet aan de volledige scènematrix.

## Fase D — toekomstbestendigheid en UX

11. **M10 ARM9-overclockfundering**
12. **M11 Thor-first GUI**
13. **M12 Stabiliteit, release en documentatie**

ARM9-OC wordt vóór de GUI voltooid zodat de GUI een echte capabilitystatus kan tonen en geen loze toggle bouwt.

## Volgende doelstelling

14. **M13 60fps**

M13 is als document onderdeel van dit pakket, maar niet automatisch releaseblokker voor v0.1. De verplichte bijdrage tijdens deze run is:

- communitypatch identificeren;
- patchdiff-harness;
- decompmapping;
- timingtelemetrie;
- overclockcapability;
- disabled-by-default profielplaceholder;
- testmatrix.

Een stabiele 60fps-implementatie mag alleen door als M0–M12 al groen zijn en de 60fps-gates eveneens slagen.

## Afhankelijkheden

```text
M0 → M1 → M2
          ↓
         M3 → M4 → M5 → M6
                        ↓
                       M7 → M8 → M9
                                 ↓
                                M10 → M11 → M12
                                             ↓
                                            M13
```

## Stop-/fallbackregels

- M1 build faalt: repareer toolchain/baseline, niet vooruit programmeren.
- M2 secondary display faalt: herstel upstream Thor-path vóór GUIwerk.
- M6 analog faalt: True Widescreen mag wel worden gespiked, maar release blijft geblokkeerd.
- M7 toont onvoldoende laagseparatie: bouw geen nep-True-Widescreen; analyseer compositor of gebruik game-side HUDpatch.
- M8 heeft enkele onveilige scènes: die scènes vallen terug naar 4:3; feature kan pas groen worden als fallback stabiel en expliciet getest is.
- M9 RA-service tijdelijk onbereikbaar: voltooi lokale policytests en markeer online unlock als externe blocker.
- M10 overclock niet timingveilig: lever no-op 100%-plumbing en laat >100% verborgen.
- M13 instabiel: v0.1 blijft 30fps en gaat door.
