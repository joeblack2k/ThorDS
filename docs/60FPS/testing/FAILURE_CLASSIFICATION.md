# Failure classification

## Performance debt

Symptoms:

- all systems slow together;
- audio may remain correct or underrun;
- semantic rate falls below target.

Response:

- ARM9 headroom;
- optimize patch/telemetry;
- profile renderer load.

## Fixed-step logic

Symptoms:

- enemies too fast;
- timers half duration;
- specific object broken while global rate is stable.

Response:

- consumer-specific timestep correction.

## Cadence/reset

Symptoms:

- mode reverts after transition;
- semantic rate alternates 30/60.

Response:

- initializer/overlay patch and lifecycle reapply.

## Presentation

Symptoms:

- semantic rate correct but repeated frames.

Response:

- render callback/compositor investigation, not gameplay speed hacks.
