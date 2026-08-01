# Python — Action Replay generator

Save as `tools/thords/60fps/generate_ar.py`.

Manifest format:

```json
{
  "guardsAndWrites32": [
    {
      "address": "0x0208EE44",
      "expected": "0x00000002",
      "value": "0x00000001"
    }
  ],
  "blocks": []
}
```

```python
#!/usr/bin/env python3
from __future__ import annotations

import argparse
import hashlib
import json
import struct
from pathlib import Path

def number(value) -> int:
    return int(value, 0) if isinstance(value, str) else int(value)

def line(code: int, value: int) -> str:
    return f"{code & 0xFFFFFFFF:08X} {value & 0xFFFFFFFF:08X}"

def guard_code(address: int) -> int:
    return 0x50000000 | (address & 0x0FFFFFFF)

def write32_code(address: int) -> int:
    return address & 0x0FFFFFFF

def block_code(address: int) -> int:
    return 0xE0000000 | (address & 0x0FFFFFFF)

def emit(manifest: dict) -> list[str]:
    output: list[str] = []
    for item in manifest.get("guardsAndWrites32", []):
        address = number(item["address"])
        expected = number(item["expected"])
        value = number(item["value"])
        if address & 3:
            raise ValueError(f"unaligned write32 address 0x{address:08X}")
        output += [
            line(guard_code(address), expected),
            line(write32_code(address), value),
            line(0xD0000000, 0),
        ]

    for item in manifest.get("blocks", []):
        address = number(item["address"])
        data = Path(item["binary"]).read_bytes()
        if address & 3:
            raise ValueError(f"unaligned block address 0x{address:08X}")
        if len(data) % 4:
            data += bytes(4 - len(data) % 4)
        output.append(line(block_code(address), len(data)))
        for offset in range(0, len(data), 8):
            chunk = data[offset:offset + 8].ljust(8, b"\0")
            a, b = struct.unpack("<II", chunk)
            output.append(line(a, b))

    output.append(line(0xD2000000, 0))
    return output

def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("manifest", type=Path)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--metadata", type=Path, required=True)
    args = parser.parse_args()

    manifest = json.loads(args.manifest.read_text(encoding="utf-8"))
    lines = emit(manifest)
    canonical = "\n".join(lines) + "\n"
    args.output.write_text(canonical, encoding="ascii")
    digest = hashlib.sha256(canonical.encode("ascii")).hexdigest()
    metadata = {
        "schemaVersion": 1,
        "lineCount": len(lines),
        "sha256": digest,
        "output": args.output.as_posix(),
    }
    args.metadata.write_text(
        json.dumps(metadata, indent=2, sort_keys=True) + "\n",
        encoding="utf-8",
    )
    print(json.dumps(metadata, indent=2))
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
```

The generator intentionally supports only the code types ThorDS already uses
for guarded writes and block injection. Extend only with tests.
