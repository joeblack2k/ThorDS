#!/usr/bin/env python3
"""Build the source-derived player-only pose payload."""
from __future__ import annotations

import argparse
import importlib.util
from io import BytesIO
from pathlib import Path

PAYLOAD = 0x02004DF8
HELPER = 0x02004C8C
HOOKS = (
    (0x020BEDD4, 0xE92D4010, PAYLOAD),
    (0x0201686C, 0xE92D4010, PAYLOAD + 0x100),
)
EXTERNAL = {
    "_ZNK6Player14GetBodyModelIDEjb": 0x020BECC8,
    "_ZN9Animation7AdvanceEv": 0x02015C3C,
    "_ZN6Player6IsAnimEj": 0x020BEECC,
    "_ZN15ModelComponents11UpdateBonesEP8BCA_Filei": 0x02045394,
    "_ZN15ModelComponents21UpdateVertsUsingBonesEv": 0x0204504C,
    "func_0204531c": 0x0204531C,
}
UNITS = (
    ("Player_AdvanceAnims.c", ("Player_AdvanceAnims",), PAYLOAD),
    (
        "ModelAnim_UpdateVerts.cpp",
        ("_Z20update_temporal_poseP9ModelAnim", "_ZN9ModelAnim11UpdateVertsEv"),
        HELPER,
    ),
)


def load_match(root: Path):
    spec = importlib.util.spec_from_file_location("sm64ds_match", root / "tools/match.py")
    module = importlib.util.module_from_spec(spec)
    assert spec.loader
    spec.loader.exec_module(module)
    return module


def extract_functions(obj: bytes):
    from elftools.elf.elffile import ELFFile

    elf = ELFFile(BytesIO(obj))
    symtab = elf.get_section_by_name(".symtab")
    result = {}
    for symbol in symtab.iter_symbols():
        if symbol["st_info"]["type"] != "STT_FUNC" or symbol["st_size"] == 0:
            continue
        section = elf.get_section(symbol["st_shndx"])
        relocations = []
        for rel in elf.iter_sections():
            if rel["sh_type"] not in ("SHT_REL", "SHT_RELA") or rel["sh_info"] != symbol["st_shndx"]:
                continue
            for item in rel.iter_relocations():
                offset = item["r_offset"] - symbol["st_value"]
                if 0 <= offset < symbol["st_size"]:
                    target = symtab.get_symbol(item["r_info_sym"]).name
                    relocations.append((offset, target))
        result[symbol.name] = (
            bytearray(section.data()[symbol["st_value"]:symbol["st_value"] + symbol["st_size"]]),
            relocations,
        )
    return result


def branch(source: int, target: int, original: int) -> int:
    delta = target - source - 8
    if delta % 4 or not -(1 << 25) <= delta < (1 << 25):
        raise ValueError("branch out of range")
    return (original & 0xFF000000) | ((delta // 4) & 0xFFFFFF)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--decomp-root", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    match = load_match(args.decomp_root)
    blobs = {}
    for source, names, _ in UNITS:
        path = Path(__file__).parent / "pose-source" / source
        obj = match.compile_c(path, "2004/b56", match.DEFAULT_FLAGS, [])
        if obj is None:
            raise SystemExit(f"compile failed: {source}")
        blobs.update(extract_functions(obj))

    layout = {
        "Player_AdvanceAnims": PAYLOAD,
        "_Z20update_temporal_poseP9ModelAnim": HELPER,
        "_ZN9ModelAnim11UpdateVertsEv": PAYLOAD + 0x100,
    }
    writes = []
    for function, address in layout.items():
        code, relocations = blobs[function]
        for offset, target in relocations:
            target_address = layout.get(target, EXTERNAL.get(target))
            if target_address is None:
                raise SystemExit(f"unresolved relocation {function}+0x{offset:X}: {target}")
            word = int.from_bytes(code[offset:offset + 4], "little")
            if word >> 24 not in (0xEA, 0xEB):
                raise SystemExit(f"non-branch relocation {function}+0x{offset:X}: {word:08X}")
            code[offset:offset + 4] = branch(address + offset, target_address, word).to_bytes(4, "little")
        writes.extend((address + i, int.from_bytes(code[i:i + 4], "little")) for i in range(0, len(code), 4))

    if len(blobs["Player_AdvanceAnims"][0]) != 0x100:
        raise SystemExit("player size changed")
    if len(blobs["_Z20update_temporal_poseP9ModelAnim"][0]) != 0xD4:
        raise SystemExit("helper size changed")
    if len(blobs["_ZN9ModelAnim11UpdateVertsEv"][0]) != 0x50:
        raise SystemExit("model size changed")
    lines = [f"5{address & 0x0FFFFFFF:07X} {expected:08X}" for address, expected, _ in HOOKS]
    lines += [f"{address:08X} {branch(address, target, False):08X}" for address, _, target in HOOKS]
    lines += [f"{address:08X} {value:08X}" for address, value in writes]
    lines += ["D0000000 00000000", "D2000000 00000000"]
    args.output.write_text("\n".join(lines) + "\n", encoding="ascii")
    print(f"payload_bytes={sum(len(blobs[name][0]) for name in layout)}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
