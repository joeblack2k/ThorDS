# Product decision

## Approved target

ThorDS Enhanced will ship an exact-European **game-side 60 FPS mode**.

It is not:

- a 60 Hz Android surface displaying duplicated 30 FPS frames;
- frame interpolation;
- fast-forward;
- an opaque patched USA ROM;
- a one-byte toggle with unverified physics;
- a renderer-only setting.

## Required user experience

```text
Original profile:
  original SM64DS cadence

Thor Enhanced:
  Analog
  Smooth Orbit Camera
  True Widescreen
  60 FPS
  validated ARM9 headroom
  RA Off or Casual
```

## Product naming

Before validation:

```text
60 FPS — Experimental
```

After the complete gate:

```text
60 FPS
```

## Core design decision

The first implementation route is a guarded exact-ROM runtime patch using the
existing curated Action Replay engine. Core work is reserved for:

- semantic execution telemetry;
- ARM9 overclock headroom;
- capabilities that the runtime patch cannot safely express.

## Why the community binary is not the product

The known USA Rev1 patch is useful as a behavioral reference, but public users
report both performance slowdowns and broken game logic. The EU patch must
therefore be source-derived and independently validated.
