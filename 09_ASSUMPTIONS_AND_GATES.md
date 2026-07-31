# Aannames en bewijs-gates

Een aanname wordt pas projectfeit na bewijs.

## A1 — De ROM is de verwachte Europese retailvariant

**Verwacht**

- gamecode `ASMP`;
- revision 0;
- RA Nintendo DS-hash `ba3c4052e00c5cc31df5d5534c39de1b`.

**Gate**

- headerdump;
- RA-hash berekend met dezelfde logica als de emulator;
- volledige SHA-256;
- profielresolver kiest exact één variant.

## A2 — rc5 bouwt in de gebruikersomgeving

**Gate**

- toolchainversies;
- recursieve submodules;
- succesvolle ongewijzigde build;
- APK-installatie;
- baseline launch.

## A3 — De Thor exposeert twee Presentation-displays bruikbaar aan de app

De source ondersteunt Thor expliciet, maar firmware kan wijzigen.

**Gate**

- `dumpsys display`;
- displaynamen/flags/afmetingen;
- Presentation op tweede paneel;
- beide surfaces leveren beeld;
- lifecycle na slaap.

## A4 — Slot-2 Analog werkt met de Europese runtimecode

**Gate**

- profile injecteert code;
- Slot-2-device actief;
- live x/y/magnitude/anglebewijs;
- spel reageert continu;
- geen ROMwijziging.

## A5 — De Vulkan-compositor kan SM64DS 3D en HUD betrouwbaar scheiden

De rc5-code heeft gestructureerde 2D/3D-data, maar game-scènes moeten dit bevestigen.

**Gate**

- debugcaptures van titel, castle, level, HUD, cutscene en pause;
- duidelijke 3D/2D-maskers;
- scene classifier;
- veilige fallback.

## A6 — Europese 16:9-aspectpatch kan reproduceerbaar worden afgeleid

**Gate**

- decompfuncties/symbolen;
- lokale ROM-adressen;
- check-before-write;
- 4:3 en 16:9 screenshotvergelijking;
- extra horizontale world content;
- cullingtest.

## A7 — Casual RA blijft functioneel met runtimecodes

**Gate**

- originele ROM wordt herkend;
- set laadt;
- active-enhancementstatus zichtbaar;
- normale Casual-unlock;
- geen Hardcoreclaim.

## A8 — ARM9-overclock kan zonder wandklokversnelling

Dit is niet gegarandeerd.

**Gate**

- gekozen timingstrategie gedocumenteerd;
- timer-, audio-, RTC- en gameplaytempovergelijking;
- stressbenchmark;
- save-statebeleid;
- geen schedulerdesync.

Bij falen blijft de instelling 100% en wordt >100% niet productmatig aangeboden.

## A9 — 60fps heeft naast overclock ook gamepatches nodig

**Gate voor later**

- binary diff van de communitypatch;
- mapping naar decomp;
- unieke gameupdates per videoframe;
- physics/timers/audio gelijk;
- geen slowdown in stressscènes.

## A10 — Thor-first defaults breken generieke Android niet

**Gate**

- apparaatdetectie strikt;
- non-Thor fallback;
- één single-display Androidtest;
- algemene ROMstart zonder profiel.
