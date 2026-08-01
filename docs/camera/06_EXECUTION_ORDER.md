# Execution order

Do not implement the runtime patch first. Use this order.

| Gate | Purpose | Exit condition |
|---|---|---|
| C0 | Baseline and liveness proof | Existing 45° behavior, UI path, sound trigger and hook registers documented |
| C1 | Frontend smooth input | Pure mapping, axis ownership, neutralization and R3 sequence tests pass |
| C2 | Core protocol | Mode 2 registers work; mode 0/1 regressions pass; core fork pushed |
| C3 | JNI/session bridge | Exact Enhanced session transports state and neutralizes lifecycle |
| C4 | EU runtime patch | Guarded generated patch changes yaw continuously and mismatch fails closed |
| C5 | HUD/audio cleanup | Both arrow paths suppressed; normal yaw sound trigger absent |
| C6 | Settings/profile | Effective state, recovery path and RA policy are correct |
| C7 | Full Thor acceptance | Matrix and 60-minute soak pass |

Each gate should end in:

1. tests;
2. source/diff review;
3. public-safe evidence;
4. bounded commit;
5. push and remote SHA verification.
