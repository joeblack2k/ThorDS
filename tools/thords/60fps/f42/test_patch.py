#!/usr/bin/env python3
"""Deterministic builder, verifier, and fail-closed model test for F4 v10."""
from __future__ import annotations

import hashlib
import importlib.util
import json
import subprocess
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[4]
HERE = Path(__file__).parent
BUILD = HERE / "build_patch.py"
VERIFY = HERE / "verify_patch.py"
MC = Path("/opt/homebrew/Cellar/llvm/22.1.8/bin/llvm-mc")
ARM9 = ROOT / "tools/research/sm64ds-decomp/extracted/arm9_dec.bin"
OVERLAY = ROOT / "tools/research/sm64ds-decomp/extracted/overlays/overlay_0002.bin"


def load_verifier():
    spec = importlib.util.spec_from_file_location("f42_verify_patch", VERIFY)
    module = importlib.util.module_from_spec(spec)
    assert spec.loader is not None
    spec.loader.exec_module(module)
    return module


def apply_model(lines: list[str], initial_memory: dict[int, int]):
    memory = dict(initial_memory)
    applied = []
    region_writes = []
    cond = 1
    condstack = 0
    for line in lines:
        left, right = line.split()
        opcode = int(left[:2], 16)
        value = int(right, 16)
        if ((opcode < 0xD0 and opcode != 0xC5) or opcode > 0xD2) and not cond:
            continue
        if 0x50 <= opcode <= 0x5F:
            condstack = ((condstack << 1) | cond) & 0xFFFFFFFF
            address = int(left[1:], 16)
            cond = int(memory.get(address) == value)
        elif left == "D0000000":
            applied.append(region_writes)
            region_writes = []
            cond = condstack & 1
            condstack >>= 1
        elif left == "D2000000":
            cond = 1
            condstack = 0
        else:
            address = int(left, 16)
            region_writes.append((address, value))
            memory[address & 0x0FFFFFFF] = value
    return applied


def runtime_code(catalog):
    if isinstance(catalog, dict):
        runtime = catalog.get("runtimeCode")
        if runtime and runtime.get("id") == "sm64ds.eu.60fps-dev-cadence.v10":
            return runtime
        for value in catalog.values():
            found = runtime_code(value)
            if found:
                return found
    elif isinstance(catalog, list):
        for value in catalog:
            found = runtime_code(value)
            if found:
                return found
    return None


def main():
    assert MC.exists()
    verifier = load_verifier()
    with tempfile.TemporaryDirectory() as temp_dir:
        temp = Path(temp_dir)
        objects = {}
        for source, name in (
            ("sm64ds_eu_player_timestep.s", "player"),
            ("sm64ds_eu_animation_timestep.s", "animation"),
            ("sm64ds_eu_world_timestep.s", "world"),
        ):
            objects[name] = temp / f"{name}.o"
            subprocess.run(
                [
                    str(MC),
                    "-triple=armv5-none-eabi",
                    "-filetype=obj",
                    str(HERE / source),
                    "-o",
                    str(objects[name]),
                ],
                check=True,
            )

        output = temp / "patch.txt"
        command = [
            "python3",
            str(BUILD),
            "--arm9-image",
            str(ARM9),
            "--overlay2-image",
            str(OVERLAY),
            "--object",
            str(objects["player"]),
            "--animation-object",
            str(objects["animation"]),
            "--world-object",
            str(objects["world"]),
            "--output",
            str(output),
        ]
        first = subprocess.check_output(command, text=True)
        first_bytes = output.read_bytes()
        second = subprocess.check_output(command, text=True)
        assert first == second
        assert first_bytes == output.read_bytes()
        assert "player_payload_bytes=248" in first
        assert "world_payload_bytes=396" in first
        assert hashlib.sha256(first_bytes).hexdigest() in first

        verified = verifier.verify(output, ARM9, OVERLAY)
        assert verified["player_payload_bytes"] == 248
        assert verified["world_payload_bytes"] == 396
        lines = output.read_text(encoding="ascii").splitlines()
        regions = verifier.parse(output)
        assert len(regions) == 5

        catalog = json.loads(
            (ROOT / "app/src/main/assets/enhancement-profiles.json").read_text()
        )
        runtime = runtime_code(catalog)
        assert runtime is not None
        assert runtime["codeWords"] == lines
        canonical = ("\n".join(lines) + "\n").encode("ascii")
        assert runtime["codeSha256"] == hashlib.sha256(canonical).hexdigest()

        for region_index, (guards, expected_writes) in enumerate(regions):
            memory = dict(guards)
            applied = apply_model(lines, memory)
            assert applied[region_index] == expected_writes
            assert all(
                not writes
                for index, writes in enumerate(applied)
                if index != region_index
            )
            for guard_index, (address, value) in enumerate(guards):
                tampered_memory = dict(memory)
                tampered_memory[address] = value ^ 1
                assert apply_model(lines, tampered_memory)[region_index] == [], (
                    region_index,
                    guard_index,
                )

        def rejected(candidate_lines: list[str]):
            candidate = temp / "tampered.txt"
            candidate.write_text("\n".join(candidate_lines) + "\n", encoding="ascii")
            try:
                verifier.verify(candidate, ARM9, OVERLAY)
            except (KeyError, ValueError):
                return True
            return False

        first_region_end = lines.index("D0000000 00000000")
        tamper_indexes = [
            index
            for index, line in enumerate(lines)
            if line.startswith("5")
        ]
        tamper_indexes.extend(
            index
            for index, line in enumerate(lines[:first_region_end])
            if not line.startswith("5")
            and (
                verifier.PLAYER_PAYLOAD
                <= int(line.split()[0], 16)
                < verifier.PLAYER_PAYLOAD + verifier.PLAYER_BYTES
                or verifier.WORLD_PAYLOAD
                <= int(line.split()[0], 16)
                < verifier.WORLD_PAYLOAD + verifier.WORLD_BYTES
            )
        )
        for index in tamper_indexes:
            candidate = list(lines)
            left, right = candidate[index].split()
            candidate[index] = f"{left} {int(right, 16) ^ 1:08X}"
            assert rejected(candidate), index

        source_split_words = (0x01A0C0CC, 0x104CC0CC)
        destination_split_words = (0x01A0C0C3, 0x1043C0C3)
        source_split_indexes = [
            index
            for index, line in enumerate(lines[:first_region_end])
            if int(line.split()[0], 16) >= verifier.WORLD_PAYLOAD
            and int(line.split()[0], 16) < verifier.WORLD_PAYLOAD + verifier.WORLD_BYTES
            and int(line.split()[1], 16) in source_split_words
        ]
        assert len(source_split_indexes) == 6
        for index in source_split_indexes:
            candidate = list(lines)
            left, right = candidate[index].split()
            replacement = destination_split_words[source_split_words.index(int(right, 16))]
            candidate[index] = f"{left} {replacement:08X}"
            assert rejected(candidate), index

        malformed = list(lines)
        malformed.insert(first_region_end + 1, f"{verifier.CADENCE:08X} 00000001")
        assert rejected(malformed)
        assert rejected(lines[:-1])

        overlay = OVERLAY.read_bytes()
        correct_offset = 0x020BF3F4 - verifier.OVERLAY_BASE
        wrong_offset = 0x020BF3F4 - 0x020C0000
        assert int.from_bytes(
            overlay[correct_offset:correct_offset + 4],
            "little",
        ) == 0xEBFD460D
        assert int.from_bytes(
            overlay[wrong_offset:wrong_offset + 4],
            "little",
        ) != 0xEBFD460D

    def signed32(value):
        value &= 0xFFFFFFFF
        return value - 0x100000000 if value & 0x80000000 else value

    def split(value, parity):
        half = signed32(value) >> 1
        return half if parity == 0 else signed32(value - half)

    for value in (-3, -1, 0, 1, 3):
        for path in ("acceleration", "addvec", "animation"):
            assert signed32(split(value, 0) + split(value, 1)) == value, (path, value)

    for length in (0x1000, 0x9000, 0x1A000):
        for speed in (1, 2, 3, 0x800, 0x1000):
            original_r1 = length
            frame = 0
            for parity in range(60):
                adjusted_speed = split(speed, parity & 1)
                assert original_r1 == length
                frame = (frame + adjusted_speed + original_r1) % original_r1
            assert frame == (30 * speed) % length

    for destination, source, expected in ((10, 3, (11, 12)), (-10, -3, (-12, -11))):
        for parity in (0, 1):
            result = signed32(destination + split(source, parity))
            assert result == expected[parity]
            assert result != signed32(destination + split(destination, parity))

    assert verifier.COIN_SPIN_ORIGINAL == 0xE2811B03
    assert verifier.COIN_SPIN_HALF == 0xE2811C06

    print("test_patch=PASS")


if __name__ == "__main__":
    main()
