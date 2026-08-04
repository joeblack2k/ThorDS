# F5 ARM9 Ratio Smoke Test

Status: PARTIAL

## Scope

This was an autonomous Vulkan smoke test on the exact EU SM64DS identity:

- Device: AYN Thor
- ROM: ASMP revision 0
- ROM key: `asmp:0:ba3c4052e00c5cc31df5d5534c39de1b`
- Tested ratios: 100%, 125%, 150%, 175%, 200%

Each ratio was applied in ascending order. The app relaunched and reached the
Vulkan runtime. Native ARM9 telemetry reported the requested ratio and matching
ARM9 and ARM7 wall-clock domains. No FATAL exception or ANR was observed.

## Observed telemetry

| Requested | ARM9 telemetry | VBlank window |
| ---: | --- | ---: |
| 100% | `percent=100`, `scaledCycles=357030428` | 60 |
| 125% | `percent=125`, `scaledCycles=313239179` | 60 |
| 150% | `percent=150`, `scaledCycles=270528244` | 60 |
| 175% | `percent=175`, `scaledCycles=239551354` | 60 |
| 200% | `percent=200`, `scaledCycles=212422871` | 61 |

The telemetry also showed the ARM9 timestamp tracking twice the ARM7
timestamp, as expected for the dual-clock comparison.

## Limits

This is not an F5 pass. The observed windows were mainly title or transition
states. The 100% and 125% live gameplay checks were normal Enhanced baseline
runs with the cadence probe disabled and therefore showed about 30 semantic
gameplay updates per second.

The later attempt to combine 125% with the developer cadence probe used a
relaunch preference race and ended with `percent=100`; that result is rejected.
No ratio is selected for the final product from this record.
