# RetroAchievements-policyarchitectuur

## Inputs

```text
requested RA mode
profile integrity
active curated enhancements
user cheats
ARM9 OC
rewind
save-state load capability
slowdown/frame advance
resume state
```

## Resolver

```text
resolveRaMode(requested, integrity) -> EffectiveRaPolicy
```

Output:

- effective mode;
- allowed features;
- disabled reasons;
- reset required;
- UI message.

## Matrix

| Requested | Original clean | Enhanced |
|---|---:|---:|
| Off | Off | Off |
| Casual | Casual | Casual |
| Hardcore | Hardcore eligible | blocked → user must switch profile |

ThorDS schakelt een expliciete Hardcorekeuze niet stilzwijgend naar Casual en start dan alsof niets is gebeurd. Toon een keuze:

```text
Use Original and restart in Hardcore
or
Continue Enhanced in Casual
```

## Session transitions

### Casual → Hardcore

- alleen via Original;
- volledige reset/relaunch;
- no resume state.

### Hardcore → Casual

- toegestaan;
- duidelijke mode-indicator;
- leaderboards aangepast conform existing integration.

### Resume/quick resume

- effectieve mode wordt Casual;
- gebruiker wordt geïnformeerd.

## Enhancementstatus

RA UI bevat:

```text
Mode: Casual
Enhancements:
- Analog Controls
- True Widescreen
Hardcore unavailable with active enhancements
```

## User-Agent

Centraliseer in build/runtime metadata.

```text
ThorDSEnhanced/<version> (Android <release>) melonDS/<core>
```

Test:

- niet leeg;
- numerieke productversie;
- geen CR/LF/control;
- maximale redelijke lengte;
- niet identiek aan upstream/andere client;
- log alleen redacted header in debug.

## Hashing

Gebruik bestaande DS RA-hashimplementatie. Profile engine consumeert die identiteit maar wijzigt hem niet.

## Offline

Behoud upstream queueing. Enhancementstatus hoeft niet naar RA-server te worden gestuurd buiten normale User-Agent/clientcontext, maar moet lokaal voor bugreports beschikbaar zijn.

## Leaderboards

Volg de bestaande integrationpolicy. Als leaderboards Hardcore vereisen, Casual met enhancements submit die niet. UI moet niet suggereren dat casual leaderboards actief zijn.

## Tests zonder credentials

- policy unit tests;
- mock RA repository;
- fake session;
- User-Agent format;
- no token logging.

## Online tests

Gebruik alleen user-provided login via bestaande UI. Luna mag niet om credentials in prompt/log vragen. ADB screenshots redigeren gebruikersnaam waar nodig.
