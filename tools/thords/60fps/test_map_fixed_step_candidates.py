#!/usr/bin/env python3
"""Small runnable self-test for binary function-range candidate mapping."""
from __future__ import annotations

import importlib.util
import json
import tempfile
from pathlib import Path

MAPPER = Path(__file__).with_name("map_fixed_step_candidates.py")


def load_mapper():
    spec = importlib.util.spec_from_file_location("fixed_step_mapper", MAPPER)
    module = importlib.util.module_from_spec(spec)
    assert spec.loader is not None
    spec.loader.exec_module(module)
    return module


def main() -> int:
    mapper = load_mapper()
    with tempfile.TemporaryDirectory() as directory:
        root = Path(directory) / "decomp"
        main = root / "config/arm9"
        overlay = main / "overlays/ov002"
        images = root / "extracted/overlays"
        overlay.mkdir(parents=True)
        images.mkdir(parents=True)
        (main / "delinks.txt").write_text(
            "    .text start:0x02004000 end:0x02004008 kind:code\n"
        )
        (main / "symbols.txt").write_text(
            "func_main kind:function(arm,size=0x8) addr:0x02004000\n"
        )
        (main / "relocs.txt").write_text(
            "from:0x02004004 kind:load to:0x0208ee44 module:main\n"
        )
        (root / "extracted/arm9_dec.bin").write_bytes(bytes(range(8)))
        (overlay / "delinks.txt").write_text(
            "    .text start:0x020ad660 end:0x020ad668 kind:code\n"
        )
        (overlay / "symbols.txt").write_text(
            "_ZN4Coin8BehaviorEv kind:function(arm,size=0x8) addr:0x020ad660\n"
        )
        (overlay / "relocs.txt").write_text("")
        (images / "overlay_0002.bin").write_bytes(bytes(range(8, 16)))
        candidates = root / "candidates.json"
        candidates.write_text(json.dumps({
            "sourceTreeSha256": "a" * 64,
            "findings": [
                {
                    "file": "src/func_main.c",
                    "kind": "cadence_symbol_ref",
                    "category": "world_physics",
                },
                {
                    "file": "src/_ZN4Coin8BehaviorEv.cpp",
                    "kind": "direct_pointer_member_mutation",
                    "category": "rotation",
                },
            ],
        }))
        payload = mapper.map_candidates(root, candidates)
        assert payload["counts"] == {
            "candidateFindings": 2,
            "sourceFiles": 2,
            "mappedSourceFiles": 2,
            "unresolvedSourceFiles": 0,
            "cadenceRelocations": 1,
            "unresolvedCadenceRelocations": 0,
        }
        assert not payload["unresolved"]
        assert payload["cadenceRelocations"][0]["function"] == "func_main"
        assert all(len(item["functionBytesSha256"]) == 64
                   for item in payload["functions"])
        assert all("bytes" not in key.lower()
                   for item in payload["functions"] for key in item
                   if key != "functionBytesSha256")
    print(json.dumps({"ok": True, "mapped": 2}, sort_keys=True))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
