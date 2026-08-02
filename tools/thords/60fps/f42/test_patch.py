#!/usr/bin/env python3
"""Small direct smoke test for F4.2 patch invariants."""
from __future__ import annotations
import hashlib
import subprocess
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[4]
BUILD = Path(__file__).with_name("build_patch.py")
VERIFY = Path(__file__).with_name("verify_patch.py")
SRC = Path(__file__).with_name("sm64ds_eu_player_timestep.s")
ANIMATION_SRC = Path(__file__).with_name("sm64ds_eu_animation_timestep.s")
HOOK_GUARDS = [
    (0x020BF3F4, 0xEBFD460D),
    (0x020D4D88, 0xE92D4030),
    (0x020E4F10, 0xE0850000),
    (0x020E4FDC, 0xE2850D1B),
    (0x020BEDD4, 0xE92D4010),
    (0x0208EE44, 2),
]

def apply_model(lines, memory):
    active, writes = True, []
    for line in lines:
        left, right = line.split()
        address, value = int(left, 16), int(right, 16)
        if left == "D0000000":
            pass
        elif left == "D2000000":
            active = True
        elif left[0] == "5":
            if active:
                active = memory.get(address & 0x0FFFFFFF, 0) == value
        elif active:
            writes.append((address, value))
    return writes

def main():
    mc = Path("/opt/homebrew/Cellar/llvm/22.1.8/bin/llvm-mc")
    assert mc.exists()
    arm9 = ROOT / "tools/research/sm64ds-decomp/extracted/arm9_dec.bin"
    ov2 = ROOT / "tools/research/sm64ds-decomp/extracted/overlays/overlay_0002.bin"
    with tempfile.TemporaryDirectory() as d:
        obj = Path(d) / "f42.o"
        animation_obj = Path(d) / "f42-animation.o"
        out = Path(d) / "patch.txt"
        subprocess.run([str(mc), "-triple=armv5-none-eabi", "-filetype=obj", str(SRC), "-o", str(obj)], check=True)
        subprocess.run([str(mc), "-triple=armv5-none-eabi", "-filetype=obj", str(ANIMATION_SRC), "-o", str(animation_obj)], check=True)
        cmd = [
            "python3", str(BUILD),
            "--arm9-image", str(arm9),
            "--overlay2-image", str(ov2),
            "--object", str(obj),
            "--animation-object", str(animation_obj),
            "--output", str(out),
        ]
        first = subprocess.check_output(cmd, text=True)
        second = subprocess.check_output(cmd, text=True)
        assert first == second and hashlib.sha256(out.read_bytes()).hexdigest() in first
        lines = out.read_text().splitlines()
        assert lines[-2:] == ["D0000000 00000000", "D2000000 00000000"]
        assert int(lines[0][1:8], 16) == 0x020BF3F4 & 0x0FFFFFFF
        assert int(lines[1][1:8], 16) == 0x020D4D88 & 0x0FFFFFFF
        assert int(lines[2][1:8], 16) == 0x020E4F10 & 0x0FFFFFFF
        assert int(lines[3][1:8], 16) == 0x020E4FDC & 0x0FFFFFFF
        assert int(lines[4][1:8], 16) == 0x020BEDD4 & 0x0FFFFFFF
        assert int(lines[6].split()[0], 16) == 0x020BF3F4
        assert lines[5] == "5208EE44 00000002"
        valid_memory = {address & 0x0FFFFFFF: value for address, value in HOOK_GUARDS}
        expected_writes = [(int(a, 16), int(v, 16)) for a, v in
                           (line.split() for line in lines[6:-2])]
        assert apply_model(lines, valid_memory) == expected_writes
        for index, (address, value) in enumerate(HOOK_GUARDS):
            tampered_memory = dict(valid_memory)
            tampered_memory[address & 0x0FFFFFFF] = value ^ 1
            assert apply_model(lines, tampered_memory) == [], index
        subprocess.run(["python3", str(VERIFY), str(out), "--arm9-image", str(arm9), "--overlay2-image", str(ov2)], check=True)
        tampered = Path(d) / "tampered.txt"
        bad = out.read_text().replace("EBFD460D", "00000000", 1)
        tampered.write_text(bad)
        assert subprocess.run(["python3", str(VERIFY), str(tampered), "--arm9-image", str(arm9), "--overlay2-image", str(ov2)]).returncode != 0
        malformed = Path(d) / "malformed.txt"
        malformed.write_text(out.read_text().replace("D0000000 00000000", "D0000000 00000000\n0208EE44 00000001", 1))
        assert subprocess.run(["python3", str(VERIFY), str(malformed), "--arm9-image", str(arm9), "--overlay2-image", str(ov2)]).returncode != 0
        overlay = ov2.read_bytes()
        correct = 0x020BF3F4 - 0x020AD660
        wrong = 0x020BF3F4 - 0x020C0000
        assert int.from_bytes(overlay[correct:correct + 4], "little") == 0xEBFD460D
        assert int.from_bytes(overlay[wrong:wrong + 4], "little") != 0xEBFD460D
    print("test_patch=PASS")

if __name__ == "__main__":
    main()
