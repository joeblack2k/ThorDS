# Configuratie en migraties

## Scheiding

```text
GlobalSettings
DeviceDefaults
RomSettings
ProfileUserPreferences
ResolvedSessionPlan
```

Vermeng profiledefaults niet direct in bestaande ROMconfigvelden.

## Nieuwe gegevens

### Profile preferences

- ROM stable key;
- selected profile id;
- enhancement toggle overrides;
- analog curve;
- requested RA mode;
- requested OC percent;
- optional scene fallback overrides.

### Device defaults

- Thor role mapping override;
- safe launch preference;
- default renderer;
- default bottom fit;
- controller descriptor.

## Databasekey

Niet filename/path. Gebruik stable identity.

## Migrations

Bij schema-uitbreiding:

- Room migrationtest;
- oude library blijft zichtbaar;
- bestaande savepaths blijven;
- bestaande controllerconfig blijft;
- profile preferences krijgen defaults;
- corrupt profile row valt terug zonder library te verwijderen.

## Redditbugpreventie

Het communityrapport beschreef dat per-game custom controls tot `no games found` konden leiden.

Preventies:

- library query/table niet afhankelijk van serialized controller JSON;
- profile overrides afzonderlijke table;
- parse error per row isoleren;
- geen exception die hele libraryflow annuleert;
- migration fixture met malformed old controllerconfig;
- startup recovery en diagnostics.

## Defaults

Op Thor:

```text
soft controls: off
dual display: auto
bottom fit: 4:3
SM64DS profile: Enhanced
analog: on
true widescreen: on when validated/capable
RA: preserve user global choice, prompt once
OC: 100%
60fps: required product and release mode, not implemented until the M13
timing and gameplay acceptance matrix is validated
```

Op niet-Thor: upstreamachtige defaults.

## Import/export

Settings backup mag:

- profile preferences;
- display mapping;
- controller curve;
- UI settings.

Niet:

- ROM;
- patch cache;
- RA tokens;
- screenshots;
- private evidence.

## Invalid state

- unknown profile id → Original/general;
- higher schema → preserve row but disable profile;
- invalid OC → 100;
- missing renderer capability → 4:3;
- RA Hardcore + enhancement persisted → present conflict resolution before launch.
