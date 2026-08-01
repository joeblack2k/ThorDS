#!/usr/bin/env python3
"""Build and verify the EU SM64DS smooth-camera Action Replay payload.

This tool only reads a locally supplied decompressed ARM9 image. It emits
public-safe code words and hashes; it never copies ROM bytes into an artifact.
"""

from __future__ import annotations

import argparse
import hashlib
import struct
from pathlib import Path


ARM9_BASE = 0x02004000
HOOK = 0x0200BCF0
PAYLOAD = 0x02075BB4
EXPECTED_HOOK_WORD = 0xE2110C02
MAX_PAYLOAD = 0x100


def text_from_elf(path: Path) -> bytes:
    data = path.read_bytes()
    fields = struct.unpack_from("<16sHHIIIIIHHHHHH", data, 0)
    section_offset, entry_size, count, strings_index = fields[6], fields[11], fields[12], fields[13]
    sections = [
        struct.unpack_from("<IIIIIIIIII", data, section_offset + index * entry_size)
        for index in range(count)
    ]
    strings = sections[strings_index]
    names = data[strings[4] : strings[4] + strings[5]]
    for section in sections:
        name_start = section[0]
        name_end = names.find(b"\0", name_start)
        if names[name_start:name_end] == b".text":
            return data[section[4] : section[4] + section[5]]
    raise ValueError("object has no .text section")


def word_at(image: bytes, address: int) -> int:
    offset = address - ARM9_BASE
    if offset < 0 or offset + 4 > len(image):
        raise ValueError(f"address outside ARM9 image: 0x{address:08X}")
    return int.from_bytes(image[offset : offset + 4], "little")


def arm_branch(source: int, target: int) -> int:
    delta = target - (source + 8)
    if delta % 4:
        raise ValueError("branch target is not word aligned")
    immediate = delta // 4
    if not -(1 << 23) <= immediate < (1 << 23):
        raise ValueError("branch target is outside ARM branch range")
    return 0xEA000000 | (immediate & 0x00FFFFFF)


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--arm9-image", type=Path, required=True)
    parser.add_argument("--object", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()

    image = args.arm9_image.read_bytes()
    original = word_at(image, HOOK)
    if original != EXPECTED_HOOK_WORD:
        raise SystemExit(
            f"refusing patch: hook word is 0x{original:08X}, "
            f"expected 0x{EXPECTED_HOOK_WORD:08X}"
        )
    payload_offset = PAYLOAD - ARM9_BASE
    if payload_offset < 0 or payload_offset + MAX_PAYLOAD > len(image):
        raise SystemExit("refusing patch: payload region is outside ARM9 image")
    if any(image[payload_offset : payload_offset + MAX_PAYLOAD]):
        raise SystemExit("refusing patch: payload region is not unused zero-fill")

    payload = text_from_elf(args.object)
    if len(payload) > MAX_PAYLOAD:
        raise SystemExit(f"refusing patch: payload is {len(payload)} bytes, limit is {MAX_PAYLOAD}")
    if len(payload) % 4:
        raise SystemExit("refusing patch: payload is not word aligned")

    words = [f"{PAYLOAD + offset:08X} {int.from_bytes(payload[offset:offset + 4], 'little'):08X}"
             for offset in range(0, len(payload), 4)]
    words.insert(0, f"{HOOK:08X} {arm_branch(HOOK, PAYLOAD):08X}")
    words.append("D0000000 00000000")

    digest = hashlib.sha256("\n".join(words).encode("ascii") + b"\n").hexdigest()
    args.output.write_text("\n".join(words) + "\n", encoding="ascii")
    print(f"payload_bytes={len(payload)}")
    print(f"hook_original={original:08X}")
    print(f"ar_sha256={digest}")


if __name__ == "__main__":
    main()
