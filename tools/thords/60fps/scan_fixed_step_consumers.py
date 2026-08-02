#!/usr/bin/env python3
"""Generate deterministic, non-proof candidates for fixed-step consumers."""
from __future__ import annotations

import argparse
import hashlib
import json
import re
from pathlib import Path

SOURCE_SUFFIXES = {".c", ".cc", ".cpp", ".h", ".hpp"}
ROOT_WORDS = (
    "advance", "behavior", "callback", "camera", "cutscene", "main",
    "particle", "render", "step", "timer", "update",
)
CADENCE = re.compile(r"\bdata_0208ee44\b", re.IGNORECASE)
POINTER_MUTATION = re.compile(
    r"(?:"
    r"\*\s*\([^;=]+\)\s*\([^;=]+\)"
    r"|(?:\b(?:self|this)->|\b(?:self|this)\s*\+)[^;=]+"
    r"|\b[A-Za-z_]\w*(?:->|\.)[A-Za-z_]\w*"
    r")\s*(?:\+\+|--|\+=|-=|\*=|/=|=(?!=))"
)
TIMING_API = re.compile(
    r"\b(?:"
    r"Advance|Approach|DecTowards|IncTowards|SetAnim|UpdateAngle|"
    r"UpdateAnimation|UpdateTimer|WillHitFrame"
    r")\w*\s*\("
)
MUTATION = re.compile(r"(?:\+\+|--|\+=|-=|\*=|/=)")
TIMING_NAME = re.compile(
    r"\b(?:"
    r"accel|angle|anim|cooldown|count|delay|frame|life|rot|speed|step|"
    r"tick|timer|velocity"
    r")\w*\b",
    re.IGNORECASE,
)
NUMERIC_ONLY = re.compile(r"(?<![\w])(?:0x[0-9a-fA-F]+|\d+)(?![\w])")
FUNCTION = re.compile(
    r"^\s*(?:[\w:<>,~*&]+\s+)+([\w:~]+)\s*\([^;]*\)\s*(?:const\s*)?(?:\{|$)"
)


def source_context(path: Path, line: str, current: str) -> str:
    match = FUNCTION.match(line)
    if match:
        return match.group(1)
    if current:
        return current
    stem = path.stem
    return stem if stem.startswith(("_ZN", "func_")) else ""


def semantic_category(path: Path, line: str, context: str) -> str:
    text = f"{path.as_posix()} {context} {line}".lower()
    if any(word in text for word in ("message", "hud", "minimap", "oam")):
        return "message_hud"
    if any(word in text for word in ("camera", "cutscene")):
        return "camera_cutscene"
    if any(word in text for word in ("particle", "effect", "glitter", "splash")):
        return "particle_effect"
    if any(word in text for word in ("sound", "audio", "voice")):
        return "audio_trigger"
    if any(word in text for word in ("anim", "frame")):
        return "animation"
    if any(word in text for word in ("angle", "rotation", "rotate", "yaw")):
        return "rotation"
    if any(word in text for word in ("timer", "countdown", "cooldown", "delay")):
        return "timer_counter"
    if any(word in text for word in (
        "actor", "behavior", "clsn", "gravity", "physics", "player",
        "position", "speed", "velocity",
    )):
        return "world_physics"
    if "ov" in path.name.lower():
        return "scene_overlay"
    return "unclassified"


def source_files(root: Path) -> list[Path]:
    return sorted(
        path for path in root.rglob("*")
        if path.is_file() and path.suffix in SOURCE_SUFFIXES
    )


def tree_hash(root: Path, paths: list[Path]) -> str:
    digest = hashlib.sha256()
    for path in paths:
        digest.update(path.relative_to(root).as_posix().encode())
        digest.update(b"\0")
        digest.update(path.read_bytes())
        digest.update(b"\0")
    return digest.hexdigest()


def candidate_kind(line: str, context: str) -> tuple[str, str] | None:
    stripped = line.strip()
    declaration = stripped.startswith((
        "extern ", "struct ", "class ", "typedef ", "using ",
        "void ", "int ", "bool ", "short ", "unsigned ",
    ))
    loop_control = stripped.startswith(("for ", "for("))
    rooted = any(word in context.lower() for word in ROOT_WORDS)
    if CADENCE.search(line):
        return "cadence_symbol_ref", "A"
    if rooted and POINTER_MUTATION.search(line):
        return "direct_pointer_member_mutation", "B"
    if rooted and not declaration and not loop_control and (
        TIMING_API.search(line)
        or (MUTATION.search(line) and TIMING_NAME.search(line))
    ):
        return "timing_or_fixed_point_mutation", "C"
    return None


def scan(root: Path, include_source: bool = False) -> dict:
    root = root.resolve()
    paths = source_files(root)
    findings = []
    numeric_only_lines = 0
    for path in paths:
        context = ""
        for number, line in enumerate(path.read_text(encoding="utf-8", errors="replace").splitlines(), 1):
            context = source_context(path, line, context)
            result = candidate_kind(line, context)
            if result:
                kind, confidence = result
                finding = {
                    "file": path.relative_to(root).as_posix(),
                    "line": number,
                    "context": context or None,
                    "kind": kind,
                    "confidence": confidence,
                    "category": semantic_category(path, line, context),
                    "operator": (MUTATION.search(line).group(0)
                                 if MUTATION.search(line) else None),
                    "constants": NUMERIC_ONLY.findall(line),
                }
                if include_source:
                    finding["source"] = line.strip()
                findings.append(finding)
            elif NUMERIC_ONLY.search(line):
                numeric_only_lines += 1
    counts = {}
    for finding in findings:
        counts[finding["kind"]] = counts.get(finding["kind"], 0) + 1
    categories = {}
    for finding in findings:
        category = finding["category"]
        categories[category] = categories.get(category, 0) + 1
    return {
        "schemaVersion": 2,
        "purpose": "candidate_generation_only_not_proof",
        "completenessClaim": "none",
        "sourceTextIncluded": include_source,
        "sourceRoot": root.name,
        "sourceTreeSha256": tree_hash(root, paths),
        "filesScanned": len(paths),
        "counts": counts,
        "categoryCounts": categories,
        "lowConfidenceNumericOnlyLinesExcluded": numeric_only_lines,
        "findings": findings,
    }


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", type=Path, required=True)
    parser.add_argument("--output", type=Path)
    parser.add_argument("--include-source", action="store_true")
    args = parser.parse_args()
    payload = scan(args.root, args.include_source)
    text = json.dumps(payload, indent=2, sort_keys=True) + "\n"
    if args.output:
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(text, encoding="utf-8")
    print(text, end="")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
