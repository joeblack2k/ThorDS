#!/usr/bin/env python3
"""Inspect Nintendo DS ROM identity without exposing ROM contents."""

from __future__ import annotations

import argparse
import hashlib
import json
from io import BytesIO
from pathlib import Path
from typing import BinaryIO


HEADER_SIZE = 0x160
BANNER_SIZE = 0xA00
MAX_ARM_BOOTCODE_SIZE = 0x3BFE00


def read_exact(source: BinaryIO, offset: int, size: int) -> bytes:
    source.seek(offset)
    data = source.read(size)
    if len(data) != size:
        raise ValueError(f"section at 0x{offset:x} is truncated")
    return data


def read_u32_le(data: bytes, offset: int) -> int:
    return int.from_bytes(data[offset : offset + 4], "little")


def rom_identity(source: BinaryIO, size: int, basename: str) -> dict[str, object]:
    if size < HEADER_SIZE:
        raise ValueError("ROM is smaller than its required header")

    header = read_exact(source, 0, HEADER_SIZE)
    game_code = header[0x0C:0x10].decode("ascii")
    sections = (
        ("arm9", read_u32_le(header, 0x20), read_u32_le(header, 0x2C)),
        ("arm7", read_u32_le(header, 0x30), read_u32_le(header, 0x3C)),
        ("banner", read_u32_le(header, 0x68), BANNER_SIZE),
    )

    digest = hashlib.md5(header)
    for name, offset, section_size in sections:
        if name != "banner" and section_size > MAX_ARM_BOOTCODE_SIZE:
            raise ValueError(f"{name} bootcode size is invalid")
        if offset < HEADER_SIZE or offset + section_size > size:
            raise ValueError(f"{name} section is outside the ROM")
        digest.update(read_exact(source, offset, section_size))

    return {
        "basename": basename,
        "size_bytes": size,
        "game_code": game_code,
        "revision_byte": header[0x1E],
        "header_crc16": f"0x{int.from_bytes(header[0x15E:0x160], 'little'):04x}",
        "retroachievements_system_hash": digest.hexdigest(),
    }


def full_hashes(path: Path) -> dict[str, str]:
    sha256 = hashlib.sha256()
    md5 = hashlib.md5()
    with path.open("rb") as source:
        for chunk in iter(lambda: source.read(1024 * 1024), b""):
            sha256.update(chunk)
            md5.update(chunk)
    return {"full_sha256": sha256.hexdigest(), "full_md5": md5.hexdigest()}


def inspect(path: Path) -> dict[str, object]:
    with path.open("rb") as source:
        result = rom_identity(source, path.stat().st_size, path.name)
    result.update(full_hashes(path))
    return result


def self_test() -> None:
    image = bytearray(0x1600)
    image[0x0C:0x10] = b"ASMP"
    image[0x1E] = 0
    image[0x15E:0x160] = (0x477C).to_bytes(2, "little")
    image[0x20:0x24] = (0x200).to_bytes(4, "little")
    image[0x2C:0x30] = (4).to_bytes(4, "little")
    image[0x30:0x34] = (0x300).to_bytes(4, "little")
    image[0x3C:0x40] = (4).to_bytes(4, "little")
    image[0x68:0x6C] = (0x400).to_bytes(4, "little")
    image[0x200:0x204] = b"ARM9"
    image[0x300:0x304] = b"ARM7"
    image[0x400:0xE00] = bytes(range(256)) * 10

    expected = hashlib.md5(
        bytes(image[:HEADER_SIZE])
        + bytes(image[0x200:0x204])
        + bytes(image[0x300:0x304])
        + bytes(image[0x400:0xE00])
    ).hexdigest()
    actual = rom_identity(BytesIO(image), len(image), "synthetic.nds")

    assert actual["game_code"] == "ASMP"
    assert actual["revision_byte"] == 0
    assert actual["header_crc16"] == "0x477c"
    assert actual["retroachievements_system_hash"] == expected


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("rom", type=Path, nargs="?")
    parser.add_argument("--self-test", action="store_true")
    args = parser.parse_args()

    if args.self_test:
        self_test()
        print("self-test: PASS")
        return
    if args.rom is None:
        parser.error("provide a ROM path or --self-test")
    print(json.dumps(inspect(args.rom), indent=2, sort_keys=True))


if __name__ == "__main__":
    main()
