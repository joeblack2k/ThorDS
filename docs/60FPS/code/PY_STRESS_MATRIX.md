# Python — stress matrix

Save as `tools/thords/60fps/stress_matrix.py`.

```python
#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
from pathlib import Path

REQUIRED = [
    "bob_spawn",
    "chain_chomp",
    "mountain_path",
    "king_bobomb",
    "bobomb_explosion",
    "yoshi_mouth",
    "tiny_huge_island",
    "swimming",
    "flying",
    "sliding",
    "caps",
    "moving_platform",
    "door_transition",
    "star_select",
    "cutscene",
    "minigame",
    "sleep_wake",
    "save_load",
    "controller_reconnect",
    "combined_ra_casual",
]

def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("results", type=Path)
    parser.add_argument("--markdown", type=Path, required=True)
    args = parser.parse_args()

    data = json.loads(args.results.read_text(encoding="utf-8"))
    by_id = {item["id"]: item for item in data.get("tests", [])}

    lines = [
        "# SM64DS 60 FPS stress matrix",
        "",
        "| Test | Status | Semantic FPS | ARM9 | Notes |",
        "|---|---|---:|---:|---|",
    ]
    passed = True
    for test_id in REQUIRED:
        item = by_id.get(test_id, {})
        status = item.get("status", "MISSING")
        passed &= status == "PASS"
        lines.append(
            f"| `{test_id}` | {status} | "
            f"{item.get('semanticFps', '')} | "
            f"{item.get('arm9Percent', '')} | "
            f"{str(item.get('notes', '')).replace('|', '\\|')} |"
        )

    lines += ["", f"Overall: `{'PASS' if passed else 'FAIL'}`", ""]
    args.markdown.write_text("\n".join(lines), encoding="utf-8")
    return 0 if passed else 1

if __name__ == "__main__":
    raise SystemExit(main())
```
