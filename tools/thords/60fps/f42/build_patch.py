#!/usr/bin/env python3
"""Build the bounded SM64DS EU rev0 F4.2 Action Replay patch."""
from __future__ import annotations
import argparse
import hashlib
import struct
from pathlib import Path

ARM9_BASE = 0x02004000
OVERLAY_BASE = 0x020AD660
PAYLOAD = 0x02075C1C
RESERVATION = 0xF8
ANIMATION_PAYLOAD = 0x02004B00
ANIMATION_RESERVATION = 0x40
CADENCE = 0x0208EE44
ANIMATION_HOOK = 0x020BEDD4
PLAYER_HOOK = 0x020BF3F4
SPEED_HOOK = 0x020D4D88
TIMER_HOOK = 0x020E4F10
CONTROL_TIMER_HOOK = 0x020E4FDC
EXPECTED = {PLAYER_HOOK: 0xEBFD460D, SPEED_HOOK: 0xE92D4030,
            TIMER_HOOK: 0xE0850000, CONTROL_TIMER_HOOK: 0xE2850D1B,
            0x020BF3C0: 0xE5964098, 0x020BF3FC: 0xE5864098}
ANIMATION_EXPECTED = 0xE92D4010

def elf_sections(data):
    if data[:4] != b"\x7fELF" or data[4] != 1 or data[5] != 1:
        raise ValueError("expected ELF32 little-endian object")
    h = struct.unpack_from("<16sHHIIIIIHHHHHH", data, 0)
    shoff, shentsize, shnum, shstrndx = h[6], h[11], h[12], h[13]
    return [struct.unpack_from("<IIIIIIIIII", data, shoff + i * shentsize)
            for i in range(shnum)], shstrndx

def elf_payload(path, required):
    data = path.read_bytes()
    sections, shstrndx = elf_sections(data)
    names_sec = sections[shstrndx]
    names = data[names_sec[4]:names_sec[4] + names_sec[5]]
    text = None
    symbols = {}
    for sec in sections:
        name = names[sec[0]:names.find(b"\0", sec[0])]
        if name == b".text":
            text = data[sec[4]:sec[4] + sec[5]]
        if name in (b".rel.text", b".rela.text"):
            if sec[5]:
                raise ValueError("executable payload has relocations")
        if name == b".symtab":
            strtab = sections[sec[6]]
            strings = data[strtab[4]:strtab[4] + strtab[5]]
            for off in range(sec[4], sec[4] + sec[5], sec[9]):
                st_name, st_value, _, _, _, _ = struct.unpack_from("<IIIBBH", data, off)
                end = strings.find(b"\0", st_name)
                if st_name:
                    symbols[strings[st_name:end].decode("ascii")] = st_value
    if text is None:
        raise ValueError("object has no .text")
    if any(name not in symbols for name in required):
        raise ValueError("required global symbols are missing")
    return text, {name: symbols[name] for name in required}

def word(image, address, base=ARM9_BASE):
    off = address - base
    if off < 0 or off + 4 > len(image):
        raise ValueError(f"address outside image: 0x{address:08X}")
    return int.from_bytes(image[off:off + 4], "little")

def branch(source, target, link=False):
    delta = target - source - 8
    if delta % 4 or not -(1 << 25) <= delta < (1 << 25):
        raise ValueError("branch target is out of range")
    return (0xEB000000 if link else 0xEA000000) | ((delta // 4) & 0xFFFFFF)

def main():
    p = argparse.ArgumentParser()
    p.add_argument("--arm9-image", type=Path, required=True)
    p.add_argument("--overlay2-image", type=Path, required=True)
    p.add_argument("--object", type=Path, required=True)
    p.add_argument("--animation-object", type=Path, required=True)
    p.add_argument("--output", type=Path, required=True)
    a = p.parse_args()
    arm9, ov2 = a.arm9_image.read_bytes(), a.overlay2_image.read_bytes()
    for addr, expected in EXPECTED.items():
        if word(ov2, addr, OVERLAY_BASE) != expected:
            raise SystemExit(f"overlay guard failed at 0x{addr:08X}")
    off = PAYLOAD - ARM9_BASE
    if off < 0 or off + RESERVATION > len(arm9) or any(arm9[off:off + RESERVATION]):
        raise SystemExit("ARM9 payload reservation is not zero-filled")
    if word(ov2, ANIMATION_HOOK, OVERLAY_BASE) != ANIMATION_EXPECTED:
        raise SystemExit(f"animation guard failed at 0x{ANIMATION_HOOK:08X}")
    animation_off = ANIMATION_PAYLOAD - ARM9_BASE
    if (animation_off < 0
            or animation_off + ANIMATION_RESERVATION > len(arm9)
            or any(arm9[animation_off:animation_off + ANIMATION_RESERVATION])):
        raise SystemExit("animation payload reservation is not zero-filled")
    required = (
        "f42_player_timestep_entry",
        "f42_player_speed_entry",
        "f42_player_timer_entry",
        "f42_player_control_timer_entry",
    )
    text, symbols = elf_payload(a.object, required)
    if len(text) == 0 or len(text) > RESERVATION or len(text) % 4:
        raise SystemExit("payload size/alignment invalid")
    if symbols["f42_player_timestep_entry"] != 0:
        raise SystemExit("unexpected first symbol offset")
    second = PAYLOAD + symbols["f42_player_speed_entry"] - symbols["f42_player_timestep_entry"]
    if second % 4 or second >= PAYLOAD + len(text):
        raise SystemExit("second entry is outside payload")
    timer = PAYLOAD + symbols["f42_player_timer_entry"] - symbols["f42_player_timestep_entry"]
    control_timer = PAYLOAD + symbols["f42_player_control_timer_entry"] - symbols["f42_player_timestep_entry"]
    if any(entry % 4 or entry >= PAYLOAD + len(text) for entry in (timer, control_timer)):
        raise SystemExit("timer entry is outside payload")
    animation_text, animation_symbols = elf_payload(
        a.animation_object,
        ("f42_animation_entry",),
    )
    if (len(animation_text) == 0
            or len(animation_text) > ANIMATION_RESERVATION
            or len(animation_text) % 4):
        raise SystemExit("animation payload size/alignment invalid")
    if animation_symbols["f42_animation_entry"] != 0:
        raise SystemExit("unexpected animation symbol offset")
    writes = [(PLAYER_HOOK, branch(PLAYER_HOOK, PAYLOAD, True)),
              (SPEED_HOOK, branch(SPEED_HOOK, second)),
              (TIMER_HOOK, branch(TIMER_HOOK, timer)),
              (CONTROL_TIMER_HOOK, branch(CONTROL_TIMER_HOOK, control_timer)),
              (ANIMATION_HOOK, branch(ANIMATION_HOOK, ANIMATION_PAYLOAD)),
              (CADENCE, 1)]
    writes += [(PAYLOAD + i, int.from_bytes(text[i:i + 4], "little"))
               for i in range(0, len(text), 4)]
    writes += [(ANIMATION_PAYLOAD + i, int.from_bytes(animation_text[i:i + 4], "little"))
               for i in range(0, len(animation_text), 4)]
    lines = [f"5{addr & 0x0FFFFFFF:07X} {expected:08X}" for addr, expected in
             ((PLAYER_HOOK, EXPECTED[PLAYER_HOOK]),
              (SPEED_HOOK, EXPECTED[SPEED_HOOK]),
              (TIMER_HOOK, EXPECTED[TIMER_HOOK]),
              (CONTROL_TIMER_HOOK, EXPECTED[CONTROL_TIMER_HOOK]),
              (ANIMATION_HOOK, ANIMATION_EXPECTED),
              (CADENCE, 2))]
    lines += [f"{addr:08X} {value:08X}" for addr, value in writes]
    lines.append("D0000000 00000000")
    lines.append("D2000000 00000000")
    # Region B is deliberately maintenance-only: it can repair cadence, never
    # hooks or payload, and is fail-closed on any incomplete patched state.
    patched = dict(writes)
    maintenance_addresses = (
        PLAYER_HOOK,
        SPEED_HOOK,
        TIMER_HOOK,
        CONTROL_TIMER_HOOK,
        ANIMATION_HOOK,
        *range(PAYLOAD, PAYLOAD + len(text), 4),
        *range(ANIMATION_PAYLOAD, ANIMATION_PAYLOAD + len(animation_text), 4),
    )
    lines += [
        f"5{addr & 0x0FFFFFFF:07X} {patched[addr]:08X}"
        for addr in maintenance_addresses
    ]
    lines += [
        f"5208EE44 00000002",
        "0208EE44 00000001",
    ]
    lines.append("D0000000 00000000")
    lines.append("D2000000 00000000")
    a.output.write_text("\n".join(lines) + "\n", encoding="ascii")
    print(f"payload_bytes={len(text)}")
    print(f"second_entry=0x{second:08X}")
    print(f"timer_entry=0x{timer:08X}")
    print(f"control_timer_entry=0x{control_timer:08X}")
    print(f"animation_payload_bytes={len(animation_text)}")
    print(f"ar_sha256={hashlib.sha256(a.output.read_bytes()).hexdigest()}")

if __name__ == "__main__":
    main()
