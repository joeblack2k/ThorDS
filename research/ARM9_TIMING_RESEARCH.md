# ARM9-timing- en overclockonderzoek

## Doel

Extra ARM9-uitvoercapaciteit bieden zonder de geëmuleerde DS-wandklok te versnellen. Dit is bedoeld als fundering voor een latere stabiele 60fps-SM64DS-patch.

## Relevante melonDS-state

De core bevat onder meer:

```text
ARM9Timestamp
ARM9Target
ARM7Timestamp
ARM7Target
ARM9ClockShift
scheduler events
DMA timing
GPU timing
SPU timing
```

De bron waarschuwt expliciet dat wijzigingen aan main loop/timing uitgebreid op desync moeten worden getest.

## Wat overclock niet is

Niet:

- Android fast-forward;
- targetFPS naar 120 zetten;
- audio sneller afspelen;
- VBlank verdubbelen;
- alle scheduler timestamps schalen;
- ARM7 en RTC versnellen;
- alleen renderframes interpoleren.

## Mogelijke strategieën

Luna onderzoekt minimaal twee.

### Strategie A — CPU cycle cost scaling

Schaal alleen de tijd die ARM9-instructie-executie aan de scheduler doorbelast:

```text
effectiveCost = physicalCost × 100 / overclockPercent
```

Voordeel:

- meer instructies binnen dezelfde schedulerwindow.

Risico:

- bus-/memorytiming wordt onrealistisch;
- DMA/GXFIFO-interactie;
- JIT en interpreter moeten gelijk zijn;
- integer rounding/debt.

### Strategie B — extra ARM9 execution budget

Behoud schedulerwandtijd, maar geef ARM9 aanvullende slices terwijl events/timers op oorspronkelijke tijd blijven.

Voordeel:

- expliciet conceptueel onderscheid.

Risico:

- moeilijk te integreren met timestamp-based core;
- eventordering;
- CPU/ARM7 IPC;
- interrupts;
- DMA.

### Strategie C — gamegerichte wait/skip-optimalisatie

Geen generieke OC, maar profile-specific vermijden van kunstmatige frame waits.

Voordeel:

- minder core-impact.

Risico:

- lost echte CPU-bound scenes niet op;
- is onderdeel van 60fps-gamepatch, niet algemene OC.

## Vereiste ADR

Voor >100% effect:

- schedulerpad diagram;
- gekozen strategie;
- JIT/interpreterimpact;
- memory/DMA/GXimpact;
- roundingmethode;
- runtimechange-policy;
- save-stateformat;
- tests;
- rollback.

## Config

```text
arm9OverclockEnabled: false
arm9OverclockPercent: 100
allowed values: 100, 125, 150, 175, 200
```

Intern liever rationeel/integer:

```text
numerator/denominator
```

Bewaar fractional debt zodat cycles niet structureel verloren gaan.

## Telemetrie

Per seconde en per testscene:

- requested ARM9 budget;
- executed ARM9 cycles/instructions;
- ARM9 timestamp debt;
- ARM7 delta;
- missed deadlines;
- emulated video FPS;
- unique game updates;
- present FPS;
- frame time percentiles;
- audio underruns;
- GXFIFO stalls;
- JIT status;
- thermal state waar Android die geeft.

## Safe UI

- Alleen `Experimental`.
- Standaard 100%.
- Veranderen tijdens actieve sessie vereist minimaal pause + reset; voorkeur relaunch.
- Een save state bevat de ratio of is alleen laadbaar op dezelfde ratio.
- Hardcore forceert 100%.

## Gate

Een ratio >100% is pas productmatig actief wanneer:

- gameklok over 10 minuten binnen 0,1% van 100%-reference blijft;
- audio pitch/duur gelijk;
- RTC gelijk;
- inputtempo gelijk;
- geen save-statecorruptie;
- stressscènes objectief meer headroom hebben;
- geen nieuwe regressies in generieke DS-smoke tests.

Bij falen blijft de plumbing bestaan maar de capabilitystatus `unsupported`.
