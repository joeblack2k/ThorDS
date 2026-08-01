# Python — NDS section map

Save as `tools/thords/60fps/nds_sections.py`.

```python
#!/usr/bin/env python3
from __future__ import annotations

import argparse
import hashlib
import json
import struct
from dataclasses import asdict, dataclass
from pathlib import Path

@dataclass(frozen=True)
class Section:
    name: str
    offset: int
    size: int
    runtime_address: int | None = None
    overlay_id: int | None = None

def u32(data: bytes, offset: int) -> int:
    return struct.unpack_from("<I", data, offset)[0]

def sha256(path: Path) -> str:
    h = hashlib.sha256()
    with path.open("rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()

def parse_overlay_table(
    rom: bytes,
    table_offset: int,
    table_size: int,
    fat_offset: int,
    fat_size: int,
    prefix: str,
) -> list[Section]:
    if table_size % 32:
        raise ValueError(f"{prefix} overlay table is not 32-byte aligned")
    sections: list[Section] = []
    fat_entries = fat_size // 8
    for pos in range(table_offset, table_offset + table_size, 32):
        overlay_id, ram_addr, ram_size, _, _, _, file_id, flags = \
            struct.unpack_from("<8I", rom, pos)
        if file_id >= fat_entries:
            raise ValueError(f"{prefix} overlay {overlay_id}: bad file id")
        start, end = struct.unpack_from("<2I", rom, fat_offset + file_id * 8)
        sections.append(Section(
            name=f"{prefix}-overlay-{overlay_id}",
            offset=start,
            size=end - start,
            runtime_address=ram_addr,
            overlay_id=overlay_id,
        ))
    return sections

def parse(path: Path) -> dict:
    rom = path.read_bytes()
    if len(rom) < 0x160:
        raise ValueError("ROM too small")

    sections = [
        Section("header", 0, 0x200),
        Section("arm9", u32(rom, 0x20), u32(rom, 0x2C), u32(rom, 0x28)),
        Section("arm7", u32(rom, 0x30), u32(rom, 0x3C), u32(rom, 0x38)),
        Section("fnt", u32(rom, 0x40), u32(rom, 0x44)),
        Section("fat", u32(rom, 0x48), u32(rom, 0x4C)),
        Section("banner", u32(rom, 0x68), 0xA00),
        Section("arm9-overlay-table", u32(rom, 0x50), u32(rom, 0x54)),
        Section("arm7-overlay-table", u32(rom, 0x58), u32(rom, 0x5C)),
    ]

    fat_offset = u32(rom, 0x48)
    fat_size = u32(rom, 0x4C)
    sections += parse_overlay_table(
        rom, u32(rom, 0x50), u32(rom, 0x54),
        fat_offset, fat_size, "arm9",
    )
    sections += parse_overlay_table(
        rom, u32(rom, 0x58), u32(rom, 0x5C),
        fat_offset, fat_size, "arm7",
    )

    return {
        "schemaVersion": 1,
        "size": len(rom),
        "sha256": sha256(path),
        "gameCode": rom[0x0C:0x10].decode("ascii", errors="replace"),
        "revision": rom[0x1E],
        "sections": [asdict(section) for section in sections],
    }

def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("rom", type=Path)
    parser.add_argument("--output", type=Path)
    args = parser.parse_args()
    payload = parse(args.rom)
    text = json.dumps(payload, indent=2, sort_keys=True) + "\n"
    if args.output:
        args.output.write_text(text, encoding="utf-8")
    else:
        print(text, end="")
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
```
