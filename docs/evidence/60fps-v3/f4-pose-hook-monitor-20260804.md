# F4 Pose Hook Monitor Evidence

Date: 2026-08-04
Status: PARTIAL

## Result

- The generated pose payload uses the checked-in source, compiled object,
  relocation-aware builder, and verifier.
- The corrected developer payload installs the expected ARM9 branch words.
- The pose profile is developer-only and `defaultEnabled` is false.
- Normal Enhanced remains limited to the accepted curated codes.
- The live pose hook word changed to `0xEAFFB9A1` in gameplay.
- The first execution monitor returned zero because the JIT monitor skipped
  the first instruction of a compiled block.
- A separate core fix now monitors the second pose-payload instruction at
  `0x02004DFC` and maps it to the player-pose event.

## Acceptance

F4 is not green. A rebuilt APK is required to test the new monitor on the
device. The current local build is blocked by the Rust wrapper passing
`+stable` directly to Cargo, while the installed binary requires rustup for
toolchain selection.

The pose enhancement remains disabled by default and is not part of the normal
Enhanced forced set.
