# Startchecklist voor de gebruiker

Voer alleen dit uit voordat Luna Xhigh begint:

## Werkmap

```text
MelonDS/
├── jouw Europese Super Mario 64 DS-ROM.nds
└── inhoud van dit pakket
```

De bestandsnaam van de ROM is niet belangrijk. Laat de ROM ongewijzigd.

## Thor

```bash
adb devices -l
```

Er moet precies een bruikbaar `device` zichtbaar zijn. Een regel met `unauthorized` vereist eerst toestemming op de Thor.

## Agent

- open `MelonDS` als project-/werkmap;
- kies `Luna Xhigh`;
- plak de volledige inhoud van `01_GOAL_PROMPT.md`;
- start de `/goal`.

## Niet vooraf doen

- MelonDualDS niet handmatig in een submap klonen;
- ROM niet vooraf patchen;
- geen losse cheatdatabase importeren;
- geen 60fps-binary downloaden;
- geen RA-wachtwoord in een prompt of Markdownbestand zetten;
- geen handmatige display-ID hardcoden.

Luna voert de veilige gitbootstrap, broncheckout, submodules, build, installatie en ADB-validatie uit.
