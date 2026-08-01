# Known facts and open questions

## Known

- DS refresh is approximately 59.826 Hz.
- Most ordinary SM64DS gameplay updates every second DS frame.
- Title and star-select are exceptions.
- The main game thread sleeps and is woken from the VBlank handler according to
  `data_0208EE44`.
- That same value is consumed as a delta in multiple systems.
- Scene/overlay initializers can rewrite it.
- Existing Castle Garden measurements are not a valid 30 FPS baseline.
- The known community patch targets USA Rev1 and has significant bugs.
- A cadence-only change can require more ARM9 budget.

## Open

- Which exact EU gameplay scenes set cadence 2 in the current direct-boot flow?
- Which JIT block entries correspond to semantic Behavior/Render in each scene?
- Which fixed-step consumers require corrections?
- Does the Action Replay VBlank ordering race the ARM9 wake?
- What is the minimum sustaining ARM9 ratio on Thor?
- Which community patch changes are timing fixes versus frame unlock?
- Which systems intentionally change behavior at 60 Hz?
- What save-state metadata is necessary for timing mode?
