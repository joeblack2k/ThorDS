# F7 — RA and lifecycle

## RA

- source ROM identified before runtime patch;
- Off and Casual user-selectable;
- Hardcore blocked;
- no direct award calls;
- no hash spoof.

## Relaunch

Timing mode requires a full game relaunch.

## Save state

Reject state when:

```text
state 60fps != session 60fps
state ARM9 ratio != effective ratio
state patch hash != current hash
```

## Sleep/wake

After wake:

- same effective mode;
- no cadence reset to 2;
- no stale telemetry window;
- audio resumes normally.
