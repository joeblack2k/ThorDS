# Non-negotiables

1. Exact identity only: `ASMP`, revision `0`, RA hash `ba3c4052e00c5cc31df5d5534c39de1b`.
2. Original profile remains original.
3. No ROM or save in Git, history, APK, ZIP, evidence or release.
4. Runtime patch only; no distributed patched ROM.
5. Do not map ordinary right-stick movement to DS D-pad bits.
6. Do not globally mute a sound ID.
7. Do not overwrite final camera position from the emulator.
8. Preserve original camera collision and scripted states.
9. Normal right-stick yaw must be proportional and continuous.
10. R3 produces one recenter edge, not a held left+right combination.
11. Smooth camera state is transient and neutralized on lifecycle boundaries.
12. Mode 0/1 of the existing Slot-2 analog protocol must remain compatible.
13. No unverified code cave.
14. Every runtime patch write is guarded by exact expected words or an equally strong check.
15. No silent RA downgrade.
16. Hardcore requires Original plus relaunch.
17. No force-push.
18. No false `PASS`.
