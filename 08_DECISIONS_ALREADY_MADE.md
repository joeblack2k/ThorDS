# Besluiten die al genomen zijn

Luna heropent deze keuzes alleen bij aantoonbaar technisch bewijs dat uitvoering onmogelijk of schadelijk is. Een afwijking vereist een ADR.

| Besluit | Uitkomst |
|---|---|
| Native SM64DS-port of emulatorbasis | Emulatorbasis |
| Basisfork | SapphireRhodonite MelonDualDS |
| Baseline | tag `0.7.0.rc5` |
| Eerste game | Super Mario 64 DS Europe |
| Productvorm | algemene emulator met gecureerde gameprofielen |
| Thor als hoofdtarget | ja |
| Analogbron | AM64DS runtimecode + Slot-2 Analog Input |
| Widescreen | True Widescreen, geen eindproductstretch |
| Renderer voor True Widescreen | Vulkan eerst |
| Bottom screen | altijd aspect-correct 4:3 |
| RA-toggle | gebruiker bepaalt |
| RA Casual met enhancements | toegestaan |
| RA Hardcore met enhancements | geblokkeerd |
| 60fps in v0.1 | harde product- en release-eis |
| ARM9-OC-fundering in v0.1 | wel |
| Onbekende 60fps-binary | niet als vertrouwde dependency |
| ROMmodificatie | nooit in-place |
| Europese ROM in Git | absoluut niet |
| Zelda | roadmap na SM64DS |
| Luna-opdracht | programmeren, bouwen en op Thor testen |
| Push/PR | niet zonder latere expliciete opdracht |

## Reden van de kernkeuze

De emulatorroute levert direct:

- complete DS-hardwareemulatie;
- dual-screenoutput;
- touch;
- audio;
- saves;
- JIT;
- Android lifecycle;
- RetroAchievements;
- bestaande Thor-ondersteuning.

De decomp wordt gebruikt om gamepatches te begrijpen en te genereren, niet om tijdens deze fase een nieuwe engineport te starten.

## Productgrens

ThorDS Enhanced mag visueel en qua bediening als een gespecialiseerde portervaring voelen, maar blijft technisch eerlijk benoemd als emulatorfork. Geen marketingterm mag verhullen dat de originele ROM in melonDS draait.
