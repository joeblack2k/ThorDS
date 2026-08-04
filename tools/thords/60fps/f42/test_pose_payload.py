#!/usr/bin/env python3
"""Small self-test for generated pose hook invariants."""
from __future__ import annotations

from build_pose_payload import branch


def main() -> int:
    hooks = (
        (0x020BEDD4, 0x02004DF8),
        (0x0201686C, 0x02004EF8),
    )
    for source, target in hooks:
        opcode = branch(source, target, 0xEA000000)
        assert opcode >> 24 == 0xEA
    assert branch(0x020BEDD4, 0x02004DF8, 0xEA000000) != 0x00FD1807
    print("pose_payload_hooks=PASS")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
