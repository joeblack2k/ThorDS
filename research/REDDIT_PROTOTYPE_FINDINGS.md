# Bevindingen uit het AYN Thor-communityprototype

## Wat het prototype bewijst

De gelinkte AYN Thor-post toont een werkende handmatige combinatie van:

- MelonDualDS met Slot-2 Analog Input;
- SM64DS analogpatch;
- 16:9-aspectcode;
- aangepaste top-screenlayout;
- bottom screen op eigen aspect;
- rechterstickcamera;
- optionele 60fps-patch.

Daarmee is het productconcept geen theoretische gok.

## Handmatige stappen die wij productiseren

Communityflow:

```text
ROMvariant zoeken
→ analogpatch vinden
→ eventueel ROM patchen
→ widescreen cheat toevoegen
→ GBA Slot Analog kiezen
→ custom layout maken
→ rechterstick extra mappen
→ RA zelf uitzoeken
```

ThorDS-flow:

```text
ROM toevoegen
→ exact profiel automatisch herkennen
→ enhancements zichtbaar kiezen
→ Play
```

## Bekende widescreencode uit de post

De post noemt voor een Amerikaanse SM64DS 1.1-achtige variant drie conditional writes van:

```text
0x1555 → 0x1C72
```

De adressen zijn regio-/revisionafhankelijk en **niet bruikbaar voor het Europese productieprofiel**.

De waardeconversie is logisch:

```text
0x1555 ≈ 4/3 × 4096
0x1C72 ≈ 16/9 × 4096
```

De SM64DS-decomp bevestigt dat `0x1555` als Fix12 aspectratio wordt gebruikt.

## UX-problemen uit de post

- Top en bottom werden aanvankelijk beide gestretcht.
- Een handmatige custom layout was nodig.
- Rechterstickmapping was niet vanzelfsprekend.
- Per-ROM custom controls leidden bij een gebruiker tot een lege ROMbibliotheek na relaunch.
- De workflow was te foutgevoelig voor normale gebruikers.

ThorDS moet daarom:

- top/bottom apart configureren;
- controlleroverride als immutable profile overlay opslaan;
- filename nooit als stabiele databasekey gebruiken;
- een veilige recoveryroute bieden;
- profile state en library scan state scheiden.

## 60fps-waarschuwing

Gebruikers meldden:

- slowdown bij Chain Chomp;
- slowdown op de berg;
- afwijkend tempo tijdens King Bob-omb;
- explosie-/gameplayproblemen;
- stabiel gedrag na verwijderen van de 60fps-patch.

De post concludeert dat een accurate DS-emulator extra geëmuleerde CPU-capaciteit nodig heeft. Daarom:

- analog + True Widescreen + 60fps zijn productdoelen;
- ARM9-OC-fundering komt mee;
- 60fps is pas groen na eigen patchanalyse en timingtests; zonder die groene
  gate is de productrelease niet compleet.

## RA-les

Een fysiek gepatchte ROM kan de ondersteunde RA-hash verliezen. Runtimecodes behouden de originele bronidentiteit en zijn daarom de voorkeursroute voor Casual. Hardcore blijft geblokkeerd met actieve enhancements.

## Productlesson

De community heeft de technische onderdelen al aangetoond. Onze innovatie is:

- betrouwbare detectie;
- correct layer-aware widescreen;
- provenance;
- policy;
- testmatrix;
- één-klikbare Thorervaring.
