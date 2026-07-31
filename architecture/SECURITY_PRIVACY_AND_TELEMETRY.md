# Security, privacy en telemetrie

## Threat model

- ROM per ongeluk committen;
- RA-token in logcat;
- malicious/malformed patch;
- path traversal in cachepatcher;
- untrusted profilecatalogus;
- updater supply-chain;
- debugreceiver in release;
- oversized ROM/patch causing memory exhaustion;
- evidence met privédata.

## ROM

- read-only open;
- bounded offsets/sizes;
- streaming hashes;
- no external upload;
- no content in exceptions;
- cache path generated, never derived unsanitized from filename.

## Profiles

V0.1 catalogus is embedded en signed door APK-integriteit.

Wanneer later externe catalogi bestaan:

- manifest signature;
- HTTPS;
- pinned catalog version;
- no executable code;
- strict schema/size limits;
- patch hash;
- explicit user update.

## Patch parsers

- integer overflow checks;
- output size limit;
- source hash;
- target hash;
- temp directory confinement;
- no symlinks/path traversal;
- fuzz/unit tests;
- atomic cleanup.

## RA

- existing secure auth store;
- tokens redacted;
- User-Agent fixed;
- network errors sanitized;
- screenshots redact username where needed;
- no password config file.

## Telemetry

V0.1 heeft **geen eigen remote telemetry**.

Lokale diagnostics:

- opt-in export;
- hashes/profile ids;
- performance metrics;
- device model/build;
- no ROM path unless user chooses;
- no RA token/account email;
- no Android serial.

## Debug tooling

- debug command receiver only debug builds;
- release manifest audit;
- no arbitrary memory editor in release;
- developer layer captures gated;
- Hardcore always unavailable with developer tooling active.

## Logs

Rate limit:

- input;
- scene classifier;
- frame timing;
- display changes.

Never log:

- credentials;
- complete HTTP bodies;
- ROM bytes;
- save contents;
- encryption keys.

## Evidence scrub

Script scans:

```text
.nds/.srl
token/password/auth
home directory paths
adb serial
email
private IP where unnecessary
```

Fail release if found.

## Privacy text

About/Privacy explains:

- ROM stays local;
- RA requests go to RetroAchievements when enabled;
- no ThorDS analytics;
- cache/save locations;
- clear cache and logout.
