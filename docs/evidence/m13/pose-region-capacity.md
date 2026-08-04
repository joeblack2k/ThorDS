# Pose Region Capacity

The compiled player-only pose implementation needs `0x224` bytes:

- `Player_AdvanceAnims`: `0x100`
- `update_temporal_pose`: `0xD4`
- `ModelAnim::UpdateVerts`: `0x50`

The original pose region provides `0x208` bytes. The world payload occupies
`0x02004B00..0x02004C8C`, leaving a proven zero-filled tail of
`0x02004C8C..0x02004D00` (`0x74` bytes).

The runtime region manifest now reserves:

```text
02004B00..02004C8C  world payload
02004C8C..02004D00  pose helper code
02004D00..02004DF8  player timestep
02004DF8..02005000  player pose entry and model update
```

The overlap audit passes. The pose helper is still developer-only and no
runtime payload has been generated or enabled.

