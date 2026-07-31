# Security- en secretscan

## Git

```bash
git ls-files | grep -Ei '\.(nds|srl|sav)$'
git log --all --name-only --pretty=format: | grep -Ei '\.(nds|srl)$'
git diff --cached --name-only
```

Verwacht geen ROM.

## APK

```bash
unzip -l app.apk | grep -Ei '\.(nds|srl|sav)$'
strings app.apk | grep -Ei 'password|bearer|authorization'
```

Beoordeel false positives.

## Repository scan

Zoek:

- RA tokens;
- passwords;
- emails;
- home paths;
- ADB serial;
- keystore;
- private keys;
- ROM magic/content;
- save dumps;
- screenshots.

## Manifest

- exported debug receivers;
- cleartext traffic;
- file provider paths;
- backup rules;
- debuggable release;
- permissions.

## Patch engine

- malformed patch tests;
- output bounds;
- path traversal;
- symlink;
- temp cleanup;
- checksum fail.

## Network

- RA HTTPS;
- updater disabled/secured;
- no own analytics;
- User-Agent correct;
- no log bodies.

## Release gate

Een finding wordt:

```text
confirmed safe
fixed
false positive with reason
```

Geen onverklaarde high-severity finding.
