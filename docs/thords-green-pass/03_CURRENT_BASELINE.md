# Current baseline

## Repository

```text
origin:       https://github.com/joeblack2k/ThorDS
main HEAD:    a2aa88e58031b15a5a77abe33feb2d7fe70a3721
base:         SapphireRhodonite/melonDS-android 0.7.0.rc5
base commit:  9b28076281545a1e08dccee0b3f925febb8933ac
```

The supplied source archive and public `main` both identify `a2aa88e5` as the minimum continuation point. A newer descendant is authoritative.

## Source pins

```text
SM64DS decomp: 2d38fe9b825199deec408240849b64b91c965d85
AM64DS:        d3ae02560c32c402672036677e06e0df6e692fd1
RA game ID:    9983
SM64DS EU RA:  ba3c4052e00c5cc31df5d5534c39de1b
```

## Milestone state at baseline

| Milestone | State | Meaning |
|---|---|---|
| M0-M5 | PASS | workspace, Thor hardware, product identity, profile engine and exact EU profile are complete |
| M6 | IN_PROGRESS | analog code and plumbing exist; end-to-end gameplay acceptance is open |
| M7 | IN_PROGRESS | strong developer widescreen spike; W01/W03/W04/W05/W06/W20 not all closed |
| M8 | PASS | exact-profile product mode, layer-aware fallback and Thor smoke validated; see `docs/evidence/m8/` |
| M9 | IN_PROGRESS | RA policy and pre-bootstrap gate exist; complete UI/network/device acceptance is open |
| M10 | IN_PROGRESS | policy-only; effective runtime remains 100% |
| M11 | IN_PROGRESS | read-only pause status exists; full enhancement UX is open |
| M12 | IN_PROGRESS | release preflight inventory is blocked |
| M13 | NOT_STARTED | no true 60fps implementation |

## Existing code that must be reused

- deterministic enhancement profile catalog and resolver;
- exact EU SM64DS profile;
- verified AM64DS runtime payload;
- Slot-2 Analog Input path;
- radial analog normalization;
- right-stick camera hysteresis;
- profile-aware RA policy and pre-bootstrap gate;
- ARM9 requested/effective policy model;
- session status snapshot in pause menu;
- Vulkan structured 2D/3D compositor and developer widescreen probe;
- debug launch, state, frame-step, renderer capture and direct-input harnesses;
- Thor display mapping, touch mapping, safe mode and product identity.

## Remaining M7 facts

- Castle Garden is the owner-selected M7 representative scene.
- W01 is partial: paired final-primary captures and a 0.39% diagnostic ratio delta exist, but not a formal reference-object test.
- W03 is partial: side regions change coherently, but no semantic landmark is tracked wholly inside one side ROI.
- W06 is partial: native bottom raster is proven, physical final placement is not.
- W20 is partial with an observed transition; continuous no-flash cadence/soak remains open.
- Exact HUD circle and glyph geometry remain open.

These gaps are solved through improved instrumentation and product code, not by repeating the same host-polled capture method indefinitely.
