# Native Vulkan Capture And Display Comparison

Status: PARTIAL

## Runtime

- Device: AYN Thor
- Package: `io.github.joeblack2k.thords.dev`
- Renderer: Vulkan
- ROM: SM64DS EU ASMP revision 0
- RA hash: `ba3c4052e00c5cc31df5d5534c39de1b`

## Evidence

The autonomous title and save flow was run on both DS displays. The debug
renderer capture was then collected from the running Vulkan session.

Native capture files:

- `prod-native-check_screenFrame.png`
- `prod-native-check_compositedFrame.png`
- `prod-native-check_renderer3dFrame.png`
- `prod-native-check_packedTopPrimary.png`
- `prod-native-check_packedBottomPrimary.png`

The native screen and composited frame contained valid SM64DS pixels. The
frame showed the title image, touch prompt, Mario animation, and both DS
screens. The renderer log also reported non-zero geometry:

- triangles: `2292`
- opaque draws: `930`
- alpha/shadow draws: non-zero

The Android physical screen capture taken at the same stage was white or
corrupted.

## Conclusion

This separates the failure boundary:

- Vulkan native rendering and compositing produced valid pixels.
- The white physical capture is downstream of native rendering.
- The result does not prove that the user-visible Thor displays are correct.
- The result does not prove 60 FPS gameplay.
- The dual-display SurfaceView or Thor display/capture path remains open.

No timing or pose code was changed for this result.
