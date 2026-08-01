# Execution order

| Gate | Purpose | Exit |
|---|---|---|
| F0 | Baseline and dossier import | current build, exact refs and scene baselines recorded |
| F1 | Semantic execution telemetry | JIT/interpreter counters proven on Thor |
| F2 | Cadence consumer map | every write/read classified |
| F3 | Developer cadence mode | exact-profile probe produces measurable 30→60 change |
| F4 | Timing-correct patch | fixed-step systems corrected and generated patch verified |
| F5 | ARM9 headroom | lowest sustaining ratio validated |
| F6 | Profile/UI/RA | requested/effective/reason and policy correct |
| F7 | Deterministic parity | timing/physics/audio comparison green |
| F8 | Stress integration | all known problem scenes and combined features green |
| F9 | Release | 60-minute soak, safety, docs and publication green |

Do not skip F1/F2 and jump directly to a public toggle.

Every gate ends with:

1. smallest relevant tests;
2. physical Thor evidence where applicable;
3. public-safety scan;
4. bounded commit;
5. push and remote-SHA verification.
