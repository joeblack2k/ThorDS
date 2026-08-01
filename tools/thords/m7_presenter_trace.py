#!/usr/bin/env python3
"""Validate and summarize bounded ThorDS Vulkan presenter traces."""

from __future__ import annotations

import argparse
import json
import sys
from collections import Counter, defaultdict
from pathlib import Path
from typing import Any

SCHEMA = "thords.presenter-trace.v1"
NATIVE_DS_ASPECT = 4.0 / 3.0
ASPECT_TOLERANCE_PERCENT = 2.0
RECT_NAMES = ("topRect", "bottomRect", "hybridTopRect", "hybridBottomRect")


def _aspect_measurement(rect: dict[str, Any]) -> dict[str, Any] | None:
    if not rect.get("enabled"):
        return None
    width = int(rect.get("width", 0))
    height = int(rect.get("height", 0))
    if width <= 0 or height <= 0:
        return {"valid": False}
    aspect = width / height
    error_percent = abs(aspect - NATIVE_DS_ASPECT) / NATIVE_DS_ASPECT * 100.0
    return {
        "valid": True,
        "aspect": round(aspect, 6),
        "nativeAspectErrorPercent": round(error_percent, 4),
        "withinTwoPercent": error_percent <= ASPECT_TOLERANCE_PERCENT,
    }


def analyze(trace: dict[str, Any]) -> tuple[dict[str, Any], bool]:
    errors: list[str] = []
    warnings: list[str] = []
    records = trace.get("records")
    if trace.get("schema") != SCHEMA:
        errors.append(f"schema must be {SCHEMA}")
    if not isinstance(records, list):
        records = []
        errors.append("records must be an array")

    target = int(trace.get("targetRecords", 0))
    declared_count = int(trace.get("recordCount", -1))
    if target < 1 or target > 512:
        errors.append("targetRecords must be in 1..512")
    if declared_count != len(records):
        errors.append("recordCount does not match records length")
    if trace.get("complete") is not True:
        errors.append("capture is incomplete")
    if trace.get("overflow") is not False:
        errors.append("capture overflowed")
    if target != len(records):
        errors.append("complete capture must contain targetRecords records")

    sequences = [int(record.get("sequence", -1)) for record in records]
    if sequences != list(range(len(records))):
        errors.append("record sequence is not contiguous")

    timestamps = [int(record.get("timestampNs", 0)) for record in records]
    if any(timestamp <= 0 for timestamp in timestamps):
        errors.append("record timestamps must be positive")
    if any(current <= previous for previous, current in zip(timestamps, timestamps[1:])):
        errors.append("record timestamps must be strictly increasing")

    role_records: dict[str, list[dict[str, Any]]] = defaultdict(list)
    invalid_rects = 0
    for record in records:
        role = str(record.get("surfaceRole", "none"))
        if role not in {"top", "bottom", "mixed", "none"}:
            errors.append(f"unknown surfaceRole={role}")
        role_records[role].append(record)

        output_width = int(record.get("outputWidth", 0))
        output_height = int(record.get("outputHeight", 0))
        if output_width <= 0 or output_height <= 0:
            errors.append("output dimensions must be positive")
            continue
        for rect_name in RECT_NAMES:
            rect = record.get(rect_name)
            if not isinstance(rect, dict):
                errors.append(f"{rect_name} is missing")
                continue
            if not rect.get("enabled"):
                continue
            x = int(rect.get("x", 0))
            y = int(rect.get("y", 0))
            width = int(rect.get("width", 0))
            height = int(rect.get("height", 0))
            if (
                width <= 0
                or height <= 0
                or x < 0
                or y < 0
                or x + width > output_width
                or y + height > output_height
            ):
                invalid_rects += 1
    if invalid_rects:
        errors.append(f"{invalid_rects} enabled presenter rects fall outside their output")

    role_summaries: dict[str, Any] = {}
    frame_regressions = 0
    duplicate_frame_ids = 0
    for role, grouped in sorted(role_records.items()):
        frame_ids = [int(record.get("frameId", 0)) for record in grouped]
        regressions = sum(
            current < previous for previous, current in zip(frame_ids, frame_ids[1:])
        )
        duplicates = sum(
            current == previous for previous, current in zip(frame_ids, frame_ids[1:])
        )
        frame_regressions += regressions
        duplicate_frame_ids += duplicates
        output_sizes = sorted(
            {
                f"{int(record.get('outputWidth', 0))}x{int(record.get('outputHeight', 0))}"
                for record in grouped
            }
        )
        draw_modes: Counter[int] = Counter()
        for record in grouped:
            mask = int(record.get("drawModeMask", 0))
            for mode in range(32):
                if mask & (1 << mode):
                    draw_modes[mode] += 1
        latest = grouped[-1]
        role_summaries[role] = {
            "recordCount": len(grouped),
            "firstFrameId": frame_ids[0] if frame_ids else None,
            "lastFrameId": frame_ids[-1] if frame_ids else None,
            "frameRegressions": regressions,
            "consecutiveDuplicateFrameIds": duplicates,
            "outputSizes": output_sizes,
            "rotatePrimaryVulkan180Values": sorted(
                {bool(record.get("rotatePrimaryVulkan180")) for record in grouped}
            ),
            "developerWidescreenProbeValues": sorted(
                {bool(record.get("developerWidescreenProbe")) for record in grouped}
            ),
            "drawModeRecordCounts": {
                str(mode): count for mode, count in sorted(draw_modes.items())
            },
            "finalRectAspects": {
                name: measurement
                for name in RECT_NAMES
                if (measurement := _aspect_measurement(latest.get(name, {}))) is not None
            },
        }
    if frame_regressions:
        errors.append(f"{frame_regressions} per-role frameId regressions")
    if duplicate_frame_ids:
        warnings.append(
            f"{duplicate_frame_ids} consecutive duplicate frameIds require content-level stale-frame review"
        )

    scenario = trace.get("scenario")
    scenario_summary: dict[str, Any] = {"present": False}
    pause_bracketed = False
    if isinstance(scenario, dict):
        native_started = int(trace.get("startedTimestampNs", 0))
        scenario_started = int(scenario.get("startedTimestampNs", 0))
        scenario_pause_started = int(scenario.get("pauseStartedTimestampNs", 0))
        scenario_pause_ended = int(scenario.get("pauseEndedTimestampNs", 0))
        pause_started = native_started + scenario_pause_started - scenario_started
        pause_ended = native_started + scenario_pause_ended - scenario_started
        before = sum(timestamp < pause_started for timestamp in timestamps)
        during = sum(pause_started <= timestamp <= pause_ended for timestamp in timestamps)
        after = sum(timestamp > pause_ended for timestamp in timestamps)
        pause_bracketed = (
            scenario.get("name") == "world-pause-world"
            and native_started > 0
            and scenario_started > 0
            and scenario_started <= scenario_pause_started < scenario_pause_ended
            and before > 0
            and after > 0
        )
        scenario_summary = {
            "present": True,
            "name": scenario.get("name"),
            "pauseDurationMs": round(
                (scenario_pause_ended - scenario_pause_started) / 1_000_000.0,
                3,
            ),
            "recordsBeforePause": before,
            "recordsDuringPause": during,
            "recordsAfterPause": after,
            "worldPauseWorldBracketed": pause_bracketed,
        }
        if not pause_bracketed:
            errors.append("world-pause-world markers do not bracket presenter records")
    else:
        warnings.append("world-pause-world scenario markers are absent")

    structural_pass = not errors
    report = {
        "schema": "thords.presenter-trace-analysis.v1",
        "sourceSchema": trace.get("schema"),
        "structuralPass": structural_pass,
        "recordCount": len(records),
        "targetRecords": target,
        "durationMs": (
            round((timestamps[-1] - timestamps[0]) / 1_000_000.0, 3)
            if len(timestamps) >= 2
            else 0.0
        ),
        "sequenceGaps": 0 if sequences == list(range(len(records))) else 1,
        "frameRegressions": frame_regressions,
        "consecutiveDuplicateFrameIds": duplicate_frame_ids,
        "scenario": scenario_summary,
        "surfaces": role_summaries,
        "m7": {
            "W20": {
                "status": "PARTIAL",
                "worldPauseWorldTrace": "PASS" if structural_pass and pause_bracketed else "FAIL",
                "paintingOrStarTransitionTrace": "NOT_MEASURED",
                "contentBlackOrStaleCheck": "NOT_MEASURED",
            }
        },
        "errors": errors,
        "warnings": warnings,
    }
    return report, structural_pass


def _synthetic_trace() -> dict[str, Any]:
    records = []
    for sequence, timestamp in enumerate((100, 200, 800, 900)):
        role = "top" if sequence % 2 == 0 else "bottom"
        records.append(
            {
                "sequence": sequence,
                "frameId": sequence // 2 + 1,
                "timestampNs": timestamp,
                "surfaceRole": role,
                "outputWidth": 400,
                "outputHeight": 300,
                "topRect": {
                    "enabled": role == "top",
                    "x": 0,
                    "y": 0,
                    "width": 400,
                    "height": 300,
                },
                "bottomRect": {
                    "enabled": role == "bottom",
                    "x": 0,
                    "y": 0,
                    "width": 400,
                    "height": 300,
                },
                "hybridTopRect": {"enabled": False, "x": 0, "y": 0, "width": 0, "height": 0},
                "hybridBottomRect": {"enabled": False, "x": 0, "y": 0, "width": 0, "height": 0},
                "drawModeMask": 1 << (7 if role == "top" else 3),
                "rotatePrimaryVulkan180": role == "top",
                "developerWidescreenProbe": role == "top",
            }
        )
    return {
        "schema": SCHEMA,
        "targetRecords": 4,
        "recordCount": 4,
        "complete": True,
        "overflow": False,
        "startedTimestampNs": 50,
        "records": records,
        "scenario": {
            "name": "world-pause-world",
            "startedTimestampNs": 1_000,
            "pauseStartedTimestampNs": 1_250,
            "pauseEndedTimestampNs": 1_650,
        },
    }


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("trace", nargs="?", type=Path)
    parser.add_argument("--output", type=Path)
    parser.add_argument("--self-test", action="store_true")
    args = parser.parse_args()

    if args.self_test:
        report, passed = analyze(_synthetic_trace())
        if not passed or report["m7"]["W20"]["worldPauseWorldTrace"] != "PASS":
            print(json.dumps(report, indent=2, sort_keys=True))
            return 1
        broken = _synthetic_trace()
        broken["records"][2]["sequence"] = 7
        _, broken_passed = analyze(broken)
        if broken_passed:
            print("self-test failed to reject a sequence gap", file=sys.stderr)
            return 1
        print("m7_presenter_trace self-test: PASS")
        return 0

    if args.trace is None:
        parser.error("trace is required unless --self-test is used")
    trace = json.loads(args.trace.read_text())
    report, passed = analyze(trace)
    rendered = json.dumps(report, indent=2, sort_keys=True) + "\n"
    if args.output:
        args.output.write_text(rendered)
    else:
        sys.stdout.write(rendered)
    return 0 if passed else 2


if __name__ == "__main__":
    raise SystemExit(main())
