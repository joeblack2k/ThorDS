#!/usr/bin/env python3
"""Independently verify the bounded F4.2 Action Replay patch."""
from __future__ import annotations
import argparse
import hashlib
from pathlib import Path

ARM9_BASE = 0x02004000
OVERLAY_BASE = 0x020AD660
PAYLOAD = 0x02075C1C
LIMIT = 0xF8
ANIMATION_PAYLOAD = 0x02004B00
ANIMATION_LIMIT = 0x40
CADENCE = 0x0208EE44
CAMERA_START, CAMERA_END = 0x02075BB4, 0x02075C18
ANIMATION_HOOK = 0x020BEDD4
PLAYER_HOOK, SPEED_HOOK = 0x020BF3F4, 0x020D4D88
TIMER_HOOK, CONTROL_TIMER_HOOK = 0x020E4F10, 0x020E4FDC
TIMER_ENTRY, CONTROL_TIMER_ENTRY = 0x02075CB8, 0x02075CE4
EXPECTED = {PLAYER_HOOK: 0xEBFD460D, SPEED_HOOK: 0xE92D4030,
            TIMER_HOOK: 0xE0850000, CONTROL_TIMER_HOOK: 0xE2850D1B,
            0x020BF3C0: 0xE5964098, 0x020BF3FC: 0xE5864098}
ANIMATION_EXPECTED = 0xE92D4010
LITERALS = (0x0208EE44, 0x02010C5C, 0x02010D40, 0x020A0DB0)
MOVEMENT_HALF_STEPS = (0xE1A060C6, 0xE1A070C7, 0xE1A080C8)
VERTICAL_HALF_ACCELERATION = (0xE594509C, 0xE584C09C, 0xE584509C)
VERTICAL_POSITION_CORRECTION = (0xE08771C5, 0xE04771C5)
TIMER_CONTINUATIONS = (0x020E4F14, 0x020E4FB8, 0x020E4FE0, 0x020E508C)
ANIMATION_CONTINUATION = 0x020BEDD8
ANIMATION_SKIP_RETURN = 0x012FFF1E

def parse(path):
    regions, resets, terminated, writing = [], 0, False, False
    current_guards, current_writes = [], []
    for line in path.read_text(encoding="ascii").splitlines():
        if not line.strip(): continue
        left, right = line.split()
        if left == "D0000000":
            if right != "00000000" or terminated or not writing: raise ValueError("bad terminator")
            regions.append((current_guards, current_writes))
            current_guards, current_writes, writing = [], [], False
            terminated = True
        elif left == "D2000000":
            if right != "00000000" or not terminated: raise ValueError("bad reset")
            terminated, resets = False, resets + 1
        elif terminated:
            raise ValueError("code before reset")
        elif left[0] == "5":
            if writing: raise ValueError("guard outside region")
            current_guards.append((int(left[1:], 16), int(right, 16)))
        else:
            if len(left) != 8 or len(right) != 8: raise ValueError("bad AR word")
            writing = True
            current_writes.append((int(left, 16), int(right, 16)))
    if terminated or current_guards or current_writes or len(regions) != 2 or resets != 2:
        raise ValueError("expected exactly two regions")
    return regions

def target(address, value):
    if value >> 24 not in (0xEA, 0xEB): raise ValueError("not ARM B/BL")
    imm = value & 0xFFFFFF
    if imm & 0x800000: imm -= 1 << 24
    return address + 8 + 4 * imm

def word(image, address, base):
    off = address - base
    if off < 0 or off + 4 > len(image): raise ValueError("address outside image")
    return int.from_bytes(image[off:off + 4], "little")

def main():
    p = argparse.ArgumentParser()
    p.add_argument("patch", type=Path)
    p.add_argument("--arm9-image", type=Path, required=True)
    p.add_argument("--overlay2-image", type=Path, required=True)
    a = p.parse_args()
    regions = parse(a.patch)
    guards, writes = regions[0]
    if guards != [(PLAYER_HOOK & 0x0FFFFFFF, EXPECTED[PLAYER_HOOK]),
                  (SPEED_HOOK & 0x0FFFFFFF, EXPECTED[SPEED_HOOK]),
                  (TIMER_HOOK & 0x0FFFFFFF, EXPECTED[TIMER_HOOK]),
                  (CONTROL_TIMER_HOOK & 0x0FFFFFFF, EXPECTED[CONTROL_TIMER_HOOK]),
                  (ANIMATION_HOOK & 0x0FFFFFFF, ANIMATION_EXPECTED),
                  (0x0208EE44, 2)]:
        raise SystemExit("exact hook guards missing")
    if len({x for x, _ in writes}) != len(writes): raise SystemExit("duplicate write")
    by = dict(writes)
    if by.get(0x0208EE44) != 1: raise SystemExit("cadence write missing")
    if by[PLAYER_HOOK] >> 24 != 0xEB or any(
        by[hook] >> 24 != 0xEA
        for hook in (SPEED_HOOK, TIMER_HOOK, CONTROL_TIMER_HOOK, ANIMATION_HOOK)
    ):
        raise SystemExit("wrong hook branch type")
    if target(PLAYER_HOOK, by[PLAYER_HOOK]) != PAYLOAD:
        raise SystemExit("player hook target mismatch")
    payload_addrs = sorted(x for x in by if PAYLOAD <= x < PAYLOAD + LIMIT)
    if not payload_addrs or payload_addrs != list(range(PAYLOAD, payload_addrs[-1] + 4, 4)):
        raise SystemExit("payload is not contiguous")
    if payload_addrs[-1] >= CAMERA_START and payload_addrs[0] <= CAMERA_END:
        raise SystemExit("payload overlaps camera")
    if payload_addrs[-1] >= PAYLOAD + LIMIT: raise SystemExit("payload overflow")
    animation_addrs = sorted(
        x for x in by
        if ANIMATION_PAYLOAD <= x < ANIMATION_PAYLOAD + ANIMATION_LIMIT
    )
    if (not animation_addrs
            or animation_addrs != list(range(
                ANIMATION_PAYLOAD,
                animation_addrs[-1] + 4,
                4,
            ))):
        raise SystemExit("animation payload is not contiguous")
    if animation_addrs[-1] >= ANIMATION_PAYLOAD + ANIMATION_LIMIT:
        raise SystemExit("animation payload overflow")
    if set(by) != {
        PLAYER_HOOK,
        SPEED_HOOK,
        TIMER_HOOK,
        CONTROL_TIMER_HOOK,
        ANIMATION_HOOK,
        0x0208EE44,
        *payload_addrs,
        *animation_addrs,
    }:
        raise SystemExit("unexpected write outside hooks and payload")
    speed_entry = target(SPEED_HOOK, by[SPEED_HOOK])
    if speed_entry not in payload_addrs:
        raise SystemExit("speed hook target is outside payload")
    if target(TIMER_HOOK, by[TIMER_HOOK]) != TIMER_ENTRY:
        raise SystemExit("timer hook target mismatch")
    if target(CONTROL_TIMER_HOOK, by[CONTROL_TIMER_HOOK]) != CONTROL_TIMER_ENTRY:
        raise SystemExit("control timer hook target mismatch")
    if target(ANIMATION_HOOK, by[ANIMATION_HOOK]) != ANIMATION_PAYLOAD:
        raise SystemExit("animation hook target mismatch")
    raw = b"".join(by[x].to_bytes(4, "little") for x in payload_addrs)
    for literal in LITERALS:
        if literal.to_bytes(4, "little") not in raw: raise SystemExit("required literal missing")
    for instruction in MOVEMENT_HALF_STEPS:
        if instruction.to_bytes(4, "little") not in raw:
            raise SystemExit("movement half-step invariant missing")
    for instruction in VERTICAL_HALF_ACCELERATION:
        if instruction.to_bytes(4, "little") not in raw:
            raise SystemExit("vertical half-acceleration invariant missing")
    for instruction in VERTICAL_POSITION_CORRECTION:
        if instruction.to_bytes(4, "little") not in raw:
            raise SystemExit("vertical position correction invariant missing")
    if not any((value >> 24) == 0xEA and
               target(address, value) == 0x02010C30
               for address, value in ((x, by[x]) for x in payload_addrs)):
        raise SystemExit("original UpdatePos tail branch missing")
    if not any((value >> 24) == 0xEA and
               target(address, value) == 0x020D4D8C
               for address, value in ((x, by[x]) for x in payload_addrs)):
        raise SystemExit("speed continuation branch missing")
    branch_targets = {
        target(address, by[address])
        for address in payload_addrs
        if by[address] >> 24 == 0xEA
    }
    if not set(TIMER_CONTINUATIONS).issubset(branch_targets):
        raise SystemExit("timer continuation branch missing")
    if any(by[x] == 0 for x in payload_addrs): raise SystemExit("zero placeholder")
    animation_raw = b"".join(by[x].to_bytes(4, "little") for x in animation_addrs)
    for literal in (
        0x0208EE44,
        0x020A0DB0,
        ANIMATION_EXPECTED,
        ANIMATION_SKIP_RETURN,
    ):
        if literal.to_bytes(4, "little") not in animation_raw:
            raise SystemExit("animation payload invariant missing")
    if not any(
        (by[address] >> 24) == 0xEA
        and target(address, by[address]) == ANIMATION_CONTINUATION
        for address in animation_addrs
    ):
        raise SystemExit("animation continuation branch missing")
    if any(by[x] == 0 for x in animation_addrs):
        raise SystemExit("animation zero placeholder")
    maintenance_guards, maintenance_writes = regions[1]
    expected_maintenance = [
        (PLAYER_HOOK & 0x0FFFFFFF, by[PLAYER_HOOK]),
        (SPEED_HOOK & 0x0FFFFFFF, by[SPEED_HOOK]),
        (TIMER_HOOK & 0x0FFFFFFF, by[TIMER_HOOK]),
        (CONTROL_TIMER_HOOK & 0x0FFFFFFF, by[CONTROL_TIMER_HOOK]),
        (ANIMATION_HOOK & 0x0FFFFFFF, by[ANIMATION_HOOK]),
        *((address & 0x0FFFFFFF, by[address]) for address in payload_addrs),
        *((address & 0x0FFFFFFF, by[address]) for address in animation_addrs),
        (CADENCE & 0x0FFFFFFF, 2),
    ]
    if maintenance_guards != expected_maintenance:
        raise SystemExit("maintenance guards mismatch")
    if maintenance_writes != [(CADENCE, 1)]:
        raise SystemExit("maintenance writes must contain cadence only")
    arm9, ov2 = a.arm9_image.read_bytes(), a.overlay2_image.read_bytes()
    for addr, expected in EXPECTED.items():
        if word(ov2, addr, OVERLAY_BASE) != expected: raise SystemExit("overlay proof failed")
    off = PAYLOAD - ARM9_BASE
    if any(arm9[off:off + LIMIT]): raise SystemExit("reservation is not zero-filled")
    if word(ov2, ANIMATION_HOOK, OVERLAY_BASE) != ANIMATION_EXPECTED:
        raise SystemExit("animation hook proof failed")
    animation_off = ANIMATION_PAYLOAD - ARM9_BASE
    if any(arm9[animation_off:animation_off + ANIMATION_LIMIT]):
        raise SystemExit("animation reservation is not zero-filled")
    print(f"payload_bytes={len(raw)}")
    print(f"animation_payload_bytes={len(animation_raw)}")
    print(f"second_entry=0x{speed_entry:08X}")
    print(f"ar_sha256={hashlib.sha256(a.patch.read_bytes()).hexdigest()}")
    print("verification=PASS")

if __name__ == "__main__":
    main()
