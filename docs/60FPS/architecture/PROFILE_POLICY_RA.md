# Profile and RetroAchievements policy

## 60 FPS enhancement schema

```text
id: 60fps
requires:
  - exact EU identity
  - Action Replay
  - semantic telemetry capability
  - validated ARM9 ratio
conflicts:
  - RA Hardcore
requires relaunch: true
```

## RA

| Mode | Original | Enhanced 60 FPS |
|---|---:|---:|
| Off | allowed | allowed |
| Casual | allowed | allowed |
| Hardcore | allowed | blocked |

No silent downgrade.

## Launch order

1. identify unchanged source ROM;
2. resolve Original/Enhanced;
3. resolve RA policy;
4. resolve 60 FPS and required OC;
5. calculate immutable session plan;
6. identify/load RA game;
7. install curated runtime codes;
8. start emulation.

## Status

Show:

```text
60 FPS requested
60 FPS effective
effective ARM9 ratio
validation state
reason when unavailable
RA mode
```
