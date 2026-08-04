#!/usr/bin/env python3
"""Map source candidates to exact decomp binary function ranges."""
from __future__ import annotations

import argparse
import hashlib
import json
import re
from collections import defaultdict
from pathlib import Path

SYMBOL = re.compile(
    r"^(\S+)\s+kind:function\([^)]*size=(0x[0-9a-fA-F]+)\)"
    r"\s+addr:(0x[0-9a-fA-F]+)"
)
SOURCE_EXCLUDE_PREFIXES = ("port/",)
TEXT_START = re.compile(r"^\s*\.text\s+start:(0x[0-9a-fA-F]+)")
CADENCE_RELOC = re.compile(
    r"^from:(0x[0-9a-fA-F]+)\s+kind:(\S+)\s+"
    r"to:0x0208ee44\b",
    re.IGNORECASE,
)


def sha256(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def module_inputs(root: Path):
    yield "main", root / "config/arm9", root / "extracted/arm9_dec.bin"
    overlay_root = root / "config/arm9/overlays"
    for config in sorted(overlay_root.glob("ov*")):
        number = int(config.name[2:])
        yield config.name, config, root / f"extracted/overlays/overlay_{number:04d}.bin"


def load_symbols(root: Path, wanted: set[str]) -> dict[str, dict]:
    symbols = {}
    for module, config, image_path in module_inputs(root):
        matches = []
        for line in (config / "symbols.txt").read_text(
            encoding="utf-8", errors="replace"
        ).splitlines():
            match = SYMBOL.match(line)
            if match and match.group(1) in wanted:
                matches.append(match.groups())
        if not matches:
            continue
        delinks = (config / "delinks.txt").read_text(encoding="utf-8", errors="replace")
        base_match = next(
            (TEXT_START.match(line) for line in delinks.splitlines()
             if TEXT_START.match(line)),
            None,
        )
        if base_match is None:
            raise ValueError(f"{module}: missing .text base")
        base = int(base_match.group(1), 16)
        image = image_path.read_bytes()
        image_hash = sha256(image)
        for name, size_text, address_text in matches:
            if name in symbols:
                raise ValueError(f"duplicate function symbol: {name}")
            address = int(address_text, 16)
            size = int(size_text, 16)
            offset = address - base
            if address % 4 or size <= 0 or offset < 0 or offset + size > len(image):
                raise ValueError(f"{module}:{name}: invalid binary range")
            symbols[name] = {
                "symbol": name,
                "module": module,
                "address": f"0x{address:08x}",
                "size": size,
                "image": image_path.relative_to(root).as_posix(),
                "imageSha256": image_hash,
                "functionBytesSha256": sha256(image[offset:offset + size]),
            }
    return symbols


def map_cadence_relocations(root: Path) -> tuple[list[dict], list[dict]]:
    mapped = []
    unresolved = []
    for module, config, _ in module_inputs(root):
        relocation_path = config / "relocs.txt"
        if not relocation_path.is_file():
            continue
        references = []
        for line in relocation_path.read_text(
            encoding="utf-8", errors="replace"
        ).splitlines():
            match = CADENCE_RELOC.match(line)
            if match:
                references.append((int(match.group(1), 16), match.group(2)))
        if not references:
            continue
        ranges = []
        for line in (config / "symbols.txt").read_text(
            encoding="utf-8", errors="replace"
        ).splitlines():
            match = SYMBOL.match(line)
            if match:
                name, size_text, address_text = match.groups()
                start = int(address_text, 16)
                ranges.append((start, start + int(size_text, 16), name))
        for address, kind in references:
            matches = [
                (start, end, name)
                for start, end, name in ranges
                if start <= address < end
            ]
            if len(matches) != 1:
                unresolved.append({
                    "module": module,
                    "address": f"0x{address:08x}",
                    "matches": len(matches),
                })
                continue
            start, end, name = matches[0]
            mapped.append({
                "module": module,
                "address": f"0x{address:08x}",
                "kind": kind,
                "function": name,
                "functionAddress": f"0x{start:08x}",
                "functionSize": end - start,
                "relocationFile": relocation_path.relative_to(root).as_posix(),
            })
    return mapped, unresolved


def map_candidates(root: Path, candidate_path: Path) -> dict:
    raw = candidate_path.read_bytes()
    candidates = json.loads(raw)
    grouped = defaultdict(list)
    for finding in candidates["findings"]:
        if any(finding["file"].startswith(prefix) for prefix in SOURCE_EXCLUDE_PREFIXES):
            continue
        grouped[finding["file"]].append(finding)
    wanted = {Path(source_path).stem for source_path in grouped}
    symbols = load_symbols(root, wanted)

    functions = []
    unresolved = []
    for source_path, findings in sorted(grouped.items()):
        symbol_name = Path(source_path).stem
        symbol = symbols.get(symbol_name)
        if symbol is None:
            unresolved.append({"sourceFile": source_path, "symbol": symbol_name})
            continue
        functions.append({
            **symbol,
            "sourceFile": source_path,
            "candidateCount": len(findings),
            "candidateKinds": sorted({item["kind"] for item in findings}),
            "categories": sorted({item["category"] for item in findings}),
        })
    cadence_relocations, unresolved_relocations = map_cadence_relocations(root)

    return {
        "schemaVersion": 1,
        "purpose": "binary_function_range_mapping_not_instruction_semantics",
        "candidateInputSha256": sha256(raw),
        "sourceTreeSha256": candidates["sourceTreeSha256"],
        "counts": {
            "candidateFindings": len(candidates["findings"]),
            "sourceFiles": len(grouped),
            "mappedSourceFiles": len(functions),
            "unresolvedSourceFiles": len(unresolved),
            "cadenceRelocations": len(cadence_relocations),
            "unresolvedCadenceRelocations": len(unresolved_relocations),
        },
        "functions": functions,
        "unresolved": unresolved,
        "cadenceRelocations": cadence_relocations,
        "unresolvedCadenceRelocations": unresolved_relocations,
    }


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", type=Path, required=True)
    parser.add_argument("--candidates", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    payload = map_candidates(args.root.resolve(), args.candidates.resolve())
    if payload["unresolved"] or payload["unresolvedCadenceRelocations"]:
        raise SystemExit(
            "unresolved binary mapping: "
            f"{payload['counts']['unresolvedSourceFiles']} source files, "
            f"{payload['counts']['unresolvedCadenceRelocations']} relocations"
        )
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(
        json.dumps(payload, indent=2, sort_keys=True) + "\n",
        encoding="utf-8",
    )
    print(json.dumps(payload["counts"], sort_keys=True))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
