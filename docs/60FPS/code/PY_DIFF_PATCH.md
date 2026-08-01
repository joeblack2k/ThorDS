# Python — private original/patched ROM diff

Save as `tools/thords/60fps/diff_local_patch.py`.

```python
#!/usr/bin/env python3
from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path

from nds_sections import parse as parse_sections

def sha256(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()

def changed_ranges(a: bytes, b: bytes, join_gap: int) -> list[tuple[int, int]]:
    limit = max(len(a), len(b))
    changed = []
    start = None
    last = None
    for index in range(limit):
        av = a[index] if index < len(a) else None
        bv = b[index] if index < len(b) else None
        if av != bv:
            if start is None:
                start = index
            elif last is not None and index - last - 1 > join_gap:
                changed.append((start, last + 1))
                start = index
            last = index
    if start is not None and last is not None:
        changed.append((start, last + 1))
    return changed

def classify(ranges, sections):
    result = []
    for start, end in ranges:
        overlaps = []
        for section in sections:
            s0 = section["offset"]
            s1 = s0 + section["size"]
            if start < s1 and end > s0:
                overlaps.append(section["name"])
        result.append({
            "offset": start,
            "size": end - start,
            "endExclusive": end,
            "sections": overlaps or ["unclassified"],
        })
    return result

def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("original", type=Path)
    parser.add_argument("patched", type=Path)
    parser.add_argument("--join-gap", type=int, default=0)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()

    original = args.original.read_bytes()
    patched = args.patched.read_bytes()
    original_map = parse_sections(args.original)

    payload = {
        "schemaVersion": 1,
        "original": {
            "size": len(original),
            "sha256": sha256(original),
            "gameCode": original_map["gameCode"],
            "revision": original_map["revision"],
        },
        "patched": {
            "size": len(patched),
            "sha256": sha256(patched),
        },
        "ranges": classify(
            changed_ranges(original, patched, args.join_gap),
            original_map["sections"],
        ),
    }
    payload["changedBytesCovered"] = sum(
        item["size"] for item in payload["ranges"]
    )

    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(
        json.dumps(payload, indent=2, sort_keys=True) + "\n",
        encoding="utf-8",
    )
    print(json.dumps({
        "ranges": len(payload["ranges"]),
        "changedBytesCovered": payload["changedBytesCovered"],
        "output": str(args.output),
    }, indent=2))
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
```

This deliberately outputs offsets, sizes, hashes and section labels—not ROM
bytes.
