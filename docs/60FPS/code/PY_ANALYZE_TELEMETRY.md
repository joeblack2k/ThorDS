# Python — analyze semantic telemetry

Save as `tools/thords/60fps/analyze_semantic_telemetry.py`.

Input: JSON Lines, one native window per line.

```python
#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import statistics
from pathlib import Path

RATE_FIELDS = (
    "mainLoopIterations",
    "slot1",
    "cadenceRender",
    "lagCallback",
    "stageBehavior",
    "stageRender",
    "entryBehavior",
    "entryRender",
    "presentedFrames",
    "uniqueGameStates",
)

def load(path: Path) -> list[dict]:
    result = []
    for line in path.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if not line:
            continue
        if line.startswith("{"):
            result.append(json.loads(line))
            continue
        marker = line.find("{")
        if marker >= 0:
            result.append(json.loads(line[marker:]))
    return result

def rate(sample: dict, field: str) -> float | None:
    wall = sample.get("windowWallNs")
    value = sample.get(field)
    if not wall or value is None:
        return None
    return float(value) * 1_000_000_000.0 / float(wall)

def summarize(samples: list[dict]) -> dict:
    summary = {"windows": len(samples), "rates": {}}
    for field in RATE_FIELDS:
        values = [
            value
            for sample in samples
            if (value := rate(sample, field)) is not None
        ]
        if not values:
            continue
        summary["rates"][field] = {
            "count": len(values),
            "mean": statistics.fmean(values),
            "median": statistics.median(values),
            "min": min(values),
            "max": max(values),
            "stdev": statistics.pstdev(values),
        }
    summary["cadenceValues"] = sorted({
        sample.get("cadenceValue")
        for sample in samples
        if sample.get("cadenceValue") is not None
    })
    summary["arm9Percents"] = sorted({
        sample.get("arm9Percent")
        for sample in samples
        if sample.get("arm9Percent") is not None
    })
    return summary

def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("input", type=Path)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()

    payload = summarize(load(args.input))
    args.output.write_text(
        json.dumps(payload, indent=2, sort_keys=True) + "\n",
        encoding="utf-8",
    )
    print(json.dumps(payload, indent=2))
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
```
