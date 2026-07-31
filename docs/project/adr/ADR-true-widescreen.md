# ADR: Layer-Aware True Widescreen

Status: proposed; M7 diagnostic exists, M8 is not approved.

## Context

A game-side 16:9 projection change alone produces an anamorphic DS frame. The
world needs the full top rectangle, while HUD/UI must remain in a centered 4:3
safe area and the lower screen must remain 4:3.

## Decision

Use the structured Vulkan presenter path only. It already binds high-resolution
3D, current/previous 3D images, packed 2D planes and control data. The M7
developer probe renders current top 3D across the top rectangle, then draws
only the structured top plane-1 overlay within a centered 4:3 rectangle.

The mode is gated by both an app-debuggable build and the explicit
`io.github.joeblack2k.thords.extra.WIDESCREEN_PROBE` activity extra. Normal
presentation, release builds, OpenGL and safe mode remain native 4:3.

The diagnostic applies only to the primary top presentation. It uses the
current top structured-3D and capture-3D compositor inputs as a conservative
classifier: no current top 3D or any capture-3D selects a centered 4:3
composite fallback. The lower external presentation always keeps its ordinary
aspect-correct configuration.

## Rejected

- Stretching the final composited framebuffer: it cannot preserve HUD geometry.
- Shipping the known US patch addresses: they are not evidence for ASMP Europe.
- One-shot ROM-file overlay patches: overlay 6 is compressed and reloadable.

## Consequences

The M7 diagnostic proves that the existing renderer exposes independent 3D and
UI sources, but it is deliberately incomplete: it does not yet reproduce every
blend, underlay, capture and HUD path. M7 has a conservative diagnostic
classifier and fallback, but M8 still requires conditional validated runtime
writes, a fuller scene classifier and the full physical matrix before using
the True Widescreen product name.

Decision gate: `NO_GO_FOR_M8` until the M7 BOB/HUD/transition measurements are
green.
