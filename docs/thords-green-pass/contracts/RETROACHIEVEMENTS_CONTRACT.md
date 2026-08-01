# RetroAchievements contract

## Mode matrix

| Requested | Original clean | Enhanced |
|---|---:|---:|
| Off | Off | Off |
| Casual | Casual | Casual |
| Hardcore | eligible | blocked with explicit recovery |

## Ordering

Resolve profile, enhancements, OC and resume integrity before RA bootstrap. A blocked Hardcore request must fail before emulator/native/RA side effects.

## Identity

Use the existing original European NDS RA hash. Runtime Action Replay codes are applied after identity resolution. Do not spoof or replace the game hash.

## User-Agent

```text
ThorDSEnhanced/<product-version> (Android <release>) melonDS/<core-version>
```

No CR/LF/control characters, no upstream impersonation and no secret logging.

## Online behavior

Use the existing rcheevos/client path. Do not call a direct award endpoint. Casual enhancements are a user choice; Hardcore incompatible features are blocked locally.
