# Definition of done

## Semantic cadence

In known ordinary gameplay scenes:

- Original averages approximately 29.913 semantic updates/s.
- 60 FPS averages approximately 59.826 semantic updates/s.
- Render opportunities track semantic updates.
- Duplicate/unchanged game-state frames do not count as unique updates.
- Title/star-select behavior does not double.

## Timing

Over ten real minutes:

- game timer/wall-clock drift <= 0.1%;
- cutscene duration within tolerance;
- animation periods within tolerance;
- particle/effect periods within tolerance;
- audio pitch and tempo remain normal;
- no persistent audio underrun.

## Physics and gameplay

- player travel distance parity;
- jump apex/landing parity;
- gravity/fall parity;
- swimming/flying/sliding parity;
- enemy speed parity;
- platform period parity;
- object timers parity;
- Bob-omb/Yoshi/explosion behavior parity;
- Tiny-Huge Island remains completable;
- minigames remain functional.

## Performance

- minimum validated ARM9 ratio documented;
- no persistent slow motion in stress scenes;
- no renderer deadline regression outside tolerance;
- no ARM7/audio starvation;
- no thermal/performance collapse during 60-minute soak.

## Integration

- Analog passes.
- Smooth Orbit passes.
- True Widescreen passes.
- RA Off passes.
- RA Casual login/unlock path passes.
- Hardcore recovery path passes.
- save/load and relaunch pass.
- sleep/wake and reconnect pass.
- wrong ROM/revision/hash fail closed.

## Engineering

- semantic telemetry tests pass;
- cadence scanner output reviewed;
- deterministic patch build passes;
- AR verification passes;
- profile tests pass;
- full Gradle build passes;
- core fork commit is public and fetchable when changed;
- clean recursive clone resolves all SHAs;
- public scans are clean.

## Release

- 60-minute physical Thor soak passes;
- product toggle is no longer labeled experimental;
- ADR, status, worklog and final report are updated;
- all bounded commits are pushed.
