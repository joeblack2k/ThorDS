# ADR: Layer-Aware True Widescreen

Status: accepted; M7 proof is green and M8 productization is approved.

## Context

A game-side 16:9 projection change alone produces an anamorphic DS frame. The
world needs the full top rectangle, while HUD/UI must remain in a centered 4:3
safe area and the lower screen must remain 4:3.

## Decision

Use the structured Vulkan presenter path only. It already binds high-resolution
3D, current/previous 3D images, packed 2D planes and control data. The M7
developer probe renders current top 3D across the top rectangle, then draws
only the structured top plane-1 overlay within a centered 4:3 rectangle.

The M7 diagnostic remains gated by both an app-debuggable build and the
explicit `io.github.joeblack2k.thords.extra.WIDESCREEN_PROBE` activity extra.
M8 may replace that developer-only route with an exact-profile product mode,
but OpenGL, software rendering, safe mode and unsupported profiles must remain
native 4:3.

The diagnostic applies only to the primary top presentation. It uses the
dominant current top structured-3D slot and capture-3D compositor inputs as a
conservative classifier: no dominant top 3D slot or any capture-3D selects a
centered 4:3 composite fallback. The lower external presentation always keeps
its ordinary aspect-correct configuration.

## Rejected

- Stretching the final composited framebuffer: it cannot preserve HUD geometry.
- Shipping the known US patch addresses: they are not evidence for ASMP Europe.
- One-shot ROM-file overlay patches: overlay 6 is compressed and reloadable.

## Consequences

The M7 deterministic matrix proves:

- game-side 16:9 projection exposes extra Castle Garden world at the sides;
- named world geometry remains within `2%` aspect tolerance;
- a named animated side landmark remains present across exact frames;
- top UI-safe circle/glyph geometry remains within `2%`;
- the physical lower display remains exact 4:3 and touch-correct;
- world-pause-world and an owner-selected Castle Garden key-door
  cinematic/scene transition present continuously without black or stale-return
  frames.

M8 may productize the exact ASMP revision-0 profile on supported Thor Vulkan
sessions using guarded runtime projection/culling code, full-width structured
3D, centered 4:3 plane-1 UI, the existing conservative capture/ambiguity
fallback and the physical lower 4:3 path.

M8 must still add normal requested/effective UI state, launch-plan integration,
scene-classifier hysteresis, release-build availability checks, relaunch
persistence and its broader product scene matrix. M7 does not by itself make
the developer extra a shipping feature.

Decision gate: `GO_FOR_M8_PRODUCTIZATION`.
