# ADR — patch strategy

## Decision

Use a game-side runtime patch that modifies the normal orbit target, with input supplied through a new Slot-2 protocol.

## Considered options

### A. Keep digital D-pad synthesis

Rejected: 45-degree stepping, repeat delay, arrows and accidental recenter remain.

### B. Write final camera position from melonDS

Rejected: fights camera collision, scripted states, easing and future game logic.

### C. Patch camera yaw target

Accepted: minimal authority, original solver remains.

## Placement candidates

### In-place digital-block replacement — preferred

Advantages:

- no separate code cave;
- exact scope;
- naturally runs at camera update rate.

Requirements:

- complete block boundaries;
- sufficient instruction space;
- reimplemented legacy fallback;
- exact original-word guards.

### Trampoline — fallback

Advantages:

- easier structured assembly;
- preserves more original code.

Requirements:

- proven safe writable/executable region;
- branch range and cache coherency;
- no heap/overlay collision;
- long soak.

## Rejected evidence standard

A region being zero in one snapshot is not proof that it is safe.
