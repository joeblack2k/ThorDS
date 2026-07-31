# Workspacebootstrap in een niet-lege map

De gebruiker heeft vóór Luna:

```text
MelonDS/
├── <eigen Europese ROM>.nds
└── dit dossier
```

Een normale `git clone ... .` werkt niet in een niet-lege map. Gebruik daarom een veilige init/fetchprocedure.

## Stap 1 — inventaris

```bash
pwd
find . -maxdepth 2 -type f -print
find . -maxdepth 1 -type f \( -iname '*.nds' -o -iname '*.srl' \) -print
adb devices -l
```

Lees geen volledige ROM naar terminal. Gebruik een script dat hash/headers gecontroleerd verwerkt.

## Stap 2 — ROM onmiddellijk beschermen

Als `.git` nog niet bestaat:

```bash
git init
mkdir -p .git/info
cat >> .git/info/exclude <<'EOF'
*.nds
*.srl
*.rom
private/
local/
docs/evidence/private/
build/
.gradle/
EOF
```

Controle:

```bash
git status --short --untracked-files=all
```

De ROM mag niet verschijnen.

## Stap 3 — upstream ophalen

```bash
git remote add upstream https://github.com/SapphireRhodonite/melonDS-android.git
git fetch --depth=1 upstream refs/tags/0.7.0.rc5
git checkout -b thords/enhancement-platform-v1 FETCH_HEAD
git submodule sync --recursive
git submodule update --init --recursive
```

Wanneer een lokale dossierfile toevallig met upstream conflicteert:

- verplaats alleen die dossierfile tijdelijk naar een veilige directory;
- checkout;
- plaats hem terug onder `docs/spec/`;
- vernietig niets.

## Stap 4 — pin verifiëren

```bash
test "$(git rev-parse HEAD)" = \
  "9b28076281545a1e08dccee0b3f925febb8933ac"

git submodule status --recursive
```

## Stap 5 — duurzame ignores

Voeg aan upstream `.gitignore` toe:

```gitignore
# Local copyrighted game data
*.nds
*.srl
private/
local-roms/
patched-rom-cache/
docs/evidence/private/
```

Controleer dat deze patronen niet per ongeluk gewenste sourcefixtures blokkeren.

## Stap 6 — dossier organiseren

De uitgepakte Markdownbestanden mogen in de projectroot blijven. Voor een schonere bronboom kan Luna ze zonder inhoudsverlies verplaatsen naar:

```text
docs/spec/thords-v1/
```

Behoud `01_GOAL_PROMPT.md` desgewenst in root als entrypoint. Werkdocumenten komen onder:

```text
docs/project/
docs/evidence/
docs/research/
```

## Bestaande gitrepository

Wanneer `.git` al bestaat:

1. geen `git init`;
2. inspecteer `git remote -v`;
3. inspecteer `git status`;
4. bepaal of HEAD van rc5 afstamt;
5. bescherm de ROM;
6. voeg upstreamremote toe zonder bestaande remote te overschrijven;
7. rebase/reset nooit destructief;
8. maak een nieuwe branch.

## Toolchain

Verzamel:

```bash
java -version
./gradlew --version
sdkmanager --list_installed
cmake --version
ninja --version
rustc --version
cargo --version
adb version
```

Installeer ontbrekende requirements alleen wanneer de omgeving dit toestaat. Noteer exact wat is gewijzigd.

## Eerste commit

Pas na:

- ROM/secret scan;
- pinverificatie;
- baseline buildplan;
- staged-file-review.

Commitvoorbeeld:

```text
docs: add ThorDS Enhanced execution specification
```

ROMhashrapport blijft alleen lokaal/evidence en hoeft niet publiek gecommit te worden als de gebruiker dat niet wil.
