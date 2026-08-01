#!/usr/bin/env python3
"""Verify an exact-frame Castle Garden transition surface sequence."""

from __future__ import annotations

import argparse
import json
import math
import sys
from pathlib import Path
from typing import Any

from m7_surface_geometry import read_png


def top_presenter(frame: dict[str, Any]) -> dict[str, Any] | None:
    return next(
        (
            record
            for record in frame.get("presenter", {}).get("records", [])
            if record.get("surfaceRole") == "top"
        ),
        None,
    )


def maximum_run(values: list[str]) -> int:
    longest = 0
    current = 0
    previous = None
    for value in values:
        current = current + 1 if value == previous else 1
        longest = max(longest, current)
        previous = value
    return longest


def nonconsecutive_reappearances(values: list[str]) -> int:
    runs = [value for index, value in enumerate(values) if index == 0 or value != values[index - 1]]
    return len(runs) - len(set(runs))


def color(summary: dict[str, Any]) -> tuple[float, float, float]:
    return (
        float(summary.get("meanRed", 0.0)),
        float(summary.get("meanGreen", 0.0)),
        float(summary.get("meanBlue", 0.0)),
    )


def color_delta(first: dict[str, Any], last: dict[str, Any]) -> float:
    return math.dist(color(first), color(last))


def changed_pixel_ratio(first_path: Path, last_path: Path, tolerance: int = 12) -> float:
    first_width, first_height, first_pixels = read_png(first_path)
    last_width, last_height, last_pixels = read_png(last_path)
    if (first_width, first_height) != (last_width, last_height):
        raise ValueError(
            f"endpoint dimensions differ: "
            f"{first_width}x{first_height} != {last_width}x{last_height}"
        )
    changed = sum(
        max(abs(a - b) for a, b in zip(first, last)) > tolerance
        for first, last in zip(first_pixels, last_pixels)
    )
    return changed / len(first_pixels)


def analyze(
    manifest: dict[str, Any],
    expected_frames: int,
    capture_dir: Path | None = None,
) -> tuple[dict[str, Any], bool]:
    frames = manifest.get("frames", [])
    final_hashes = [
        str(frame.get("finalSummary", {}).get("pixelHash64", ""))
        for frame in frames
    ]
    source_hashes = [
        str(frame.get("sourceSummary", {}).get("pixelHash64", ""))
        for frame in frames
    ]
    alpha_failures = []
    black_failures = []
    rect_failures = []
    not_ready_frames = []
    exact_steps = True
    previous_end = None

    for frame in frames:
        start = int(frame.get("startFrame", -1))
        end = int(frame.get("endFrame", -1))
        exact_steps = exact_steps and end == start + 1
        if previous_end is not None:
            exact_steps = exact_steps and start == previous_end
        previous_end = end
        if frame.get("frameReady") is not True:
            not_ready_frames.append(int(frame.get("index", -1)))

        final = frame.get("finalSummary", {})
        pixel_count = int(final.get("pixelCount", 0))
        if (
            int(final.get("alphaMin", -1)) != 255
            or int(final.get("alphaMax", -1)) != 255
            or int(final.get("opaquePixels", -1)) != pixel_count
        ):
            alpha_failures.append(int(frame.get("index", -1)))
        if int(final.get("nonBlackPixels", 0)) < pixel_count * 0.05:
            black_failures.append(int(frame.get("index", -1)))

        top = top_presenter(frame)
        rect = top.get("topRect", {}) if top else {}
        aspect = (
            float(rect.get("width", 0)) / float(rect.get("height", 1))
            if int(rect.get("height", 0)) > 0
            else 0.0
        )
        if (
            top is None
            or top.get("outputWidth") != 1920
            or top.get("outputHeight") != 1080
            or top.get("rotatePrimaryVulkan180") is not True
            or rect.get("enabled") is not True
            or min(abs(aspect - 16 / 9), abs(aspect - 4 / 3)) > 0.01
        ):
            rect_failures.append(int(frame.get("index", -1)))

    keyframe_count = sum(
        int(frame.get("pngBytes", 0)) > 0 and int(frame.get("sourcePngBytes", 0)) > 0
        for frame in frames
    )
    structural_pass = (
        manifest.get("schema") == "thords.m7-surface-sequence.v1"
        and manifest.get("target") == "main"
        and manifest.get("summaryOnly") is True
        and int(manifest.get("warmupFrames", 0)) >= 10
        and abs(float(manifest.get("leftY", 0.0))) >= 0.5
        and manifest.get("result") == "PASS"
        and len(frames) == expected_frames
        and exact_steps
        and all(
            frame.get("inputHandled") is True
            and frame.get("frameAdvanced") is True
            and frame.get("presenterComplete") is True
            and int(frame.get("presenterRecordCount", 0)) == 2
            and frame.get("finalSummary", {}).get("width") == 1920
            and frame.get("finalSummary", {}).get("height") == 1080
            and frame.get("sourceSummary", {}).get("width", 0) > 0
            and frame.get("sourceSummary", {}).get("height", 0) > 0
            for frame in frames
        )
        and not rect_failures
        and keyframe_count == 3
    )
    final_repeat_run = maximum_run(final_hashes)
    source_repeat_run = maximum_run(source_hashes)
    stale_reappearances = nonconsecutive_reappearances(final_hashes)
    first_final = frames[0].get("finalSummary", {}) if frames else {}
    last_final = frames[-1].get("finalSummary", {}) if frames else {}
    first_source = frames[0].get("sourceSummary", {}) if frames else {}
    last_source = frames[-1].get("sourceSummary", {}) if frames else {}
    final_delta = color_delta(first_final, last_final)
    source_delta = color_delta(first_source, last_source)
    endpoint_final_changed_ratio = None
    endpoint_source_changed_ratio = None
    if capture_dir is not None and frames:
        endpoint_final_changed_ratio = changed_pixel_ratio(
            capture_dir / str(frames[0].get("file")),
            capture_dir / str(frames[-1].get("file")),
        )
        endpoint_source_changed_ratio = changed_pixel_ratio(
            capture_dir / str(frames[0].get("sourceFile")),
            capture_dir / str(frames[-1].get("sourceFile")),
        )
    transition_change_pass = (
        endpoint_final_changed_ratio is not None
        and endpoint_source_changed_ratio is not None
        and endpoint_final_changed_ratio >= 0.05
        and endpoint_source_changed_ratio >= 0.05
    ) or (
        capture_dir is None
        and max(final_delta, source_delta) >= 10.0
    )
    content_pass = (
        not alpha_failures
        and not black_failures
        and final_repeat_run <= 3
        and source_repeat_run <= 3
        and stale_reappearances == 0
        and len(set(final_hashes)) >= max(3, expected_frames // 4)
        and final_hashes[0] != final_hashes[-1]
        and source_hashes[0] != source_hashes[-1]
        and transition_change_pass
    )
    passed = structural_pass and content_pass
    report = {
        "schema": "thords.m7-transition-proof.v1",
        "result": "PASS" if passed else "FAIL",
        "gate": "W20",
        "scenario": "Castle Garden castle-door transition",
        "transitionInput": manifest.get("transitionInput"),
        "transitionInputFrames": manifest.get("transitionInputFrames"),
        "warmupFrames": manifest.get("warmupFrames"),
        "leftX": manifest.get("leftX"),
        "leftY": manifest.get("leftY"),
        "structuralPass": structural_pass,
        "contentPass": content_pass,
        "frameCount": len(frames),
        "frameRange": {
            "start": frames[0].get("startFrame") if frames else None,
            "end": frames[-1].get("endFrame") if frames else None,
        },
        "exactConsecutiveSteps": exact_steps,
        "keyframeCount": keyframe_count,
        "distinctFinalHashes": len(set(final_hashes)),
        "distinctSourceHashes": len(set(source_hashes)),
        "maximumFinalRepeatRun": final_repeat_run,
        "maximumSourceRepeatRun": source_repeat_run,
        "nonconsecutiveFinalHashReappearances": stale_reappearances,
        "alphaFailureFrames": alpha_failures,
        "blackFailureFrames": black_failures,
        "rectFailureFrames": rect_failures,
        "notReadyFrames": not_ready_frames,
        "endpointFinalColorDelta": round(final_delta, 4),
        "endpointSourceColorDelta": round(source_delta, 4),
        "endpointFinalChangedPixelRatio": (
            round(endpoint_final_changed_ratio, 6)
            if endpoint_final_changed_ratio is not None
            else None
        ),
        "endpointSourceChangedPixelRatio": (
            round(endpoint_source_changed_ratio, 6)
            if endpoint_source_changed_ratio is not None
            else None
        ),
        "firstFinalMeanRgb": [round(value, 4) for value in color(first_final)],
        "lastFinalMeanRgb": [round(value, 4) for value in color(last_final)],
        "firstSourceMeanRgb": [round(value, 4) for value in color(first_source)],
        "lastSourceMeanRgb": [round(value, 4) for value in color(last_source)],
    }
    return report, passed


def synthetic_manifest() -> dict[str, Any]:
    frames = []
    hashes = ("a", "a", "b", "b", "c", "c")
    colors = ((170, 100, 20),) * 2 + ((120, 110, 90),) * 2 + ((40, 110, 180),) * 2
    for index, (pixel_hash, mean_rgb) in enumerate(zip(hashes, colors)):
        summary = {
            "width": 1920,
            "height": 1080,
            "pixelCount": 1920 * 1080,
            "pixelHash64": pixel_hash,
            "alphaMin": 255,
            "alphaMax": 255,
            "opaquePixels": 1920 * 1080,
            "nonBlackPixels": 1920 * 1080,
            "meanRed": mean_rgb[0],
            "meanGreen": mean_rgb[1],
            "meanBlue": mean_rgb[2],
        }
        source = {**summary, "width": 256, "height": 192, "pixelCount": 256 * 192}
        source["opaquePixels"] = source["pixelCount"]
        source["nonBlackPixels"] = source["pixelCount"]
        frames.append(
            {
                "index": index,
                "pngBytes": 1 if index in (0, 3, 5) else 0,
                "sourcePngBytes": 1 if index in (0, 3, 5) else 0,
                "inputHandled": True,
                "startFrame": 100 + index,
                "endFrame": 101 + index,
                "frameReady": True,
                "frameAdvanced": True,
                "presenterComplete": True,
                "presenterRecordCount": 2,
                "finalSummary": summary,
                "sourceSummary": source,
                "presenter": {
                    "records": [
                        {
                            "surfaceRole": "top",
                            "outputWidth": 1920,
                            "outputHeight": 1080,
                            "rotatePrimaryVulkan180": True,
                            "topRect": {
                                "enabled": True,
                                "x": 0,
                                "y": 0,
                                "width": 1920,
                                "height": 1080,
                            },
                        },
                        {"surfaceRole": "bottom"},
                    ]
                },
            }
        )
    return {
        "schema": "thords.m7-surface-sequence.v1",
        "target": "main",
        "summaryOnly": True,
        "transitionInput": None,
        "transitionInputFrames": 0,
        "warmupFrames": 12,
        "leftX": 0.0,
        "leftY": -1.0,
        "result": "PASS",
        "frames": frames,
    }


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--manifest", type=Path)
    parser.add_argument("--capture-dir", type=Path)
    parser.add_argument("--expected-frames", type=int, default=384)
    parser.add_argument("--output", type=Path)
    parser.add_argument("--self-test", action="store_true")
    args = parser.parse_args()
    if args.self_test:
        _, passed = analyze(synthetic_manifest(), 6)
        print(f"m7_transition_proof self-test: {'PASS' if passed else 'FAIL'}")
        return 0 if passed else 1
    if args.manifest is None:
        parser.error("--manifest is required")
    if args.capture_dir is None:
        parser.error("--capture-dir is required")

    report, passed = analyze(
        json.loads(args.manifest.read_text()),
        args.expected_frames,
        args.capture_dir,
    )
    rendered = json.dumps(report, indent=2, sort_keys=True) + "\n"
    if args.output:
        args.output.write_text(rendered)
    else:
        sys.stdout.write(rendered)
    return 0 if passed else 2


if __name__ == "__main__":
    raise SystemExit(main())
