#!/usr/bin/env python3
"""Fail closed when composed enhancement payloads overlap."""
from __future__ import annotations

import argparse
import json
from dataclasses import dataclass
from pathlib import Path


@dataclass(frozen=True, order=True)
class Region:
    start: int
    end: int
    owner: str
    purpose: str

    @classmethod
    def from_json(cls, value: dict) -> "Region":
        start = int(value["start"], 16)
        end = int(value["endExclusive"], 16)
        if start >= end or start % 4 or end % 4:
            raise ValueError(f"invalid region: {value}")
        return cls(start, end, value["owner"], value["purpose"])


def audit(regions: list[Region]) -> list[str]:
    errors = []
    for previous, current in zip(sorted(regions), sorted(regions)[1:]):
        if current.start < previous.end:
            errors.append(
                f"overlap {previous.owner}:{previous.purpose} "
                f"0x{previous.start:08X}..0x{previous.end:08X} with "
                f"{current.owner}:{current.purpose} "
                f"0x{current.start:08X}..0x{current.end:08X}"
            )
    return errors


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("manifest", nargs="?", type=Path,
                        default=Path("tools/thords/runtime-regions.json"))
    args = parser.parse_args()
    document = json.loads(args.manifest.read_text(encoding="utf-8"))
    regions = [Region.from_json(value) for value in document["regions"]]
    errors = audit(regions)
    print(json.dumps({
        "status": "FAIL" if errors else "PASS",
        "regions": [
            {"owner": r.owner, "purpose": r.purpose,
             "start": f"0x{r.start:08X}", "endExclusive": f"0x{r.end:08X}",
             "bytes": r.end - r.start}
            for r in sorted(regions)
        ],
        "errors": errors,
    }, indent=2))
    return bool(errors)


if __name__ == "__main__":
    raise SystemExit(main())
