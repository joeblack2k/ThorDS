# Risk register

| Risk | Failure mode | Mitigation |
|---|---|---|
| Duplicate axis ownership | Right stick still emits D-pad | Reserve camera axes before generic mapper |
| Wrong camera state | Cutscene camera moves | Gate inside normal orbit state and fail closed |
| Unverified injection space | Runtime corruption | Prefer in-place block; otherwise prove code region |
| Overlay timing | Arrows remain | Trace overlay load and effective words |
| Global sound suppression | Other sounds disappear | Remove trigger, never mute ID globally |
| Savestate drift | Camera remains turning | Camera state transient; neutralize before/after load |
| Sequence reset | Spurious recenter | Monotonic session sequence; no lifecycle reset |
| 30/60 mismatch | Sensitivity doubles | Explicit `yawUnitsPerTick` protocol field |
| Core commit unavailable | Clone fails | Public fork + exact submodule object |
| Profile mismatch | Patch hits wrong ROM | game code + revision + RA hash + guarded words |
| Original regression | Touch controls altered | Full Original comparison after every patch revision |
| Evidence leak | ROM/private capture published | text-only redacted evidence and staged scans |
