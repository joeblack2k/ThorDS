# M6 gameplay checklist

| Scenario | Result | Evidence |
|---|---|---|
| Exact ASMP profile launch | PASS | Thor runtime log selected `sm64ds.eu.thor-enhanced` with one curated code, Slot-2 Analog, and camera override. |
| Original fallback relaunch | PASS | Thor runtime log selected `original.sm64ds.eu` with no curated code, Slot-2 Analog, or camera override after safe mode was enabled. |
| Center, cardinals, diagonals, deadzone rescale | PASS | `Slot2AnalogMappingTest` release unit test. |
| Camera threshold, release hysteresis, diagonals | PASS | `CameraDpadHysteresisTest` release unit test. |
| Slot-2 native input bridge | PASS | Thor debug command accepted nonzero and neutral Slot-2 values in a running enhanced session. |
| Continuous walk/run/sneak | NOT_YET_WITNESSED | Requires a human gameplay observation on the physical controls. |
| Swim/fly/slide | NOT_YET_WITNESSED | Requires a human gameplay observation on the physical controls. |
| Right-stick in-game camera | NOT_YET_WITNESSED | The mapped hysteresis path is tested; camera response still needs gameplay observation. |
| Controller reconnect | NOT_YET_WITNESSED | Requires a physical disconnect/reconnect cycle. |

No gameplay result is inferred from the title or launch screen.
