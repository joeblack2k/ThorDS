# RetroAchievements-audit

## Bestaande status

RetroAchievements vermeldt `melonDS Android` als ondersteunde standalone Nintendo DS/DSi-emulator. Rc5 heeft een uitgebreide native/Androidintegratie.

SM64DS:

```text
RA game ID: 9983
Base set:   191 achievements
EU hash:    ba3c4052e00c5cc31df5d5534c39de1b
```

De hashpagina registreert daarnaast een Bonus-subsetvariant. ThorDS gebruikt voor v0.1 de originele Europese retailvariant.

## Bestaande corebridge

De runtime leest Nintendo DS-geheugen uit:

- Main RAM;
- ARM9 DTCM.

Per frame worden:

- achievements;
- measured progress;
- rich presence;
- leaderboards

bijgewerkt. Save states kunnen RA-runtimeprogress bevatten.

## Gewenste gebruikerspolicy

```text
RA Off
- geen set laden
- geen request/submission

RA Casual
- enhancements toegestaan
- gewone unlocks mogelijk
- UI toont actieve enhancements
- Hardcoreleaderboards niet claimen

RA Hardcore
- alleen Original-profiel
- geen runtimecodes
- geen user cheats
- geen ARM9-OC
- geen rewind/load state
- geen slowdown/frame advance
- reset vereist
```

## Waarom runtimepatches gunstig zijn

De game wordt eerst geïdentificeerd als originele EU-ROM. Daarna worden gecureerde AR-codes voor de sessie geladen. De bronhash blijft dus herkenbaar.

Een fysiek gepatchte ROM kan een andere RA-hash krijgen. ThorDS:

- spooft die hash niet;
- meldt helder wanneer een cachepatched variant niet wordt herkend;
- gebruikt runtimepatches waar mogelijk.

## User-Agent

ThorDS moet een eigen stabiele identiteit gebruiken, bijvoorbeeld:

```text
ThorDSEnhanced/0.1.0 (Android 13) melonDS/<core-version>
```

Exacte Android- en coreversie komen uit build/runtime metadata.

Niet doen:

- upstream MelonDualDS-User-Agent stilzwijgend behouden;
- identificeren als RetroArch;
- identificeren als een andere goedgekeurde client;
- een versie zonder numerieke productversie.

## Credentials

Behoud de bestaande beveiligde auth store.

- Geen plaintext configbestand met wachtwoord.
- Geen token in logcat.
- Geen token in bugreport/evidence.
- Logout wist lokale auth volgens bestaande semantics.
- Devendpoint/useragent overrides alleen in debugbuild en duidelijk zichtbaar.

## Casual-unlocktest

Gebruik normale gameplay en een eenvoudige, nog niet behaalde achievement als dat praktisch is.

Vereisten:

- set/game ID correct;
- Original EU source hash;
- analog + True Widescreen actief;
- Casual;
- event zichtbaar;
- normale submission;
- accountstatus gecontroleerd;
- geen geheugenmanipulatie.

Als het testaccount de eenvoudige achievement al heeft, gebruik een afzonderlijk testaccount of test lokaal event + serverflow zonder ongewenste award. Documenteer de beperking eerlijk.

## Hardcore

De huidige RA-documentatie vereist onder meer:

- cheats uit;
- rewind uit;
- load states geblokkeerd;
- geen slowdown/frame advance;
- Casual→Hardcore alleen met reset;
- unieke User-Agent.

Onze productbeslissing is strenger en eenvoudiger: **alle enhancements maken Hardcore onbeschikbaar**, ook wanneer een widescreen-QoL theoretisch mogelijk toegestaan zou kunnen zijn. Dit voorkomt ambiguïteit voor analog, OC en toekomstige patches.
