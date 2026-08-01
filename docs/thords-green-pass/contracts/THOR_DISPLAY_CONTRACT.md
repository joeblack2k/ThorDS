# Thor display contract

Known roles:

```text
Top:    Built-in Screen, 1920×1080
Bottom: Screen-2, 1240×1080, presentation-capable touchscreen
```

Do not hardcode logical display IDs as stable identity. Classify by current device/display capabilities and names already supported by the project.

## During gameplay

- DS top/world on physical top;
- DS bottom on physical bottom;
- lower touch maps exactly to 256×192 content rect;
- no soft controls by default;
- no focus theft from a secondary Presentation.

## Evidence

For physical final geometry, use the current SurfaceFlinger physical ID discovered at runtime, confirm window ownership and record only aggregate dimensions/rects publicly.
