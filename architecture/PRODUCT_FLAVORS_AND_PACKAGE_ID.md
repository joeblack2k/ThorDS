# Productflavors, package-id en branding

## Doel

ThorDS Enhanced moet naast een geïnstalleerde MelonDualDS kunnen bestaan en mag geen upstreamupdates als eigen updates presenteren.

## Application ID

Voorlopig:

```text
io.github.joeblack2k.thords
```

Debug:

```text
io.github.joeblack2k.thords.dev
```

De bestaande Kotlin namespace kan aanvankelijk `me.magnum.melonds` blijven. Een volledige package-refactor levert weinig productwaarde en vergroot mergeconflicten.

## Appnaam

```text
ThorDS Enhanced
```

Debug:

```text
ThorDS Enhanced Dev
```

## Versioning

Begin:

```text
versionName = 0.1.0-dev
versionCode = eigen monotone reeks
```

Neem upstreamversie in About op:

```text
Based on MelonDualDS 0.7.0.rc5
melonDS core <submodule short SHA>
```

## Flavorstrategie

Behoud zoveel mogelijk upstreamflavors, maar voeg productmetadata centraal toe.

Mogelijke eerste targets:

```text
gitHubProdDebug
gitHubProdRelease
```

Geen Play Store-distributieclaim in v0.1.

## Signing

- Debug gebruikt normale debugkey.
- Release alleen wanneer lokale keystoreconfig bestaat.
- Geen signing secrets in Git.
- APK-signature in evidence opnemen.
- Updater verifieert dezelfde signing lineage wanneer later actief.

## Updater

Voor v0.1:

- upstream updatecheck uitschakelen; of
- scherm toont alleen “updates handmatig via projectrelease” zonder automatische install.

Niet doen:

- MelonDualDS APK over ThorDS package heen proberen te installeren;
- upstream GitHub assets als ThorDS release tonen;
- signaturecheck omzeilen.

## Brandingassets

Geen Nintendo-logo of game-art als appbranding.

Gebruik:

- originele ThorDS-naam;
- eigen geometrisch dual-screenicoon;
- distributieveilige kleuren/typografie;
- gamebanner uitsluitend lokaal uit de gebruikers-ROM in bibliotheekcontext.

Luna hoeft voor v0.1 geen uitgebreide artcampagne te ontwerpen. Functionele branding en toegankelijke UI gaan voor.

## About/licenties

Toon:

- ThorDS versie;
- base fork/tag;
- core SHA;
- GPL/source link;
- dependency notices;
- actieve profilecatalogusversie;
- privacy/RA-netwerkinfo.
