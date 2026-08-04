# Semantic Cadence Disposition

Status: PARTIAL

The binary map and pinned EU source show two different timing classes.

## Delta-aware consumers

These functions read `data_0208ee44` and use it as an elapsed update value:

| Address | Function | Disposition |
|---|---|---|
| `0x0202BBBC` | `Stage::Behavior` | Retain delta use. Verify values in ordinary gameplay. |
| `0x0202635C` | `Stage::PS_Update` | Retain delta use for menu and transition timers. |
| `0x0201C0B8` | `Message::Update` | Retain delta use. Verify division and bitmask branches. |
| `0x020242C8` | `Stage::LC_Update` | Retain delta use. Scene-specific validation remains open. |
| `0x0201CB7C` | `Stage::UpdateMessage` | Retain delta use. |
| `0x0201A534` | `IRQ::VBlankHandler` | Scheduler boundary. Do not treat as a gameplay consumer. |

## Fixed-step candidates

`Player_AdvanceAnims` at `0x020BEDD4` does not read
`data_0208EE44`. It advances the active body animation and the auxiliary
player animation once per player behavior call. The player state methods call
this helper directly, including the jump state.

This is a fixed-step animation candidate. It is separate from the cadence
delta consumers and requires a player-only timing design. A global
`Animation::Advance` change is not authorized.

## Current disposition

- No delta-aware consumer is patched by this report.
- No fixed-step consumer is promoted to a runtime patch.
- The old pose payload remains quarantined.
- A source-derived player-only design must preserve the game-loop counter,
  avoid a second `Animation::Advance` sample, and pass live transform telemetry.

This report is a semantic source disposition, not F4 or F7 acceptance.

