# Projectmap

Gebruik deze index om niet alle documenten tegelijk te behandelen.

## Oriëntatie

| Vraag | Document |
|---|---|
| Wat bouwen we? | `02_PRODUCT_CHARTER.md` |
| Welke regels zijn hard? | `03_NON_NEGOTIABLES.md` |
| Wanneer is het af? | `04_DEFINITION_OF_DONE.md` |
| In welke volgorde? | `06_EXECUTION_ORDER.md` |
| Welke keuzes liggen al vast? | `08_DECISIONS_ALREADY_MADE.md` |
| Welke aannames moeten bewezen? | `09_ASSUMPTIONS_AND_GATES.md` |

## Bronnenonderzoek

| Onderwerp | Document |
|---|---|
| Waarom rc5? | `research/BASE_REPOSITORY_SELECTION.md` |
| Exacte pins | `research/SOURCE_PINS.md` |
| Huidige MelonDualDS-functies | `research/MELONDUALDS_CAPABILITY_AUDIT.md` |
| Thor-displays | `research/AYN_THOR_DISPLAY_AUDIT.md` |
| EU-ROM/RA-hash | `research/SM64DS_ROM_IDENTITY.md` |
| Analogpatch | `research/AM64DS_ANALOG_PATCH_AUDIT.md` |
| Redditproof | `research/REDDIT_PROTOTYPE_FINDINGS.md` |
| Decompgebruik | `research/SM64DS_DECOMP_AUDIT.md` |
| Widescreen | `research/WIDESCREEN_RESEARCH.md` |
| RA | `research/RETROACHIEVEMENTS_AUDIT.md` |
| ARM9-timing | `research/ARM9_TIMING_RESEARCH.md` |
| 60fps communitypatch | `research/60FPS_COMMUNITY_PATCH_RESEARCH.md` |
| Licenties | `research/LICENSE_PROVENANCE_AND_DISTRIBUTION.md` |

## Architectuur

| Systeem | Document |
|---|---|
| Overzicht | `architecture/SYSTEM_ARCHITECTURE.md` |
| Git/bootstrap | `architecture/WORKSPACE_BOOTSTRAP.md` |
| App-id/flavors | `architecture/PRODUCT_FLAVORS_AND_PACKAGE_ID.md` |
| Profielengine | `architecture/ENHANCEMENT_PROFILE_ENGINE.md` |
| Profielschema | `architecture/PROFILE_SCHEMA.md` |
| AR/BPS/IPS | `architecture/PATCH_PIPELINE.md` |
| ROM/cache/save | `architecture/ROM_HASHING_CACHE_AND_SAVE_IDENTITY.md` |
| True Widescreen | `architecture/TRUE_WIDESCREEN_ARCHITECTURE.md` |
| Vulkan-details | `architecture/VULKAN_LAYER_COMPOSITOR.md` |
| Analog | `architecture/ANALOG_INPUT_ARCHITECTURE.md` |
| ARM9-OC | `architecture/ARM9_OVERCLOCK_ARCHITECTURE.md` |
| RA-policy | `architecture/RETROACHIEVEMENTS_POLICY.md` |
| Thor-UX | `architecture/THOR_GUI_ARCHITECTURE.md` |
| Settings/migraties | `architecture/CONFIGURATION_AND_MIGRATIONS.md` |
| Privacy | `architecture/SECURITY_PRIVACY_AND_TELEMETRY.md` |
| Upstream | `architecture/UPSTREAM_SYNC_STRATEGY.md` |

## Implementatie

Start in `implementation/MILESTONE_PLAN.md`. Iedere M-file bevat:

- input;
- wijzigingen;
- tests;
- bewijs;
- exitgate.

`implementation/FILE_BY_FILE_PLAN.md` koppelt architectuur aan waarschijnlijke bronpaden. Die paden moeten eerst tegen rc5 worden geverifieerd.

## Gameprofielen

| Profiel | Document |
|---|---|
| SM64DS Enhanced | `profiles/SM64DS_EU_PROFILE.md` |
| Analogcode | `profiles/SM64DS_ANALOG_AR_CODE.md` |
| Widescreenpatch | `profiles/SM64DS_WIDESCREEN_PATCH_DERIVATION.md` |
| Controller | `profiles/SM64DS_CONTROLLER_PROFILE.md` |
| Scènepolicy | `profiles/SM64DS_TRUE_WIDESCREEN_SCENE_POLICY.md` |
| Original | `profiles/ORIGINAL_PROFILE.md` |
| Phantom Hourglass | `profiles/FUTURE_PHANTOM_HOURGLASS.md` |
| Spirit Tracks | `profiles/FUTURE_SPIRIT_TRACKS.md` |

## Testen en bewijs

Start met `testing/TEST_STRATEGY.md`. Gebruik:

- `testing/ADB_TEST_HARNESS.md`
- relevante acceptatietest per feature;
- `testing/REGRESSION_MATRIX.md`
- `testing/EVIDENCE_REQUIREMENTS.md`
- `testing/RELEASE_ACCEPTANCE_MATRIX.md`.

## Werkadministratie

Begin desgewenst met `operations/USER_START_CHECKLIST.md`; bronpaden staan in `operations/SOURCE_PATHS_OF_INTEREST.md`.

Templates staan onder `operations/`. Luna vult kopieën onder `docs/project/` en `docs/evidence/` in; de originelen blijven templates.
