# Test and evidence policy

## Evidence hierarchy

From strongest to weakest:

1. deterministic source-level unit/integration test;
2. native telemetry or renderer-internal timestamped capture;
3. exact ADB input plus exact emulator-frame acknowledgement;
4. physical SurfaceFlinger final-display capture;
5. host-polled screenshot sequence;
6. visual impression.

A weak method must not be used repeatedly when a stronger method can be implemented.

## Two-attempt rule

When the same method produces `PARTIAL` or `BLOCKED` twice:

- stop repeating it;
- identify what information is missing;
- add the smallest debug-only instrumentation or deterministic fixture that exposes that information;
- then retry with the new method.

## Public evidence

Allowed:

- text summaries;
- redacted JSON/CSV;
- hashes;
- aggregate geometry/timing values;
- source-controlled synthetic diagrams.

Not allowed:

- raw ROM or save data;
- user/account details;
- device serials;
- unredacted logcat with URIs/tokens;
- private gameplay screenshots or video.

Raw device captures stay in a gitignored local evidence directory or `/tmp`. Public documents record only their dimensions, hashes when useful, aggregate measurements and conclusion.

## Capture validity

Before using a physical capture:

- confirm no AYN anti-image-retention overlay is active;
- confirm correct physical SurfaceFlinger display ID;
- confirm the app is foreground and the expected presentation owns the display;
- discard stale first frames after a paused state load;
- record whether the sample is internal renderer output or final physical surface.

## No OCR dependency

HUD/glyph geometry is measured from known masks/regions or deterministic image segmentation. OCR is not required and must not be used as the primary geometry proof.

## Status updates

Every workstream updates:

```text
docs/project/STATUS.md
docs/project/WORKLOG.md
docs/evidence/<milestone>/...
```

Evidence must state scope, method, result, limitations and prohibited-data cleanup.
