# Super Mario 64 DS Europese ROM-identiteit

## Doelvariant

```text
Titel:      Super Mario 64 DS
Regio:      Europe
Talen:      En, Fr, De, Es, It
Gamecode:   ASMP
RA game ID: 9983
```

De RetroAchievements-pagina registreert voor de Europese retailvariant de Nintendo DS-systeemhash:

```text
ba3c4052e00c5cc31df5d5534c39de1b
```

## Belangrijke hashprecisie

De bovenstaande waarde is de **RetroAchievements Nintendo DS-hash**. MelonDS berekent die uit specifieke ROMsecties, waaronder:

- header;
- ARM9-bootcode;
- ARM7-bootcode;
- banner.

Behandel deze waarde niet automatisch als MD5 van het volledige `.nds`-bestand.

Bereken afzonderlijk:

- volledige SHA-256;
- optionele volledige MD5 voor lokaal onderzoek;
- bestandsgrootte;
- gamecode;
- revision byte op headeroffset `0x1E`;
- header CRC;
- RA-systeemhash.

## Discovery

Luna zoekt in de projectroot:

```text
*.nds
```

Voor iedere kandidaat:

1. lees alleen de noodzakelijke header/secties;
2. valideer offsets en groottes;
3. bereken identity;
4. selecteer exact de ASMP/RA-match;
5. bewaar identity in lokale projectstate, niet ROMbytes.

## Profielmatching

Minimale match:

```text
gameCode == ASMP
revision == expected
raHash == ba3c4052e00c5cc31df5d5534c39de1b
```

Aanvullend:

- full SHA-256 registreren;
- expected header constraints;
- geen profiel toepassen als sectiegrenzen of checksumvalidatie faalt.

## Geen filename-afhankelijkheid

Bestandsnamen zoals:

```text
Mario.nds
Super Mario 64 DS (Europe).nds
mijn-rom.nds
```

zijn semantisch irrelevant. Een filename met `[patched]` mag nooit de libraryconfig of profile key bepalen.

## Source en runtime identity

Voor runtime Action Replay:

- RA identificeert de originele bron-ROM;
- profiel activeert codes daarna;
- save blijft aan de originele game-identiteit gekoppeld.

Voor een toekomstige cachepatch:

- `sourceIdentity` blijft de originele ROM;
- `runtimeIdentity` krijgt profiel- en patchsethash;
- `saveCompatibilityGroup` bepaalt of saves gedeeld mogen worden;
- RA kan alleen laden als de actuele gamehash ondersteund is of runtimepatching de bronhash behoudt.

## Evidence

Schrijf:

```text
docs/evidence/m0/rom-identity-redacted.json
```

met:

- basename;
- size;
- gamecode;
- revision;
- RA hash;
- full SHA-256;
- source byte-identical hash vóór/na testen.

Geen pad naar privégebruikersmap wanneer dat niet nodig is.
