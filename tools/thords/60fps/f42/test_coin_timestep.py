#!/usr/bin/env python3
"""Check the bounded Coin::Behavior half-step correction."""
from __future__ import annotations

import json
from pathlib import Path


ROOT = Path(__file__).resolve().parents[4]
DECOMP = ROOT.parent / "MelonDS" / "docs" / "sm64ds-decomp"
SOURCE = DECOMP / "src" / "_ZN4Coin8BehaviorEv.cpp"
PROFILE = ROOT / "app" / "src" / "main" / "assets" / "enhancement-profiles.json"


def find_runtime(value):
    if isinstance(value, dict):
        runtime = value.get("runtimeCode")
        if runtime and runtime.get("id") == "sm64ds.eu.60fps-dev-cadence.v10":
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


def main() -> None:
    source = SOURCE.read_text(encoding="utf-8")
    assert "*(short *)(((int)((char *)this) + 0x8e)) += 0xc00;" in source

    catalog = json.loads(PROFILE.read_text(encoding="utf-8"))
    runtime = find_runtime(catalog)
    assert runtime is not None
    words = runtime["codeWords"]
    assert "020B23B0 E2811C06" in words
    assert "020B23B0 E2811B06" not in words

    print("coin_timestep=PASS source_step=0x0C00 cadence1_step=0x0600")


if __name__ == "__main__":
    main()
