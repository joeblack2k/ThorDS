# Timestep and fixed-step corrections

## Consumer classes

### Delta-aware

Example:

```c
timer -= data_0208EE44;
```

When cadence changes from 2 at 30 Hz to 1 at 60 Hz, real-time duration remains
approximately constant. Usually no patch.

### Fixed integer decrement

```c
timer--;
```

At 60 Hz it runs twice as fast. Options:

- decrement only on alternating updates;
- convert to fixed-point;
- use a shared 30 Hz phase.

### Fixed-point integration

For an original 30 Hz Euler step:

```text
position += velocity
velocity += acceleration
```

A 60 Hz conversion generally needs a half-step model. Do not blindly halve all
stored velocities; derive the unit convention and compare deterministic motion.

### Animation

If animation advances one frame per gameplay update, use half-rate animation
progress or fractional frames unless the animation is intended to become
smoother.

### RNG and event polling

Doubling Behavior calls can double RNG/event calls. Record this as a gameplay
semantic change and patch where it breaks deterministic mechanics.

## Shared phase

A patch-owned parity bit can retain 30 Hz-only subsystems:

```text
phase ^= 1
if phase == 0:
    run 30Hz-only event
```

Use only for systems that must remain 30 Hz, not as a blanket substitute for
real 60 Hz gameplay.
