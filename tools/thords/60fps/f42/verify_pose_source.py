#!/usr/bin/env python3
"""Verify checked-in pose fragments against the local EU decomp source."""
from __future__ import annotations

import argparse
import hashlib
from pathlib import Path

PAIRS = (
    ("pose-source/Player_AdvanceAnims.c", "mods/Player_AdvanceAnims.c"),
    (
        "pose-source/ModelAnim_UpdateVerts.cpp",
        "mods/_ZN9ModelAnim11UpdateVertsEv.cpp",
    ),
    (
        "pose-source/BlendModelAnim_UpdateVerts.cpp",
        "mods/_ZN14BlendModelAnim11UpdateVertsEv.cpp",
    ),
)


def sha256(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--decomp-root", type=Path, required=True)
    parser.add_argument("--source-root", type=Path, default=Path(__file__).parent)
    args = parser.parse_args()

    results = []
    for checked_in, decomp in PAIRS:
        left = args.source_root / checked_in
        right = args.decomp_root / decomp
        if not left.is_file() or not right.is_file():
            raise SystemExit(f"missing source pair: {left} / {right}")
        left_hash = sha256(left)
        right_hash = sha256(right)
        if left.read_bytes() != right.read_bytes():
            raise SystemExit(
                f"source mismatch: {checked_in} != {decomp} "
                f"({left_hash} != {right_hash})"
            )
        results.append({"checkedIn": checked_in, "sha256": left_hash})

    print({"status": "PASS", "files": results})
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
