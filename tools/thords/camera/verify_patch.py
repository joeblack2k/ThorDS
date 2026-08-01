#!/usr/bin/env python3
"""Independently verify a generated ThorDS camera Action Replay text file."""

from __future__ import annotations

import argparse
from pathlib import Path

HOOK = 0x0200BCF0
PAYLOAD = 0x02075BB4
EXPECTED_HOOK_WORD = 0xE2110C02
ARM9_BASE = 0x02004000


def parse(path: Path) -> list[tuple[int, int]]:
    writes = []
    for line in path.read_text(encoding="ascii").splitlines():
        if not line.strip():
            continue
        address, value = line.split()
        if address == "D0000000":
            if value != "00000000":
                raise ValueError("invalid terminator")
            continue
        if len(address) != 8 or len(value) != 8:
            raise ValueError(f"invalid code word: {line}")
        writes.append((int(address, 16), int(value, 16)))
    return writes


def branch_target(address: int, word: int) -> int:
    if word >> 24 != 0xEA:
        raise ValueError("hook is not an ARM branch")
    signed = word & 0xFFFFFF
    if signed & 0x800000:
        signed -= 1 << 24
    return address + 8 + (signed << 2)


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("patch", type=Path)
    parser.add_argument("--arm9-image", type=Path, required=True)
    args = parser.parse_args()

    writes = parse(args.patch)
    if not writes or writes[0][0] != HOOK:
        raise SystemExit("hook write is missing or not first")
    if branch_target(*writes[0]) != PAYLOAD:
        raise SystemExit("hook does not branch to the declared payload")
    payload = {address: value for address, value in writes[1:]}
    if len(payload) != len(writes) - 1:
        raise SystemExit("duplicate payload address")
    if not payload or min(payload) != PAYLOAD:
        raise SystemExit("payload start is missing")
    if any(address % 4 for address in payload):
        raise SystemExit("unaligned payload write")

    image = args.arm9_image.read_bytes()
    offset = HOOK - ARM9_BASE
    original = int.from_bytes(image[offset : offset + 4], "little")
    if original != EXPECTED_HOOK_WORD:
        raise SystemExit(f"wrong original hook word: 0x{original:08X}")
    print(f"verified_hook=0x{HOOK:08X}")
    print(f"verified_payload_words={len(payload)}")
    print("verification=PASS")


if __name__ == "__main__":
    main()
