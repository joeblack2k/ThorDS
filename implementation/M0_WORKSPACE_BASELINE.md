# M0 — Workspace baseline

## Doel

De reeds gevulde gebruikersmap veilig omzetten in een vastgepinde projectrepository, de Europese ROM identificeren en beschermen, en alle bronnen/toolchains inventariseren.

## Vereiste input

- projectroot met dossier;
- één of meer lokale ROMkandidaten;
- Git en ADB;
- `architecture/WORKSPACE_BOOTSTRAP.md`;
- `research/SOURCE_PINS.md`.

## Werk

1. Inventariseer zonder bestanden te wijzigen.
2. Initialiseer Git alleen indien nodig.
3. Zet ROMpatronen direct in `.git/info/exclude`.
4. Fetch exact rc5 en maak de werkbranch.
5. Initialiseer submodules recursief.
6. Verifieer HEAD.
7. Identificeer de Europese ROM via header, RA-hash en SHA-256.
8. Voeg duurzame ignores toe.
9. Maak source lock, status en worklog.
10. Scan staged/untracked state op ROMdata en secrets.

## Tests

- pin shell assertions;
- ROM identity parser test met synthetische header;
- `git check-ignore -v <rom>`;
- `git ls-files | grep -Ei '\.(nds|srl)$'` moet leeg zijn;
- `git submodule status --recursive` zonder `-` of onverwachte dirty state.

## Bewijs

```text
docs/evidence/m0/environment.txt
docs/evidence/m0/git-head.txt
docs/evidence/m0/submodules.txt
docs/evidence/m0/rom-identity-redacted.json
docs/evidence/m0/rom-ignore-proof.txt
docs/project/SOURCE_LOCK.md
```

## Exitgate

- HEAD exact rc5;
- ROM exact of duidelijk geblokkeerd;
- geen ROM in index;
- submodules aanwezig;
- werkbranch actief;
- baseline ongewijzigd buiten docs/ignores.

## Richtcommit

```text
chore: establish pinned ThorDS workspace baseline
```
