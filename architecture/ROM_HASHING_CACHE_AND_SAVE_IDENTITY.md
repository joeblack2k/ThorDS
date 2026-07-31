# ROMhashing, cache en save-identiteit

## Identitystructuur

```text
RomIdentity
├── system
├── gameCode
├── revision
├── headerCrc
├── fileSize
├── fullSha256
├── optionalFullMd5
└── retroAchievementsHash
```

## Performance

Hash een grote ROM op IO dispatcher en stream in blokken. Toon voortgang alleen bij merkbare duur. Cache identity met:

- SAF URI;
- size;
- last modified;
- content fingerprint.

Herhash wanneer metadata verandert of de gebruiker dat afdwingt.

## Privacy

Hashes zijn niet credentials maar kunnen een lokale collectie beschrijven. Upload ze niet naar eigen telemetry. RA ontvangt alleen wat de bestaande integration nodig heeft.

## Save key

Gebruik geen filename.

```text
SaveGameKey = system + gameCode + sourceRaHash + saveCompatibilityGroup
```

Voor runtimecodes blijft de savegroep hetzelfde.

## Profiles en saves

### Original ↔ Enhanced runtime-only

Gedeelde native `.sav` wanneer profile verklaart:

```text
saveCompatibilityGroup = sm64ds-eu-retail
```

### Cachepatched variant

Alleen delen wanneer bewezen compatibel. Anders:

```text
sm64ds-eu-retail-profile-<id>
```

Toon migrate/copy, geen stilzwijgende overschrijving.

## Save backups

Voor profiel- of patchwijziging:

- flush emulation;
- kopieer save atomair;
- naam bevat timestamp, source identity en profileversion;
- retentie bijvoorbeeld laatste 5;
- herstel via UI.

## Save states

Metadata naast state:

```text
source identity
profile id/version
effective patch set hash
renderer mode
ARM9 OC ratio
RA mode
core SHA
```

Load:

- exact match → normaal;
- veilige compatible match → waarschuwing;
- mismatch → weigeren of expliciete Casual-only migratie;
- Hardcore → load geblokkeerd.

## Source immutabilitytest

Hash vóór en na alle patch-/launchtests:

```text
preSha256 == postSha256
```

Dit is releasebewijs.

## Cachepad

Gebruik app-private storage, bijvoorbeeld:

```text
files/patch-cache/
```

Niet:

- naast ROM;
- publieke Downloads;
- een pad dat Android backup automatisch extern deelt.

## Cleanup

- tempfiles bij startup opruimen;
- incomplete manifesten weg;
- actieve cachefile niet verwijderen;
- clear cache beschadigt saves niet.
