#!/usr/bin/env python3
"""Independently verify the bounded SM64DS EU F4 v10 AR patch."""
from __future__ import annotations

import argparse
import hashlib
from pathlib import Path

ARM9_BASE = 0x02004000
OVERLAY_BASE = 0x020AD660
PLAYER_PAYLOAD = 0x02075C1C
PLAYER_BYTES = 0xF8
WORLD_PAYLOAD = 0x02004B00
WORLD_BYTES = 0x18C
WORLD_LIMIT = 0x200
CADENCE = 0x0208EE44
ANIMATION_HOOK = 0x020BEDD4
ANIMATION_ORIGINAL = 0xE92D4010
COIN_SPIN_HOOK = 0x020B23B0
COIN_SPIN_ORIGINAL = 0xE2811B03
COIN_SPIN_HALF = 0xE2811C06
PLAYER_HOOKS = (0x020BF3F4, 0x020D4D88, 0x020E4F10, 0x020E4FDC)
PLAYER_ORIGINALS = (0xEBFD460D, 0xE92D4030, 0xE0850000, 0xE2850D1B)
PLAYER_EXTRA = ((0x020BF3C0, 0xE5964098), (0x020BF3FC, 0xE5864098))
WORLD_HOOKS = (
    0x02010C74,
    0x02010CC4,
    0x02010D5C,
    0x02015C58,
    0x02015C7C,
    0x02022F20,
)
WORLD_ORIGINALS = (
    0xE590109C,
    0xE590409C,
    0xEB010AC8,
    0xE594000C,
    0xE594000C,
    0xE92D4010,
)
WORLD_TARGETS = (
    WORLD_PAYLOAD,
            WORLD_PAYLOAD + 0x44,
            WORLD_PAYLOAD + 0x88,
            WORLD_PAYLOAD + 0x11C,
            WORLD_PAYLOAD + 0x11C,
            WORLD_PAYLOAD + 0x150,
)
PLAYER_LITERALS = (CADENCE, 0x02010C5C, 0x02010D40, 0x020A0DB0)
PLAYER_HALF_STEPS = (0xE1A060C6, 0xE1A070C7, 0xE1A080C8)
PLAYER_VERTICAL_ACCELERATION = (0xE594509C, 0xE584C09C, 0xE584509C)
PLAYER_VERTICAL_CORRECTION = (0xE08771C5, 0xE04771C5)
PLAYER_TIMER_CONTINUATIONS = (0x020E4F14, 0x020E4FB8, 0x020E4FE0, 0x020E508C)
WORLD_LITERALS = (CADENCE, 0x020A0DB0, 0x0210A83C, 0x02053884, 0x02022F24)


def parse(path: Path):
    regions = []
    guards = []
    writes = []
    mode = "guard"
    resets = 0
    for line in path.read_text(encoding="ascii").splitlines():
        if not line:
            continue
        left, right = line.split()
        if left == "D0000000":
            if right != "00000000" or mode != "write":
                raise ValueError("bad region terminator")
            regions.append((guards, writes))
            guards, writes, mode = [], [], "reset"
        elif left == "D2000000":
            if right != "00000000" or mode != "reset":
                raise ValueError("bad region reset")
            mode = "guard"
            resets += 1
        elif mode == "guard" and left.startswith("5") and len(left) == 8:
            guards.append((int(left[1:], 16), int(right, 16)))
        elif mode == "guard":
            mode = "write"
            writes.append((int(left, 16), int(right, 16)))
        elif mode == "write":
            if left.startswith("5"):
                raise ValueError("guard after first write")
            writes.append((int(left, 16), int(right, 16)))
        else:
            raise ValueError("word outside region")
    if mode != "guard" or guards or writes or len(regions) != 5 or resets != 5:
        raise ValueError("expected exactly five complete regions")
    for guards, writes in regions:
        if not guards or not writes:
            raise ValueError("empty region")
        if len({address for address, _ in guards}) != len(guards):
            raise ValueError("duplicate guard")
        if len({address for address, _ in writes}) != len(writes):
            raise ValueError("duplicate write")
    return regions


def branch_target(address: int, value: int) -> int:
    if value >> 24 not in (0xEA, 0xEB):
        raise ValueError("not an unconditional ARM B/BL")
    immediate = value & 0xFFFFFF
    if immediate & 0x800000:
        immediate -= 1 << 24
    return address + 8 + 4 * immediate


def image_word(image: bytes, address: int, base: int) -> int:
    offset = address - base
    if offset < 0 or offset + 4 > len(image):
        raise ValueError("address outside image")
    return int.from_bytes(image[offset:offset + 4], "little")


def guard(address: int, value: int):
    return address & 0x0FFFFFFF, value


def contiguous_addresses(values: dict[int, int], start: int, size: int):
    addresses = sorted(address for address in values if start <= address < start + size)
    expected = list(range(start, start + size, 4))
    if addresses != expected:
        raise ValueError(f"payload at 0x{start:08X} is not exact and contiguous")
    return addresses


def raw_payload(values: dict[int, int], addresses: list[int]) -> bytes:
    return b"".join(values[address].to_bytes(4, "little") for address in addresses)


def require_words(raw: bytes, words: tuple[int, ...], label: str):
    for value in words:
        if value.to_bytes(4, "little") not in raw:
            raise ValueError(f"{label} invariant 0x{value:08X} missing")


def require_ordered_words(raw: bytes, words: tuple[int, ...], label: str):
    actual = [
        int.from_bytes(raw[offset:offset + 4], "little")
        for offset in range(0, len(raw), 4)
    ]
    cursor = 0
    for value in words:
        try:
            cursor = actual.index(value, cursor) + 1
        except ValueError as exc:
            raise ValueError(f"{label} invariant 0x{value:08X} missing or out of order") from exc


def verify(path: Path, arm9_path: Path, overlay_path: Path):
    fresh, migration_1, migration_2, v9_migration, maintenance = parse(path)
    fresh_guards, fresh_writes = fresh
    expected_fresh_guards = [
        *(guard(address, value) for address, value in zip(PLAYER_HOOKS, PLAYER_ORIGINALS)),
        guard(ANIMATION_HOOK, ANIMATION_ORIGINAL),
        *(guard(address, value) for address, value in zip(WORLD_HOOKS, WORLD_ORIGINALS)),
        guard(COIN_SPIN_HOOK, COIN_SPIN_ORIGINAL),
        guard(CADENCE, 2),
    ]
    if fresh_guards != expected_fresh_guards:
        raise ValueError("fresh guards mismatch")

    fresh_by = dict(fresh_writes)
    player_addresses = contiguous_addresses(
        fresh_by,
        PLAYER_PAYLOAD,
        PLAYER_BYTES,
    )
    world_addresses = contiguous_addresses(
        fresh_by,
        WORLD_PAYLOAD,
        WORLD_BYTES,
    )
    expected_fresh_write_addresses = [
        *PLAYER_HOOKS,
        ANIMATION_HOOK,
        *WORLD_HOOKS,
        COIN_SPIN_HOOK,
        CADENCE,
        *player_addresses,
        *world_addresses,
    ]
    if [address for address, _ in fresh_writes] != expected_fresh_write_addresses:
        raise ValueError("fresh write set or order mismatch")
    if (
        fresh_by[CADENCE] != 1
        or fresh_by[ANIMATION_HOOK] != ANIMATION_ORIGINAL
        or fresh_by[COIN_SPIN_HOOK] != COIN_SPIN_HALF
    ):
        raise ValueError("fresh cadence, retired animation, or coin spin mismatch")

    player_branch_types = tuple(fresh_by[address] >> 24 for address in PLAYER_HOOKS)
    if player_branch_types != (0xEB, 0xEA, 0xEA, 0xEA):
        raise ValueError("player hook branch types mismatch")
    if branch_target(PLAYER_HOOKS[0], fresh_by[PLAYER_HOOKS[0]]) != PLAYER_PAYLOAD:
        raise ValueError("player entry target mismatch")
    speed_target = branch_target(PLAYER_HOOKS[1], fresh_by[PLAYER_HOOKS[1]])
    if speed_target not in player_addresses:
        raise ValueError("player speed target outside payload")
    if branch_target(PLAYER_HOOKS[2], fresh_by[PLAYER_HOOKS[2]]) != 0x02075CB8:
        raise ValueError("player timer target mismatch")
    if branch_target(PLAYER_HOOKS[3], fresh_by[PLAYER_HOOKS[3]]) != 0x02075CE4:
        raise ValueError("player control timer target mismatch")

    world_branch_types = tuple(fresh_by[address] >> 24 for address in WORLD_HOOKS)
    if world_branch_types != (0xEB, 0xEB, 0xEB, 0xEB, 0xEB, 0xEA):
        raise ValueError("world hook branch types mismatch")
    actual_world_targets = tuple(
        branch_target(address, fresh_by[address]) for address in WORLD_HOOKS
    )
    if actual_world_targets != WORLD_TARGETS:
        raise ValueError("world hook target mismatch")

    player_raw = raw_payload(fresh_by, player_addresses)
    if any(fresh_by[address] == 0 for address in player_addresses):
        raise ValueError("zero player payload word")
    require_words(player_raw, PLAYER_LITERALS, "player literal")
    require_words(player_raw, PLAYER_HALF_STEPS, "player movement")
    require_words(player_raw, PLAYER_VERTICAL_ACCELERATION, "player acceleration")
    require_words(player_raw, PLAYER_VERTICAL_CORRECTION, "player correction")
    player_payload_branches = {
        branch_target(address, fresh_by[address])
        for address in player_addresses
        if fresh_by[address] >> 24 == 0xEA
    }
    if 0x02010C30 not in player_payload_branches:
        raise ValueError("original UpdatePos tail branch missing")
    if 0x020D4D8C not in player_payload_branches:
        raise ValueError("player speed continuation missing")
    if not set(PLAYER_TIMER_CONTINUATIONS).issubset(player_payload_branches):
        raise ValueError("player timer continuation missing")

    world_raw = raw_payload(fresh_by, world_addresses)
    if any(fresh_by[address] == 0 for address in world_addresses):
        raise ValueError("zero world payload word")
    require_words(world_raw, WORLD_LITERALS, "world literal")
    require_words(
        world_raw,
        (
            0xE92D4004,
            0xE8BD8004,
            0xE595C000,
            0xE594000C,
            0xE92D4010,
            0x012FFF1E,
            0xE92D4002,
            0xE8BD8002,
        ),
        "world instruction",
    )
    if world_raw.count((0xE92D4004).to_bytes(4, "little")) != 2:
        raise ValueError("world helpers do not preserve r2 and lr twice")
    if world_raw.count((0xE8BD8004).to_bytes(4, "little")) != 2:
        raise ValueError("world helpers do not restore r2 and pc twice")
    if world_raw.count((0xE92D4002).to_bytes(4, "little")) != 1:
        raise ValueError("animation helper does not preserve r1 and lr exactly once")
    if world_raw.count((0xE8BD8002).to_bytes(4, "little")) != 1:
        raise ValueError("animation helper does not restore r1 and pc exactly once")
    require_ordered_words(
        world_raw,
        (
            0x01A0C0CC,
            0x104CC0CC,
            0x01A0C0CC,
            0x104CC0CC,
            0x01A0C0CC,
            0x104CC0CC,
        ),
        "world vector source-derived split",
    )
    for bad_word in (
        0x01A0C0C3,
        0x1043C0C3,
        0xE590C0A0,
        0xE590C0A4,
        0xE590C0A8,
    ):
        if bad_word.to_bytes(4, "little") in world_raw:
            raise ValueError(f"forbidden world vector instruction 0x{bad_word:08X}")

    old_player_values = {
        address: fresh_by[address]
        for address in (*PLAYER_HOOKS, *player_addresses)
    }
    old_animation_hook = (
        0xEA000000
        | (((WORLD_PAYLOAD - ANIMATION_HOOK - 8) // 4) & 0xFFFFFF)
    )
    old_animation_guards_1, old_animation_writes_1 = migration_1
    old_animation_guards_2, old_animation_writes_2 = migration_2
    if old_animation_writes_1 != old_animation_writes_2:
        raise ValueError("migration writes differ")
    old_animation_addresses = list(range(WORLD_PAYLOAD, WORLD_PAYLOAD + 48, 4))
    old_animation_values = dict(
        (address, value)
        for address, value in old_animation_guards_1
        if address in old_animation_addresses
    )
    if sorted(old_animation_values) != old_animation_addresses:
        raise ValueError("v7 animation migration payload is not exact")
    old_animation_raw = raw_payload(old_animation_values, old_animation_addresses)
    require_words(
        old_animation_raw,
        (CADENCE, 0x020A0DB0, ANIMATION_ORIGINAL, 0x012FFF1E),
        "v7 animation",
    )
    old_animation_branches = {
        branch_target(address, old_animation_values[address])
        for address in old_animation_addresses
        if old_animation_values[address] >> 24 == 0xEA
    }
    if 0x020BEDD8 not in old_animation_branches:
        raise ValueError("v7 animation continuation missing")

    expected_migration_writes = [
        (ANIMATION_HOOK, ANIMATION_ORIGINAL),
        *((address, fresh_by[address]) for address in WORLD_HOOKS),
        (COIN_SPIN_HOOK, COIN_SPIN_HALF),
        (CADENCE, 1),
        *((address, fresh_by[address]) for address in world_addresses),
    ]
    if old_animation_writes_1 != expected_migration_writes:
        raise ValueError("migration write set mismatch")
    migration_prefix = [
        *(guard(address, old_player_values[address]) for address in PLAYER_HOOKS),
        guard(ANIMATION_HOOK, old_animation_hook),
        *(guard(address, old_player_values[address]) for address in player_addresses),
        *(guard(address, old_animation_values[address]) for address in old_animation_addresses),
        *(guard(address, value) for address, value in zip(WORLD_HOOKS, WORLD_ORIGINALS)),
        guard(COIN_SPIN_HOOK, COIN_SPIN_ORIGINAL),
    ]
    if old_animation_guards_1 != [*migration_prefix, guard(CADENCE, 1)]:
        raise ValueError("cadence-1 migration guards mismatch")
    if old_animation_guards_2 != [*migration_prefix, guard(CADENCE, 2)]:
        raise ValueError("cadence-2 migration guards mismatch")

    v9_migration_guards, v9_migration_writes = v9_migration
    current_common = [
        *(guard(address, fresh_by[address]) for address in PLAYER_HOOKS),
        guard(ANIMATION_HOOK, ANIMATION_ORIGINAL),
        *(guard(address, fresh_by[address]) for address in WORLD_HOOKS),
        *(guard(address, fresh_by[address]) for address in player_addresses),
        *(guard(address, fresh_by[address]) for address in world_addresses),
    ]
    if v9_migration_guards != [
        *current_common,
        guard(COIN_SPIN_HOOK, COIN_SPIN_ORIGINAL),
    ]:
        raise ValueError("v9 migration guards mismatch")
    if v9_migration_writes != [
        (COIN_SPIN_HOOK, COIN_SPIN_HALF),
        (CADENCE, 1),
    ]:
        raise ValueError("v9 migration writes mismatch")

    maintenance_guards, maintenance_writes = maintenance
    expected_maintenance_guards = [
        *current_common,
        guard(COIN_SPIN_HOOK, COIN_SPIN_HALF),
        guard(CADENCE, 2),
    ]
    if maintenance_guards != expected_maintenance_guards:
        raise ValueError("maintenance guards mismatch")
    if maintenance_writes != [(CADENCE, 1)]:
        raise ValueError("maintenance must write cadence only")

    arm9 = arm9_path.read_bytes()
    overlay = overlay_path.read_bytes()
    for address, value in (*zip(PLAYER_HOOKS, PLAYER_ORIGINALS), *PLAYER_EXTRA):
        if image_word(overlay, address, OVERLAY_BASE) != value:
            raise ValueError(f"overlay proof failed at 0x{address:08X}")
    if image_word(overlay, COIN_SPIN_HOOK, OVERLAY_BASE) != COIN_SPIN_ORIGINAL:
        raise ValueError("coin spin proof failed")
    if image_word(overlay, ANIMATION_HOOK, OVERLAY_BASE) != ANIMATION_ORIGINAL:
        raise ValueError("animation hook proof failed")
    for address, value in zip(WORLD_HOOKS, WORLD_ORIGINALS):
        if image_word(arm9, address, ARM9_BASE) != value:
            raise ValueError(f"world hook proof failed at 0x{address:08X}")
    player_offset = PLAYER_PAYLOAD - ARM9_BASE
    if any(arm9[player_offset:player_offset + PLAYER_BYTES]):
        raise ValueError("player reservation is not zero-filled")
    world_offset = WORLD_PAYLOAD - ARM9_BASE
    if any(arm9[world_offset:world_offset + WORLD_LIMIT]):
        raise ValueError("world reservation is not zero-filled")

    return {
        "player_payload_bytes": len(player_raw),
        "world_payload_bytes": len(world_raw),
        "world_payload_sha256": hashlib.sha256(world_raw).hexdigest(),
        "ar_sha256": hashlib.sha256(path.read_bytes()).hexdigest(),
    }


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("patch", type=Path)
    parser.add_argument("--arm9-image", type=Path, required=True)
    parser.add_argument("--overlay2-image", type=Path, required=True)
    args = parser.parse_args()
    result = verify(args.patch, args.arm9_image, args.overlay2_image)
    for key, value in result.items():
        print(f"{key}={value}")
    print("verification=PASS")


if __name__ == "__main__":
    main()
