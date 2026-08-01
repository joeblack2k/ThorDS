# Evidence and privacy

## Public evidence allowed

- source SHAs;
- build/test commands;
- aggregate numeric telemetry;
- redacted JSON;
- profile/patch hashes;
- pass/fail matrices;
- addresses and minimal expected-word guards.

## Public evidence forbidden

- ROM bytes or hexdumps;
- ROM filename/path;
- patched ROM;
- save contents;
- screenshots or screen recordings from the private device;
- device serial;
- account tokens;
- private home paths.

## Required scans

Before every push:

```bash
git diff --cached --check
git diff --cached --name-only | rg -i '\.(nds|srl|rom|sav|dsv)$' && exit 1 || true
git diff --cached --name-only | rg -i '^docs/evidence/.*\.(png|jpg|jpeg|webp|mp4)$' && exit 1 || true
```

Run the repository's high-confidence secret scan as well.
