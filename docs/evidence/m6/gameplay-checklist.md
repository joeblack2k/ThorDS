# M6 gameplay checklist

| Scenario | Result | Evidence |
|---|---|---|
| Exact ASMP profile launch | PASS | Thor runtime log selected `sm64ds.eu.thor-enhanced` with one curated code, Slot-2 Analog, and camera override. |
| Original fallback relaunch | PASS | Thor runtime log selected `original.sm64ds.eu` with no curated code, Slot-2 Analog, or camera override after safe mode was enabled. |
| Center, 16 directions, four magnitudes, deadzone rescale | PASS | The production Android `MotionEvent` route passed 64/64 samples plus the deadzone boundary on the Thor. |
| Camera threshold, release hysteresis, diagonals | PASS | Release unit tests and the device sweep passed. |
| Slot-2 native input bridge | PASS | Every post-Android sample was handled and advanced a ready renderer frame. |
| Low/mid/full Castle Garden movement | PASS | Same-checkpoint 30-frame trials produced distinct top-world and bottom-map response above the neutral control. |
| Swim/fly/slide | BLOCKED | Not witnessed; physical control remains impractical while the reported top screen is inverted and gameplay is approximately 20fps. |
| Right-stick in-game camera | PASS | The actual camera-axis route produced a distinct 30-frame Castle Garden response and returned to neutral. |
| D-pad plus camera source ownership | PASS | HAT D-pad and right-stick camera overlap/release sequence passed without a duplicate or stuck release. |
| Controller pipeline recreation | PASS | Live settings refresh replaced `InputProcessor`, released owned input, sent Slot-2 neutral, and accepted a fresh neutral event. |
| Pause/reset/stop neutralization | PASS | Activity pause/replacement and native reset/stop paths explicitly clear Slot-2 state; release and ownership behavior is unit tested. |

No gameplay result is inferred from the title or launch screen. Full M6 remains
`PARTIAL` until swim/fly/slide and usable physical play are witnessed.
