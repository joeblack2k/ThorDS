#!/usr/bin/env python3
"""Small direct smoke test for F4.2 patch invariants."""
from __future__ import annotations
import hashlib
import json
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
    regions, region, active = [], [], True
    for line in lines:
        left, right = line.split()
        address, value = int(left, 16), int(right, 16)
        if left == "D0000000":
            regions.append(region)
            region, active = [], True
        elif left == "D2000000":
            continue
        elif left[0] == "5":
            active &= memory.get(address & 0x0FFFFFFF, 0) == value
        elif active:
            region.append((address, value))
    return regions

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
        profiles = json.loads((ROOT / "app/src/main/assets/enhancement-profiles.json").read_text())
        def find_runtime(value):
            if isinstance(value, dict):
                runtime = value.get("runtimeCode")
                if runtime and runtime.get("id") == "sm64ds.eu.60fps-dev-cadence.v7":
                    return runtime
                for child in value.values():
                    found = find_runtime(child)
                    if found:
                        return found
            elif isinstance(value, list):
                for child in value:
                    found = find_runtime(child)
                    if found:
                        return found
            return None
        runtime = find_runtime(profiles)
        assert runtime is not None
        assert runtime["id"] == "sm64ds.eu.60fps-dev-cadence.v7"
        generated_words = out.read_text(encoding="ascii").splitlines()
        assert runtime["codeWords"] == generated_words
        canonical = ("\n".join(runtime["codeWords"]) + "\n").encode("ascii")
        assert runtime["codeSha256"] == hashlib.sha256(canonical).hexdigest()
        valid_memory = {address & 0x0FFFFFFF: value for address, value in HOOK_GUARDS}
        first_end = lines.index("D0000000 00000000")
        expected_writes = [(int(a, 16), int(v, 16)) for a, v in
                           (line.split() for line in lines[6:first_end])]
        applied = apply_model(lines, valid_memory)
        assert applied[0] == expected_writes
        assert applied[1] == []
        for index, (address, value) in enumerate(HOOK_GUARDS):
            tampered_memory = dict(valid_memory)
            tampered_memory[address & 0x0FFFFFFF] = value ^ 1
            assert apply_model(lines, tampered_memory)[0] == [], index
        patched = dict(valid_memory)
        patched.update({address & 0x0FFFFFFF: value for address, value in expected_writes})
        patched[0x0208EE44 & 0x0FFFFFFF] = 2
        assert apply_model(lines, patched)[1] == [(0x0208EE44, 1)]
        maintenance_guards = [
            (0x020BF3F4, patched[0x020BF3F4]),
            (0x020D4D88, patched[0x020D4D88]),
            (0x020E4F10, patched[0x020E4F10]),
            (0x020E4FDC, patched[0x020E4FDC]),
            (0x020BEDD4, patched[0x020BEDD4]),
            *((address, patched[address]) for address in
              range(0x02075C1C, 0x02075C1C + 248, 4)),
            *((address, patched[address]) for address in
              range(0x02004B00, 0x02004B00 + 48, 4)),
            (0x0208EE44, 2),
        ]
        for address, value in maintenance_guards:
            tampered_memory = dict(patched)
            tampered_memory[address & 0x0FFFFFFF] = value ^ 1
            assert apply_model(lines, tampered_memory)[1] == [], address
        assert apply_model(lines, {**patched, 0x0208EE44: 1})[1] == []
        assert apply_model(lines, {**patched, 0x0208EE44: 3})[1] == []
        subprocess.run(["python3", str(VERIFY), str(out), "--arm9-image", str(arm9), "--overlay2-image", str(ov2)], check=True)
        verify = ["python3", str(VERIFY), "--arm9-image", str(arm9), "--overlay2-image", str(ov2)]
        def rejected(path):
            return subprocess.run(
                verify[:2] + [str(path)] + verify[2:],
                stdout=subprocess.DEVNULL,
                stderr=subprocess.DEVNULL,
            ).returncode != 0
        tampered = Path(d) / "tampered.txt"
        bad = out.read_text().replace("EBFD460D", "00000000", 1)
        tampered.write_text(bad)
        assert rejected(tampered)
        malformed = Path(d) / "malformed.txt"
        malformed.write_text(out.read_text().replace("D0000000 00000000", "D0000000 00000000\n0208EE44 00000001", 1))
        assert rejected(malformed)
        missing_reset = Path(d) / "missing-reset.txt"
        missing_reset.write_text("\n".join(lines[:-1]) + "\n")
        assert rejected(missing_reset)
        overlay = ov2.read_bytes()
        correct = 0x020BF3F4 - 0x020AD660
        wrong = 0x020BF3F4 - 0x020C0000
        assert int.from_bytes(overlay[correct:correct + 4], "little") == 0xEBFD460D
        assert int.from_bytes(overlay[wrong:wrong + 4], "little") != 0xEBFD460D
    print("test_patch=PASS")

if __name__ == "__main__":
    main()
