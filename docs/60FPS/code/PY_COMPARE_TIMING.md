# Python — compare Original and 60 FPS runs

Save as `tools/thords/60fps/compare_timing_runs.py`.

```python
#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
from pathlib import Path

DEFAULT_TOLERANCES = {
    "wallClockDriftPercent": 0.1,
    "playerDistancePercent": 1.0,
    "jumpDurationPercent": 1.0,
    "fallDurationPercent": 1.0,
    "animationPeriodPercent": 1.0,
    "audioDurationPercent": 0.5,
}

def percent_delta(a: float, b: float) -> float:
    if a == 0:
        return 0.0 if b == 0 else float("inf")
    return abs(b - a) * 100.0 / abs(a)

def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("original", type=Path)
    parser.add_argument("fps60", type=Path)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()

    original = json.loads(args.original.read_text())
    fps60 = json.loads(args.fps60.read_text())
    metrics = {}

    for name, tolerance in DEFAULT_TOLERANCES.items():
        metric = name.removesuffix("Percent")
        if metric not in original or metric not in fps60:
            continue
        delta = percent_delta(
            float(original[metric]),
            float(fps60[metric]),
        )
        metrics[metric] = {
            "original": original[metric],
            "fps60": fps60[metric],
            "deltaPercent": delta,
            "tolerancePercent": tolerance,
            "pass": delta <= tolerance,
        }

    payload = {
        "schemaVersion": 1,
        "metrics": metrics,
        "pass": bool(metrics) and all(
            item["pass"] for item in metrics.values()
        ),
    }
    args.output.write_text(
        json.dumps(payload, indent=2, sort_keys=True) + "\n"
    )
    print(json.dumps(payload, indent=2))
    return 0 if payload["pass"] else 1

if __name__ == "__main__":
    raise SystemExit(main())
```

Extend the metric list only with documented units and tolerances.
