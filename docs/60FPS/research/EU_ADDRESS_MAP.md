# EU address map

| Item | Address | Role |
|---|---:|---|
| IRQ VBlank handler | `0x0201A534` | DS VBlank scheduling |
| lag callback | `0x02019100` | unconditional active-object slot 3 |
| cadence render callback | `0x02019144` | gated active-object slot 2 and OAM/display |
| main-loop slot 0 | `0x02019390` | active-object slot 0 |
| main-loop slot 1 | `0x02019404` | active-object slot 1 |
| main loop | `0x020197B8` | scheduler/main thread |
| main-loop counter | `0x020A0DB0` | loop liveness |
| cadence/delta | `0x0208EE44` | threshold + elapsed tick |
| VBlank accumulator | `0x0209D514` | cadence accumulator |
| Stage Behavior | `0x0202BBBC` | semantic candidate |
| Stage Render | `0x0202B8A4` | semantic render candidate |
| entry Behavior | `0x0211A2B8` | overlay-75 candidate |
| entry Render | `0x0211A26C` | overlay-75 candidate |
| entry Init | `0x0211A410` | writes cadence 2 |

Addresses are semantic anchors, not permission to patch without original-word
verification.
