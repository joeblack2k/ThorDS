#!/usr/bin/env python3
"""Small runnable self-test for the fixed-step candidate scanner."""
from __future__ import annotations

import json
import subprocess
import sys
import tempfile
from pathlib import Path

SCANNER = Path(__file__).with_name("scan_fixed_step_consumers.py")


def main() -> int:
    with tempfile.TemporaryDirectory() as directory:
        root = Path(directory) / "src"
        root.mkdir()
        (root / "fixture.c").write_text(
            """
void Behavior(void) {
    data_0208ee44 += 1;
    *(short *)(self + 0x8e) += 0xc00;
    timer--;
    animation.Advance();
}
void Render(void) {
    x += 4;
}
""",
            encoding="utf-8",
        )
        result = subprocess.run(
            [sys.executable, str(SCANNER), "--root", str(root)],
            check=True,
            capture_output=True,
            text=True,
        )
        payload = json.loads(result.stdout)
        assert payload["purpose"] == "candidate_generation_only_not_proof"
        assert payload["completenessClaim"] == "none"
        assert payload["sourceTextIncluded"] is False
        assert len(payload["sourceTreeSha256"]) == 64
        assert payload["counts"]["cadence_symbol_ref"] == 1
        assert payload["counts"]["direct_pointer_member_mutation"] == 1
        assert payload["counts"]["timing_or_fixed_point_mutation"] == 2
        assert payload["lowConfidenceNumericOnlyLinesExcluded"] >= 1
        assert {item["confidence"] for item in payload["findings"]} == {"A", "B", "C"}
        assert not any(item["context"] == "Render" for item in payload["findings"])
        assert not any("source" in item for item in payload["findings"])
    print(json.dumps({"ok": True, "findings": len(payload["findings"])}, sort_keys=True))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
