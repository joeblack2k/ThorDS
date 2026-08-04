# F8 Analog and Smooth Orbit Trials

Status: PARTIAL

## Runtime

- Device: AYN Thor
- ROM: exact EU ASMP revision 0
- RA hash: `ba3c4052e00c5cc31df5d5534c39de1b`
- Renderer: Vulkan
- Mode: Enhanced
- Cadence probe: disabled

## Analog trial

The active game received a developer controller-motion event with:

- left stick X: `0.70`
- left stick Y: `-0.35`
- camera X: `0.45`
- camera Y: `0.00`
- stepped frames: `60`

Observed result:

- input handled: `true`;
- neutral input before and after: `true`;
- renderer frame advanced: `true`;
- frame shape valid: `true`;
- upper changed pixels: `48428`;
- result: `PASS`.

This proves the debug input path and a visible response. It does not prove
full Analog gameplay parity in every stress scene.

## Smooth Orbit trial

The active game received a camera X input of `0.65` for `1200 ms`.

Observed result:

- input handled: `true`;
- camera `yawInputQ12`: `0` before, `1914` during input;
- `yawUnitsPerTick`: `1001`;
- frame shape valid: `true`;
- changed pixels: `0`;
- result: `PASS` for input-state handling only.

The unchanged capture is not visual camera evidence. Smooth Orbit remains
unproven for the combined product gate until a live gameplay capture changes
with the camera state.

## Conclusion

Analog has a bounded live input response pass. Smooth Orbit has a bounded
input-state response pass, but not a visual response pass. The combined F8
matrix remains open.
