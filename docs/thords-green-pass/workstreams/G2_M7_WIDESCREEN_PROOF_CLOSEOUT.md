# G2 — M7 True Widescreen proof closeout

## Goal

Close the remaining Castle Garden proof gates. Do not productize until this contract is green.

## Key change in method

The prior host-polled captures are insufficient for several gates. Add a debug-only deterministic measurement layer instead of repeating them.

### W01 reference geometry

Use two complementary proofs:

1. existing game projection/FOV evidence;
2. a deterministic renderer calibration primitive or an explicitly identified game object with stable mask metadata.

The calibration must traverse the same world sampling path and final presenter transform. Compare native 4:3 and 16:9 bboxes across a controlled camera movement. Ratio delta ≤2%.

### W03 side culling

Create a controlled exact-step camera/input replay that keeps one named landmark wholly within a left or right side ROI for multiple consecutive frames. Track its bbox/center. If the natural camera path cannot do this, select a different Castle Garden checkpoint or add a debug camera/input fixture; do not repeat an unsuitable path.

### W04/W05 HUD and glyphs

Capture the UI-safe plane and 4:3 reference internally. Segment a known circular indicator/calibration circle and selected glyphs. Compare aspect within 2%. No OCR required.

### W06 physical bottom

Capture the final physical lower display using the verified SurfaceFlinger ID after confirming window ownership. Measure the actual destination rect, orientation and aspect, then rerun the existing 3×3 touch mapping.

### W20 transition

Add a renderer-internal bounded frame ring or timestamped final-surface capture around input events. Prove every consecutive frame through world→pause→world plus at least one painting/star-style transition. Host polling alone is not sufficient.

## Exit

All W01, W02, W03, W04, W05, W06 and W20 are `PASS`; the ADR states exactly what M8 may productize.

## Suggested commits

```text
test: add deterministic widescreen geometry instrumentation
render: close SM64DS structured widescreen proof gates
```
