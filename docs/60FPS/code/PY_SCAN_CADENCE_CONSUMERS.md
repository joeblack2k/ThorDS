# Python — scan cadence consumers

Save as:

```text
tools/thords/60fps/scan_cadence_consumers.py
```

```python
#!/usr/bin/env python3
from __future__ import annotations

import argparse
import hashlib
import json
import re
from dataclasses import asdict, dataclass
from pathlib import Path
from typing import Iterable

SOURCE_SUFFIXES = {".c", ".cc", ".cpp", ".h", ".hpp"}
SYMBOL_DEFAULT = "data_0208ee44"

FUNCTION_RE = re.compile(
    r"^\s*(?:[\w:<>,~*&]+\s+)+"
    r"(?P<name>[\w:~]+)\s*\([^;]*\)\s*(?:const\s*)?\{?\s*$"
)

@dataclass(frozen=True)
class Finding:
    file: str
    line: int
    function: str | None
    kind: str
    category: str
    text: str

def classify_kind(line: str, symbol: str) -> str:
    escaped = re.escape(symbol)
    write_patterns = [
        rf"\b{escaped}\s*(?:=|\+=|-=|\*=|/=)",
        rf"(?:\+\+|--)\s*{escaped}\b",
        rf"\b{escaped}\s*(?:\+\+|--)",
        rf"&\s*{escaped}\b",
    ]
    if any(re.search(pattern, line) for pattern in write_patterns):
        if re.search(rf"\b{escaped}\s*=\s*[12]\b", line):
            return "initializer-write"
        return "write"
    return "read"

def classify_category(path: Path, line: str, function: str | None) -> str:
    haystack = " ".join(
        [path.as_posix(), line, function or ""]
    ).lower()
    if any(word in haystack for word in ("message", "hud", "menu", "pause")):
        return "message-hud"
    if any(word in haystack for word in ("particle", "effect", "weather")):
        return "particle-effect"
    if any(word in haystack for word in ("anim", "animation")):
        return "animation"
    if any(word in haystack for word in ("physics", "velocity", "gravity",
                                         "player", "actor", "clsn", "collision")):
        return "physics-actor"
    if any(word in haystack for word in ("timer", "countdown", "delay")):
        return "timer"
    if "ov" in path.name.lower() or "overlay" in path.as_posix().lower():
        return "scene-overlay"
    return "unknown"

def iter_sources(root: Path) -> Iterable[Path]:
    for path in sorted(root.rglob("*")):
        if path.is_file() and path.suffix.lower() in SOURCE_SUFFIXES:
            yield path

def scan_file(path: Path, root: Path, symbol: str) -> list[Finding]:
    findings: list[Finding] = []
    current_function: str | None = None
    lines = path.read_text(encoding="utf-8", errors="replace").splitlines()
    for index, line in enumerate(lines, start=1):
        match = FUNCTION_RE.match(line)
        if match:
            current_function = match.group("name")
        if symbol not in line:
            continue
        findings.append(Finding(
            file=path.relative_to(root).as_posix(),
            line=index,
            function=current_function,
            kind=classify_kind(line, symbol),
            category=classify_category(path, line, current_function),
            text=line.strip(),
        ))
    return findings

def markdown(findings: list[Finding], source_sha: str, symbol: str) -> str:
    counts: dict[tuple[str, str], int] = {}
    for finding in findings:
        key = (finding.kind, finding.category)
        counts[key] = counts.get(key, 0) + 1

    out = [
        "# Cadence consumer inventory",
        "",
        f"Symbol: `{symbol}`",
        f"Source tree SHA-256: `{source_sha}`",
        f"Findings: `{len(findings)}`",
        "",
        "## Summary",
        "",
        "| Kind | Category | Count |",
        "|---|---|---:|",
    ]
    for (kind, category), count in sorted(counts.items()):
        out.append(f"| {kind} | {category} | {count} |")

    out += [
        "",
        "## Findings",
        "",
        "| File | Line | Function | Kind | Category | Source |",
        "|---|---:|---|---|---|---|",
    ]
    for item in findings:
        text = item.text.replace("|", "\\|")
        out.append(
            f"| `{item.file}` | {item.line} | "
            f"`{item.function or ''}` | {item.kind} | "
            f"{item.category} | `{text}` |"
        )
    return "\n".join(out) + "\n"

def hash_tree(paths: Iterable[Path], root: Path) -> str:
    digest = hashlib.sha256()
    for path in paths:
        digest.update(path.relative_to(root).as_posix().encode())
        digest.update(b"\0")
        digest.update(path.read_bytes())
        digest.update(b"\0")
    return digest.hexdigest()

def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", type=Path, required=True)
    parser.add_argument("--symbol", default=SYMBOL_DEFAULT)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--markdown", type=Path, required=True)
    args = parser.parse_args()

    root = args.root.resolve()
    paths = list(iter_sources(root))
    findings = [
        finding
        for path in paths
        for finding in scan_file(path, root, args.symbol)
    ]
    source_sha = hash_tree(paths, root)

    payload = {
        "schemaVersion": 1,
        "symbol": args.symbol,
        "sourceRoot": root.name,
        "sourceTreeSha256": source_sha,
        "findings": [asdict(item) for item in findings],
    }

    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(
        json.dumps(payload, indent=2, sort_keys=True) + "\n",
        encoding="utf-8",
    )
    args.markdown.parent.mkdir(parents=True, exist_ok=True)
    args.markdown.write_text(
        markdown(findings, source_sha, args.symbol),
        encoding="utf-8",
    )

    print(json.dumps({
        "findings": len(findings),
        "json": str(args.output),
        "markdown": str(args.markdown),
        "sourceTreeSha256": source_sha,
    }, indent=2))
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
```

The classifications are starting points. Luna must manually review every
`unknown`, write and ordinary-gameplay fixed-step candidate.
