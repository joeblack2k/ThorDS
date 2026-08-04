# F3 Explicit Probe A/B

Status: PARTIAL

## Test setup

- Device: AYN Thor
- ROM: exact EU ASMP revision 0
- RA hash: `ba3c4052e00c5cc31df5d5534c39de1b`
- Renderer: Vulkan
- Flow: inspect upper and lower title displays, touch lower `Adventure`,
  select lower File A, then inspect upper gameplay

The new APK was tested in two separate relaunches. Normal Enhanced was tested
with the developer probe disabled. The second run enabled
`60fps-dev-cadence` explicitly through the debug-only exact-profile command.

## Results

| Mode | Cadence | Unique updates/s | Emulator frames/s | Stage Behavior/s | Player Behavior/s |
|---|---:|---:|---:|---:|---:|
| Normal Enhanced | 2 | 30-31 | 60-61 | 30-31 | 30-31 |
| Explicit developer probe | 1 | 60-61 | 60-61 | 60-61 | 60-61 |

The explicit probe reached live Yoshi gameplay. The log contained no
`FATAL EXCEPTION` and no `ANR in` entry.

## Decision

The probe produces a measurable semantic 30-to-60 change. This satisfies the
F3 falsification experiment only.

It does not satisfy F4 or the product gate. The probe changes update frequency
without proving wall-clock timing, physics parity, animation parity, audio
parity or stress-scene behavior. It remains developer-only and was disabled
after the test.
