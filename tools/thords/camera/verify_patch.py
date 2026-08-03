#!/usr/bin/env python3
"""Independently verify a generated ThorDS camera Action Replay text file."""

from __future__ import annotations

import argparse
from pathlib import Path

HOOK = 0x02009E70
PAYLOAD = 0x02075BB4
MAX_PAYLOAD = 0xE0
PITCH_BRIDGE_ADDRESS = 0x0200A7A8
PITCH_BRIDGE_TARGET = PAYLOAD + 0xA4
EXPECTED_HOOK_WORD = 0xE92D4FF0
ARM9_BASE = 0x02004000
TARGET_BRIDGE_PATCHES = (
    (0x0200A790, 0x02880C01, 0xE2880C01),
    (0x0200A79C, 0x01D028F4, 0xE1D028F4),
    (0x0200A7A4, 0x00811002, 0xE0811002),
)


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


def branch_target(address: int, word: int, opcode: int = 0xEA) -> int:
    if word >> 24 != opcode:
        raise ValueError("word is not the expected ARM branch")
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
    write_map = dict(writes)
    if len(write_map) != len(writes):
        raise SystemExit("duplicate patch address")
    for address, _, replacement in TARGET_BRIDGE_PATCHES:
        if write_map.get(address) != replacement:
            raise SystemExit(f"target bridge write at 0x{address:08X} is missing or wrong")
    if PITCH_BRIDGE_ADDRESS not in write_map:
        raise SystemExit("pitch bridge write is missing")
    if branch_target(PITCH_BRIDGE_ADDRESS, write_map[PITCH_BRIDGE_ADDRESS]) != PITCH_BRIDGE_TARGET:
        raise SystemExit("pitch bridge targets the wrong payload address")
    bridge_addresses = {address for address, _, _ in TARGET_BRIDGE_PATCHES}
    bridge_addresses.add(PITCH_BRIDGE_ADDRESS)
    payload = {
        address: value
        for address, value in writes[1:]
        if address not in bridge_addresses
    }
    if not payload or min(payload) != PAYLOAD:
        raise SystemExit("payload start is missing")
    if any(address % 4 for address in payload):
        raise SystemExit("unaligned payload write")
    if max(payload) >= PAYLOAD + MAX_PAYLOAD:
        raise SystemExit("payload exceeds the camera reservation")

    image = args.arm9_image.read_bytes()
    offset = HOOK - ARM9_BASE
    original = int.from_bytes(image[offset : offset + 4], "little")
    if original != EXPECTED_HOOK_WORD:
        raise SystemExit(f"wrong original hook word: 0x{original:08X}")
    for address, expected, _ in TARGET_BRIDGE_PATCHES:
        offset = address - ARM9_BASE
        original = int.from_bytes(image[offset : offset + 4], "little")
        if original != expected:
            raise SystemExit(
                f"wrong original target bridge word at 0x{address:08X}: "
                f"0x{original:08X}"
            )
    offset = PITCH_BRIDGE_ADDRESS - ARM9_BASE
    original = int.from_bytes(image[offset : offset + 4], "little")
    if original != 0xE1C107BC:
        raise SystemExit(f"wrong original pitch bridge word: 0x{original:08X}")
    print(f"verified_hook=0x{HOOK:08X}")
    print(f"verified_target_bridge_words={len(TARGET_BRIDGE_PATCHES)}")
    print(f"verified_payload_words={len(payload)}")
    print("verification=PASS")


if __name__ == "__main__":
    main()
