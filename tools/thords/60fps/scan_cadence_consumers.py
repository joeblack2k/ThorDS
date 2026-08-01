#!/usr/bin/env python3
"""Inventory reads and writes of the EU SM64DS cadence symbol."""
from __future__ import annotations

import argparse
import hashlib
import json
import re
from pathlib import Path

SUFFIXES = {".c", ".cc", ".cpp", ".h", ".hpp"}
FUNCTION = re.compile(r"^\s*(?:[\w:<>,~*&]+\s+)+([\w:~]+)\s*\([^;]*\)")


def category(path: Path, line: str, function: str | None) -> str:
    text = f"{path} {line} {function or ''}".lower()
    if any(x in text for x in ("message", "hud", "menu", "pause")):
        return "message/HUD"
    if any(x in text for x in ("particle", "effect", "weather")):
        return "particle/effect"
    if "anim" in text:
        return "animation"
    if any(x in text for x in ("physics", "velocity", "gravity", "player", "actor")):
        return "physics"
    if any(x in text for x in ("timer", "countdown", "delay")):
        return "timer"
    if "ov" in str(path).lower() or "overlay" in str(path).lower():
        return "scene-specific"
    return "unknown"


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--markdown", type=Path, required=True)
    parser.add_argument("--symbol", default="data_0208ee44")
    args = parser.parse_args()
    root = args.root.resolve()
    paths = sorted(p for p in root.rglob("*") if p.is_file() and p.suffix in SUFFIXES)
    digest = hashlib.sha256()
    findings = []
    for path in paths:
        rel = path.relative_to(root).as_posix()
        data = path.read_bytes()
        digest.update(rel.encode() + b"\0" + data + b"\0")
        function = None
        for number, line in enumerate(data.decode("utf-8", "replace").splitlines(), 1):
            match = FUNCTION.match(line)
            if match:
                function = match.group(1)
            if args.symbol not in line:
                continue
            write = bool(re.search(
                rf"\b{re.escape(args.symbol)}\s*(?:\+=|-=|\*=|/=|=(?!=)|\+\+|--)",
                line,
            ))
            if not write:
                write = bool(re.search(rf"(?:\+\+|--)\s*{re.escape(args.symbol)}", line))
            findings.append({
                "file": rel, "line": number, "function": function,
                "kind": "write" if write else "read",
                "category": category(path, line, function), "text": line.strip(),
            })
    payload = {
        "schemaVersion": 1, "symbol": args.symbol, "sourceRoot": root.name,
        "sourceTreeSha256": digest.hexdigest(), "findings": findings,
    }
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(payload, indent=2, sort_keys=True) + "\n")
    rows = [
        "# Cadence Consumer Inventory", "",
        f"Symbol: `{args.symbol}`",
        f"Source tree SHA-256: `{digest.hexdigest()}`",
        f"Findings: `{len(findings)}`", "",
        "| File | Line | Function | Kind | Category | Source |",
        "|---|---:|---|---|---|---|",
    ]
    for item in findings:
        source = item["text"].replace("|", "\\|")
        rows.append(
            f"| `{item['file']}` | {item['line']} | `{item['function'] or ''}` | "
            f"{item['kind']} | {item['category']} | `{source}` |"
        )
    args.markdown.parent.mkdir(parents=True, exist_ok=True)
    args.markdown.write_text("\n".join(rows) + "\n")
    print(json.dumps({"findings": len(findings), "sourceTreeSha256": digest.hexdigest()}))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
