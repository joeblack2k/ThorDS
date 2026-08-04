#!/usr/bin/env python3
"""Build the bounded SM64DS EU F4 v10 Action Replay patch."""
from __future__ import annotations
import argparse
import hashlib
import struct
from pathlib import Path

ARM9_BASE = 0x02004000
OVERLAY_BASE = 0x020AD660
PLAYER_PAYLOAD = 0x02004D00
OLD_PLAYER_PAYLOAD = 0x02075C1C
PLAYER_LIMIT = 0xF8
WORLD_PAYLOAD = 0x02004B00
WORLD_LIMIT = 0x200
CADENCE = 0x0208EE44
PLAYER_HOOKS = (0x020BF3F4, 0x020D4D88, 0x020E4F10, 0x020E4FDC)
PLAYER_EXPECTED = (0xEBFD460D, 0xE92D4030, 0xE0850000, 0xE2850D1B)
PLAYER_EXTRA = ((0x020BF3C0, 0xE5964098), (0x020BF3FC, 0xE5864098))
ANIMATION_HOOK = 0x020BEDD4
COIN_SPIN_HOOK = 0x020B23B0
COIN_SPIN_EXPECTED = 0xE2811B03
COIN_SPIN_HALF = 0xE2811C06
WORLD_HOOKS = (0x02010C74, 0x02010CC4, 0x02010D5C, 0x02015C58, 0x02015C7C, 0x02022F20)
WORLD_EXPECTED = (0xE590109C, 0xE590409C, 0xEB010AC8, 0xE594000C, 0xE594000C, 0xE92D4010)
ANIMATION_EXPECTED = 0xE92D4010
GAP_END = 0x02005000

def elf_payload(path, required):
    data = path.read_bytes()
    if data[:6] != b"\x7fELF\x01\x01": raise ValueError("expected ELF32 little-endian object")
    h = struct.unpack_from("<16sHHIIIIIHHHHHH", data, 0)
    sections = [struct.unpack_from("<IIIIIIIIII", data, h[6] + i * h[11]) for i in range(h[12])]
    names_sec = sections[h[13]]
    names = data[names_sec[4]:names_sec[4] + names_sec[5]]
    text, symbols = None, {}
    for sec in sections:
        name = names[sec[0]:names.find(b"\0", sec[0])]
        if name == b".text": text = data[sec[4]:sec[4] + sec[5]]
        if name in (b".rel.text", b".rela.text") and sec[5]: raise ValueError("payload has relocations")
        if name == b".symtab":
            strings_sec = sections[sec[6]]
            strings = data[strings_sec[4]:strings_sec[4] + strings_sec[5]]
            for off in range(sec[4], sec[4] + sec[5], sec[9]):
                st_name, st_value, _, _, _, _ = struct.unpack_from("<IIIBBH", data, off)
                if st_name:
                    end = strings.find(b"\0", st_name)
                    symbols[strings[st_name:end].decode()] = st_value
    if text is None or any(name not in symbols for name in required): raise ValueError("missing payload section/symbol")
    return text, {name: symbols[name] for name in required}

def word(image, address, base):
    off = address - base
    if off < 0 or off + 4 > len(image): raise ValueError("address outside image")
    return int.from_bytes(image[off:off + 4], "little")

def branch(source, target, link=False):
    delta = target - source - 8
    if delta % 4 or not -(1 << 25) <= delta < (1 << 25): raise ValueError("branch out of range")
    return (0xEB000000 if link else 0xEA000000) | ((delta // 4) & 0xFFFFFF)

def relocate_player_branches(data, payload):
    data = bytearray(data)
    for offset, target in (
        (0x6C, 0x02010C30),
        (0x8C, 0x020D4D8C),
        (0xC0, 0x020E4F14),
        (0xC4, 0x020E4FB8),
        (0xEC, 0x020E4FE0),
        (0xF0, 0x020E508C),
    ):
        data[offset:offset + 4] = branch(payload + offset, target).to_bytes(4, "little")
    return bytes(data)

def ar_guard(address, value):
    return f"5{address & 0x0FFFFFFF:07X} {value:08X}"

def words(address, data):
    return [f"{address + i:08X} {int.from_bytes(data[i:i+4], 'little'):08X}" for i in range(0, len(data), 4)]

def main():
    p = argparse.ArgumentParser()
    p.add_argument("--arm9-image", type=Path, required=True)
    p.add_argument("--overlay2-image", type=Path, required=True)
    p.add_argument("--object", type=Path, required=True)
    p.add_argument("--animation-object", type=Path, required=True)
    p.add_argument("--world-object", type=Path, required=True)
    p.add_argument("--output", type=Path, required=True)
    a = p.parse_args()
    arm9, ov2 = a.arm9_image.read_bytes(), a.overlay2_image.read_bytes()
    for addr, expected in (*zip(PLAYER_HOOKS, PLAYER_EXPECTED), *PLAYER_EXTRA):
        if word(ov2, addr, OVERLAY_BASE) != expected: raise SystemExit(f"player guard failed at 0x{addr:08X}")
    if word(ov2, COIN_SPIN_HOOK, OVERLAY_BASE) != COIN_SPIN_EXPECTED: raise SystemExit("coin spin guard failed")
    for addr, expected in zip(WORLD_HOOKS, WORLD_EXPECTED):
        if word(arm9, addr, ARM9_BASE) != expected: raise SystemExit(f"world guard failed at 0x{addr:08X}")
    if word(ov2, ANIMATION_HOOK, OVERLAY_BASE) != ANIMATION_EXPECTED: raise SystemExit("animation guard failed")
    if WORLD_PAYLOAD + WORLD_LIMIT > GAP_END: raise SystemExit("world reservation exceeds proven gap")
    if any(arm9[WORLD_PAYLOAD - ARM9_BASE:WORLD_PAYLOAD - ARM9_BASE + WORLD_LIMIT]): raise SystemExit("gap is not zero-filled")
    if any(arm9[PLAYER_PAYLOAD - ARM9_BASE:PLAYER_PAYLOAD - ARM9_BASE + PLAYER_LIMIT]): raise SystemExit("player reservation is not zero-filled")
    player, ps = elf_payload(a.object, ("f42_player_timestep_entry", "f42_player_speed_entry", "f42_player_timer_entry", "f42_player_control_timer_entry"))
    old_player = player
    player = relocate_player_branches(player, PLAYER_PAYLOAD)
    world, ws = elf_payload(a.world_object, ("f42_world_entry", "f42_world_entry_r4", "f42_world_add_vec", "f42_animation_speed", "f42_particle_entry"))
    old_animation, os = elf_payload(a.animation_object, ("f42_animation_entry",))
    if len(player) != PLAYER_LIMIT or ps["f42_player_timestep_entry"] != 0: raise SystemExit("player payload is not exact v7 size")
    if len(world) % 4 or len(world) > WORLD_LIMIT or len(old_animation) != 48: raise SystemExit("payload reservation invariant failed")
    pt = {name: PLAYER_PAYLOAD + ps[name] for name in ps}
    wt = {name: WORLD_PAYLOAD + ws[name] for name in ws}
    fresh_guards = [*zip(PLAYER_HOOKS, PLAYER_EXPECTED), (ANIMATION_HOOK, ANIMATION_EXPECTED), *zip(WORLD_HOOKS, WORLD_EXPECTED), (COIN_SPIN_HOOK, COIN_SPIN_EXPECTED), (CADENCE, 2)]
    player_writes = [(PLAYER_HOOKS[0], branch(PLAYER_HOOKS[0], pt["f42_player_timestep_entry"], True)),
                     (PLAYER_HOOKS[1], branch(PLAYER_HOOKS[1], pt["f42_player_speed_entry"])),
                     (PLAYER_HOOKS[2], branch(PLAYER_HOOKS[2], pt["f42_player_timer_entry"])),
                     (PLAYER_HOOKS[3], branch(PLAYER_HOOKS[3], pt["f42_player_control_timer_entry"]))]
    world_targets = (
        wt["f42_world_entry"],
        wt["f42_world_entry_r4"],
        wt["f42_world_add_vec"],
        wt["f42_animation_speed"],
        wt["f42_animation_speed"],
        wt["f42_particle_entry"],
    )
    world_writes = [
        (addr, branch(addr, target, addr != WORLD_HOOKS[-1]))
        for addr, target in zip(WORLD_HOOKS, world_targets)
    ]
    writes = [*player_writes, (ANIMATION_HOOK, ANIMATION_EXPECTED), *world_writes, (COIN_SPIN_HOOK, COIN_SPIN_HALF), (CADENCE, 1), *[(PLAYER_PAYLOAD+i, int.from_bytes(player[i:i+4], "little")) for i in range(0, len(player), 4)], *[(WORLD_PAYLOAD+i, int.from_bytes(world[i:i+4], "little")) for i in range(0, len(world), 4)]]
    lines = [ar_guard(x, y) for x, y in fresh_guards] + [f"{x:08X} {y:08X}" for x, y in writes] + ["D0000000 00000000", "D2000000 00000000"]
    current = dict(writes)
    current_common = [*[(x, current[x]) for x in PLAYER_HOOKS], (ANIMATION_HOOK, ANIMATION_EXPECTED), *[(x, current[x]) for x in WORLD_HOOKS], *[(PLAYER_PAYLOAD+i, current[PLAYER_PAYLOAD+i]) for i in range(0, len(player), 4)], *[(WORLD_PAYLOAD+i, current[WORLD_PAYLOAD+i]) for i in range(0, len(world), 4)]]
    for old_cadence in (1, 2):
        old_player_hooks = [(PLAYER_HOOKS[0], branch(PLAYER_HOOKS[0], OLD_PLAYER_PAYLOAD, True)),
                            (PLAYER_HOOKS[1], branch(PLAYER_HOOKS[1], OLD_PLAYER_PAYLOAD + ps["f42_player_speed_entry"])),
                            (PLAYER_HOOKS[2], branch(PLAYER_HOOKS[2], OLD_PLAYER_PAYLOAD + ps["f42_player_timer_entry"])),
                            (PLAYER_HOOKS[3], branch(PLAYER_HOOKS[3], OLD_PLAYER_PAYLOAD + ps["f42_player_control_timer_entry"]))]
        old_animation_write = (ANIMATION_HOOK, branch(ANIMATION_HOOK, WORLD_PAYLOAD))
        old_guards = [*old_player_hooks, old_animation_write, *[(OLD_PLAYER_PAYLOAD+i, int.from_bytes(old_player[i:i+4], "little")) for i in range(0, len(old_player), 4)], *[(WORLD_PAYLOAD+i, int.from_bytes(old_animation[i:i+4], "little")) for i in range(0, len(old_animation), 4)], *zip(WORLD_HOOKS, WORLD_EXPECTED), (COIN_SPIN_HOOK, COIN_SPIN_EXPECTED), (CADENCE, old_cadence)]
        migration_writes = [(ANIMATION_HOOK, ANIMATION_EXPECTED), *world_writes, (COIN_SPIN_HOOK, COIN_SPIN_HALF), (CADENCE, 1), *[(WORLD_PAYLOAD+i, int.from_bytes(world[i:i+4], "little")) for i in range(0, len(world), 4)]]
        lines += [ar_guard(x, y) for x, y in old_guards] + [f"{x:08X} {y:08X}" for x, y in migration_writes] + ["D0000000 00000000", "D2000000 00000000"]
    v9_migration = [*current_common, (COIN_SPIN_HOOK, COIN_SPIN_EXPECTED)]
    lines += [ar_guard(x, y) for x, y in v9_migration] + [f"{COIN_SPIN_HOOK:08X} {COIN_SPIN_HALF:08X}", f"{CADENCE:08X} 00000001", "D0000000 00000000", "D2000000 00000000"]
    maintenance = [*current_common, (COIN_SPIN_HOOK, COIN_SPIN_HALF), (CADENCE, 2)]
    lines += [ar_guard(x, y) for x, y in maintenance] + [f"{CADENCE:08X} 00000001", "D0000000 00000000", "D2000000 00000000"]
    a.output.write_text("\n".join(lines) + "\n", encoding="ascii")
    print(f"player_payload_bytes={len(player)}")
    print(f"world_payload_bytes={len(world)}")
    print(f"world_payload_sha256={hashlib.sha256(world).hexdigest()}")
    print(f"ar_sha256={hashlib.sha256(a.output.read_bytes()).hexdigest()}")

if __name__ == "__main__":
    main()
