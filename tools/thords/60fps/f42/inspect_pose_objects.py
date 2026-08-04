#!/usr/bin/env python3
"""Compile pose sources and emit a complete function/relocation manifest."""
from __future__ import annotations

import argparse
import importlib.util
import json
from io import BytesIO
from pathlib import Path

SOURCES = (
    ("Player_AdvanceAnims.c", "Player_AdvanceAnims"),
    ("ModelAnim_UpdateVerts.cpp", "_ZN9ModelAnim11UpdateVertsEv"),
    ("BlendModelAnim_UpdateVerts.cpp", "_ZN14BlendModelAnim11UpdateVertsEv"),
)


def load_match(path: Path):
    spec = importlib.util.spec_from_file_location("sm64ds_match", path)
    module = importlib.util.module_from_spec(spec)
    assert spec.loader
    spec.loader.exec_module(module)
    return module


def manifest(data: bytes):
    from elftools.elf.elffile import ELFFile

    elf = ELFFile(BytesIO(data))
    symtab = elf.get_section_by_name(".symtab")
    functions = []
    for index, symbol in enumerate(symtab.iter_symbols()):
        if symbol["st_size"] == 0 or symbol["st_info"]["type"] != "STT_FUNC":
            continue
        relocations = []
        for section in elf.iter_sections():
            if section["sh_type"] not in ("SHT_REL", "SHT_RELA"):
                continue
            if section["sh_info"] != symbol["st_shndx"]:
                continue
            for relocation in section.iter_relocations():
                target = symtab.get_symbol(relocation["r_info_sym"]).name
                item = {
                    "offset": relocation["r_offset"] - symbol["st_value"],
                    "target": target,
                    "type": relocation["r_info_type"],
                }
                if section["sh_type"] == "SHT_RELA":
                    item["addend"] = relocation["r_addend"]
                relocations.append(item)
        functions.append(
            {
                "name": symbol.name,
                "size": symbol["st_size"],
                "section": symbol["st_shndx"],
                "relocations": relocations,
            }
        )
    return functions


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--decomp-root", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    match = load_match(args.decomp_root / "tools/match.py")
    result = []
    for source, entry in SOURCES:
        path = Path(__file__).parent / "pose-source" / source
        obj = match.compile_c(path, "2004/b56", match.DEFAULT_FLAGS, [])
        if obj is None:
            raise SystemExit(f"compile failed: {source}")
        functions = manifest(obj)
        names = {item["name"] for item in functions}
        if entry not in names:
            raise SystemExit(f"missing entry {entry}: {source}")
        if any(not relocation["target"] for item in functions for relocation in item["relocations"]):
            raise SystemExit(f"unnamed relocation: {source}")
        result.append({"source": source, "entry": entry, "functions": functions})
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(result, indent=2, sort_keys=True) + "\n", encoding="ascii")
    print(json.dumps({"status": "PASS", "translationUnits": len(result)}))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
