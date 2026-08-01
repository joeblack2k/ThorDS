# Enhanced menu contract

## Source of truth

The UI consumes the existing resolved session/profile model. It must not independently decide capability or RA policy.

## Exact ROM controls

```text
Profile
Analog Controls
True Widescreen
ARM9 ratio
60 FPS
RetroAchievements mode
Effective status
Compatibility/relaunch reason
Play
```

## Requested versus effective

Examples:

```text
Requested ARM9: 150%
Effective ARM9: 125%
Reason: 150% not validated on this build
```

```text
Requested RA: Hardcore
Effective: blocked
Actions: Original + restart / Enhanced Casual
```

## Persistence

Store per exact ROM identity/profile using the existing preference repository. Unknown ROMs receive Original/upstream behavior.

## Gameplay UI

Do not permanently cover the lower DS screen. Pause may temporarily replace it with the ThorDS menu. Resume restores exact touch mapping.
