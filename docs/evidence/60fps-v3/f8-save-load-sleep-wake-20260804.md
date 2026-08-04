# F8 Save Load Sleep Wake

Status: PASS for the bounded integration check

## Runtime

- Device: AYN Thor
- ROM: SM64DS EU ASMP revision 0
- Renderer: Vulkan
- ARM9 request: `125%`
- Cadence probe: disabled for this integration check

## Results

The autonomous run completed:

1. Save state to slot 1: `success=1`.
2. Load state from slot 1: `success=1`.
3. Android sleep by power-button key event.
4. Android wake by power-button key event.
5. Continued native telemetry after wake.

The emulator remained alive after load and wake. No FATAL exception or ANR was
observed.

## Limits

This is a bounded integration check. It does not prove controller reconnect,
full save-file parity, timing parity, audio parity, all stress scenes, or the
final product patch.
