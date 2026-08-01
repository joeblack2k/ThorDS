# Python — limited AR verifier

Save as `tools/thords/60fps/verify_ar.py`.

```python
#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
from pathlib import Path

def parse_lines(path: Path):
    for index, raw in enumerate(
        path.read_text(encoding="ascii").splitlines(), start=1
    ):
        text = raw.strip()
        if not text or text.startswith("#"):
            continue
        left, right = text.split()
        yield index, int(left, 16), int(right, 16)

def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("code", type=Path)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()

    conditional_depth = 0
    writes = []
    block_headers = []
    ended = False

    for source_line, code, value in parse_lines(args.code):
        kind = code >> 28
        if kind == 0x5:
            conditional_depth += 1
        elif kind == 0x0:
            writes.append({
                "line": source_line,
                "address": f"0x{0x02000000 | (code & 0x0FFFFFFF):08X}"
                    if (code & 0x0F000000) == 0
                    else f"0x{code & 0x0FFFFFFF:08X}",
                "value": f"0x{value:08X}",
                "conditionalDepth": conditional_depth,
            })
        elif kind == 0xE:
            if value % 4:
                raise ValueError(
                    f"line {source_line}: block length is not word aligned"
                )
            block_headers.append({
                "line": source_line,
                "addressLow28": f"0x{code & 0x0FFFFFFF:07X}",
                "size": value,
                "conditionalDepth": conditional_depth,
            })
        elif code == 0xD0000000:
            if conditional_depth == 0:
                raise ValueError(f"line {source_line}: unmatched D0")
            conditional_depth -= 1
        elif code == 0xD2000000:
            if conditional_depth:
                raise ValueError(
                    f"line {source_line}: D2 with open condition"
                )
            ended = True

    if conditional_depth:
        raise ValueError("unclosed condition")
    if not ended:
        raise ValueError("missing D2000000 terminator")

    payload = {
        "schemaVersion": 1,
        "writes32": writes,
        "blockHeaders": block_headers,
        "terminated": ended,
    }
    args.output.write_text(
        json.dumps(payload, indent=2, sort_keys=True) + "\n",
        encoding="utf-8",
    )
    print(json.dumps(payload, indent=2))
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
```

Luna should additionally round-trip the output through ThorDS's own
`ActionReplayCodeParser`.
