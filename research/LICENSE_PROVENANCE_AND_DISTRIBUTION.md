# Licenties, provenance en distributie

## ThorDS Enhanced

De basisfork en melonDS-code vallen onder copyleftlicenties. Luna controleert de exacte licentiebestanden van:

- Androidfrontend;
- melonDS core;
- Oboe;
- faad2;
- enet;
- librashader;
- overige Gradle/native dependencies.

Een gedistribueerde APK vereist bijbehorende bron en notices conform de toepasselijke licenties.

## Productrepository

Canonieke bron:

```text
https://github.com/joeblack2k/ThorDS
```

Vereist:

- `LICENSE` behouden;
- upstreamcopyright behouden;
- `NOTICE`/credits toevoegen;
- bronlinks;
- wijzigingen duidelijk markeren;
- source beschikbaar bij binaryrelease;
- geen gesloten distributie van afgeleide GPL-code.

## AM64DS

Leg vast:

- auteur LRFLEW;
- bronrepository/branch/commit;
- emulatorcode-licentie;
- patchcodeherkomst;
- eventuele Apache/GPL-onderdelen afzonderlijk;
- onze aanpassingen.

Een Action Replay-code is klein, maar provenance blijft verplicht.

## SM64DS-decomp

De decompcode kan onder een open licentie staan. Nintendo-ROMassets vallen daar niet onder.

Toegestaan in project:

- symbolnamen;
- zelf geschreven patchgeneratie;
- functionele analyse;
- adressen/instructiewoorden voor patchvalidatie;
- bronreferenties.

Niet distribueren:

- ROM;
- volledige extracted filesystem;
- modellen/textures/audio;
- proprietary compiler;
- ROM-buildoutput.

## Communitypatches

Voor iedere patch:

```text
id
title
author
source URL
source commit/release
license/permission
input identity
output identity
patch hash
our modifications
```

Wanneer licentie onbekend is:

- niet stilzwijgend bundelen;
- auteur benaderen of patch reproduceerbaar zelf opbouwen uit technische kennis;
- oorspronkelijke bron alsnog crediteren;
- geen volledige ROMdiff met onnodig copyrighted materiaal.

## Screenshots/evidence

Lokale bewijscreenshots kunnen gamebeeld bevatten. Houd ze:

- buiten normale source release;
- onder een gitignored evidence/private pad;
- of publiceer alleen wanneer de gebruiker dat expliciet kiest.

Golden tests voor CI gebruiken synthetische patronen en layerfixtures, geen Nintendo-screenshots.

## RetroAchievements

Respecteer:

- eigen User-Agent;
- geen credentiallogging;
- geen server-/hashbypass;
- correcte modepolicy;
- privacytekst voor netwerkaanvragen.

## Updater

Een ThorDS-build mag niet automatisch een upstream MelonDualDS-APK installeren die een andere signing key/package/productpolicy heeft. De updater wordt:

- uitgeschakeld in v0.1; of
- alleen gekoppeld aan een later eigen releasekanaal met signature/assetvalidatie.
