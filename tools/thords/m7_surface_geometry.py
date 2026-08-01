#!/usr/bin/env python3
"""Measure paired internal/final ThorDS surface captures without image dependencies."""

from __future__ import annotations

import argparse
import json
import struct
import sys
import tempfile
import zlib
from pathlib import Path
from typing import Any, Callable

PNG_SIGNATURE = b"\x89PNG\r\n\x1a\n"
ASPECT_TOLERANCE_PERCENT = 2.0


def read_png(path: Path) -> tuple[int, int, list[tuple[int, int, int]]]:
    data = path.read_bytes()
    if not data.startswith(PNG_SIGNATURE):
        raise ValueError(f"{path}: invalid PNG signature")
    offset = len(PNG_SIGNATURE)
    compressed = bytearray()
    width = height = color_type = bit_depth = interlace = 0
    while offset < len(data):
        length = struct.unpack(">I", data[offset : offset + 4])[0]
        chunk_type = data[offset + 4 : offset + 8]
        chunk = data[offset + 8 : offset + 8 + length]
        offset += 12 + length
        if chunk_type == b"IHDR":
            width, height, bit_depth, color_type, _, _, interlace = struct.unpack(
                ">IIBBBBB", chunk
            )
        elif chunk_type == b"IDAT":
            compressed.extend(chunk)
        elif chunk_type == b"IEND":
            break
    if bit_depth != 8 or color_type not in (2, 6) or interlace != 0:
        raise ValueError(f"{path}: only non-interlaced 8-bit RGB/RGBA PNG is supported")

    channels = 3 if color_type == 2 else 4
    stride = width * channels
    raw = zlib.decompress(bytes(compressed))
    rows: list[bytearray] = []
    cursor = 0
    for _ in range(height):
        filter_type = raw[cursor]
        cursor += 1
        encoded = raw[cursor : cursor + stride]
        cursor += stride
        previous = rows[-1] if rows else bytearray(stride)
        row = bytearray(stride)
        for index, value in enumerate(encoded):
            left = row[index - channels] if index >= channels else 0
            above = previous[index]
            upper_left = previous[index - channels] if index >= channels else 0
            if filter_type == 0:
                predictor = 0
            elif filter_type == 1:
                predictor = left
            elif filter_type == 2:
                predictor = above
            elif filter_type == 3:
                predictor = (left + above) // 2
            elif filter_type == 4:
                p = left + above - upper_left
                distances = (abs(p - left), abs(p - above), abs(p - upper_left))
                predictor = (left, above, upper_left)[distances.index(min(distances))]
            else:
                raise ValueError(f"{path}: unsupported PNG filter {filter_type}")
            row[index] = (value + predictor) & 0xFF
        rows.append(row)

    pixels = [
        tuple(row[index : index + 3])
        for row in rows
        for index in range(0, stride, channels)
    ]
    return width, height, pixels


def normalize_rect(
    pixels: list[tuple[int, int, int]],
    image_width: int,
    rect: dict[str, Any],
    output_width: int,
    output_height: int,
) -> list[tuple[int, int, int]]:
    rect_x = int(rect["x"])
    rect_y = int(rect["y"])
    rect_width = int(rect["width"])
    rect_height = int(rect["height"])
    return [
        pixels[
            (rect_y + min(rect_height - 1, ((y * 2 + 1) * rect_height) // (output_height * 2)))
            * image_width
            + rect_x
            + min(rect_width - 1, ((x * 2 + 1) * rect_width) // (output_width * 2))
        ]
        for y in range(output_height)
        for x in range(output_width)
    ]


def mask_bbox(
    pixels: list[tuple[int, int, int]],
    width: int,
    roi: tuple[int, int, int, int],
    predicate: Callable[[tuple[int, int, int]], bool],
) -> dict[str, Any]:
    x0, y0, x1, y1 = roi
    points = [
        (x, y)
        for y in range(y0, y1)
        for x in range(x0, x1)
        if predicate(pixels[y * width + x])
    ]
    if not points:
        return {"valid": False, "pixelCount": 0}
    xs = [point[0] for point in points]
    ys = [point[1] for point in points]
    box_width = max(xs) - min(xs) + 1
    box_height = max(ys) - min(ys) + 1
    return {
        "valid": True,
        "pixelCount": len(points),
        "x": min(xs),
        "y": min(ys),
        "width": box_width,
        "height": box_height,
        "aspect": round(box_width / box_height, 6),
    }


def ratio_delta_percent(reference: dict[str, Any], candidate: dict[str, Any]) -> float:
    if not reference.get("valid") or not candidate.get("valid"):
        return float("inf")
    reference_aspect = float(reference["aspect"])
    return abs(float(candidate["aspect"]) - reference_aspect) / reference_aspect * 100.0


def analyze(
    manifest_path: Path,
    frame_index: int,
    touch_grid_path: Path | None,
) -> tuple[dict[str, Any], bool]:
    manifest = json.loads(manifest_path.read_text())
    frames = manifest.get("frames", [])
    frame = next(
        (item for item in frames if int(item.get("index", -1)) == frame_index),
        None,
    )
    if frame is None:
        raise ValueError(f"frame index {frame_index} is absent")

    source_path = manifest_path.parent / frame["sourceFile"]
    final_path = manifest_path.parent / frame["file"]
    source_width, source_height, source_pixels = read_png(source_path)
    final_width, final_height, final_pixels = read_png(final_path)
    target_role = "bottom" if manifest.get("target") == "secondary" else "top"
    rect_name = "bottomRect" if target_role == "bottom" else "topRect"
    presenter_records = frame.get("presenter", {}).get("records", [])
    presenter = next(
        (record for record in presenter_records if record.get("surfaceRole") == target_role),
        None,
    )
    if presenter is None:
        raise ValueError(f"presenter record for {target_role} is absent")
    rect = presenter[rect_name]
    normalized_pixels = normalize_rect(
        final_pixels,
        final_width,
        rect,
        source_width,
        source_height,
    )

    def color_stats(reference_pixels: list[tuple[int, int, int]]) -> tuple[int, int]:
        return (
            sum(
                reference == candidate
                for reference, candidate in zip(reference_pixels, normalized_pixels)
            ),
            sum(
                abs(reference[channel] - candidate[channel])
                for reference, candidate in zip(reference_pixels, normalized_pixels)
                for channel in range(3)
            ),
        )

    direct_stats = color_stats(source_pixels)
    red_blue_swapped_pixels = [
        (blue, green, red)
        for red, green, blue in source_pixels
    ]
    red_blue_swapped_stats = color_stats(red_blue_swapped_pixels)
    if red_blue_swapped_stats[1] < direct_stats[1]:
        channel_mapping = "SOURCE_RED_BLUE_SWAPPED"
        exact_pixels, channel_delta = red_blue_swapped_stats
    else:
        channel_mapping = "IDENTITY"
        exact_pixels, channel_delta = direct_stats

    neutral_or_outline = lambda pixel: (
        max(pixel) < 48 or (max(pixel) - min(pixel) <= 55 and 35 <= max(pixel) <= 245)
    )
    dark_outline = lambda pixel: max(pixel) < 48
    circle_reference = mask_bbox(
        source_pixels,
        source_width,
        (1, 2, 19, 20),
        neutral_or_outline,
    )
    circle_final = mask_bbox(
        normalized_pixels,
        source_width,
        (1, 2, 19, 20),
        neutral_or_outline,
    )
    glyph_reference = mask_bbox(
        source_pixels,
        source_width,
        (39, 1, 49, 20),
        dark_outline,
    )
    glyph_final = mask_bbox(
        normalized_pixels,
        source_width,
        (39, 1, 49, 20),
        dark_outline,
    )
    circle_delta = ratio_delta_percent(circle_reference, circle_final)
    glyph_delta = ratio_delta_percent(glyph_reference, glyph_final)

    rect_aspect = int(rect["width"]) / int(rect["height"])
    source_aspect = source_width / source_height
    rect_aspect_error = abs(rect_aspect - source_aspect) / source_aspect * 100.0
    touch_grid = (
        json.loads(touch_grid_path.read_text())
        if touch_grid_path is not None
        else None
    )
    touch_pass = touch_grid is not None and touch_grid.get("result") == "PASS"
    lower_geometry_pass = (
        target_role == "bottom"
        and rect_aspect_error <= ASPECT_TOLERANCE_PERCENT
        and presenter.get("rotatePrimaryVulkan180") is False
        and touch_pass
    )
    measurement_pass = (
        circle_delta <= ASPECT_TOLERANCE_PERCENT
        and glyph_delta <= ASPECT_TOLERANCE_PERCENT
    )
    structural_pass = (
        manifest.get("result") == "PASS"
        and frame.get("presenterComplete") is True
        and source_width == int(frame.get("sourceWidth", 0))
        and source_height == int(frame.get("sourceHeight", 0))
        and len(normalized_pixels) == len(source_pixels)
    )
    report = {
        "schema": "thords.m7-surface-geometry-analysis.v1",
        "structuralPass": structural_pass,
        "target": manifest.get("target"),
        "frameIndex": frame_index,
        "emulatorFrame": frame.get("endFrame"),
        "source": {"width": source_width, "height": source_height},
        "finalSurface": {
            "width": final_width,
            "height": final_height,
            "rect": rect,
            "rotatePrimaryVulkan180": presenter.get("rotatePrimaryVulkan180"),
            "rectAspectErrorPercent": round(rect_aspect_error, 4),
        },
        "sourceToFinal": {
            "channelMapping": channel_mapping,
            "exactPixelRatio": round(exact_pixels / len(source_pixels), 6),
            "meanChannelDelta": round(channel_delta / (len(source_pixels) * 3), 6),
        },
        "knownCircle": {
            "name": "bottom-hud-bomb-body",
            "reference": circle_reference,
            "final": circle_final,
            "aspectDeltaPercent": round(circle_delta, 4),
            "measurementPass": circle_delta <= ASPECT_TOLERANCE_PERCENT,
        },
        "knownGlyph": {
            "name": "bottom-hud-digit-4",
            "reference": glyph_reference,
            "final": glyph_final,
            "aspectDeltaPercent": round(glyph_delta, 4),
            "measurementPass": glyph_delta <= ASPECT_TOLERANCE_PERCENT,
        },
        "gates": {
            "W04": {
                "status": "PARTIAL",
                "lowerReferenceMeasurement": "PASS" if measurement_pass else "FAIL",
                "topUiSafePlane": "NOT_MEASURED",
            },
            "W05": {
                "status": "PARTIAL",
                "lowerReferenceMeasurement": "PASS" if measurement_pass else "FAIL",
                "topUiSafePlane": "NOT_MEASURED",
            },
            "W06": {
                "status": "PASS" if lower_geometry_pass else "PARTIAL",
                "physicalRectAspect": "PASS"
                if rect_aspect_error <= ASPECT_TOLERANCE_PERCENT
                else "FAIL",
                "orientation": "PASS"
                if presenter.get("rotatePrimaryVulkan180") is False
                else "FAIL",
                "touchGrid": "PASS" if touch_pass else "NOT_MEASURED",
            },
        },
    }
    return report, structural_pass and measurement_pass and lower_geometry_pass


def write_png(path: Path, width: int, height: int, pixels: list[tuple[int, int, int]]) -> None:
    raw = b"".join(
        b"\x00" + bytes(channel for pixel in pixels[y * width : (y + 1) * width] for channel in pixel)
        for y in range(height)
    )
    ihdr = struct.pack(">IIBBBBB", width, height, 8, 2, 0, 0, 0)

    def chunk(kind: bytes, payload: bytes) -> bytes:
        return (
            struct.pack(">I", len(payload))
            + kind
            + payload
            + struct.pack(">I", zlib.crc32(kind + payload) & 0xFFFFFFFF)
        )

    path.write_bytes(
        PNG_SIGNATURE
        + chunk(b"IHDR", ihdr)
        + chunk(b"IDAT", zlib.compress(raw))
        + chunk(b"IEND", b"")
    )


def self_test() -> bool:
    with tempfile.TemporaryDirectory() as directory_name:
        directory = Path(directory_name)
        source_width, source_height = 256, 192
        source = [(20, 220, 20)] * (source_width * source_height)
        for y in range(5, 16):
            for x in range(4, 15):
                if (x - 9) ** 2 + (y - 10) ** 2 <= 25:
                    source[y * source_width + x] = (90, 90, 90)
        for y in range(3, 18):
            for x in range(41, 48):
                if x in (41, 47) or y in (3, 17):
                    source[y * source_width + x] = (0, 0, 0)
        final_width, final_height = 512, 384
        final = [
            source[(y // 2) * source_width + (x // 2)]
            for y in range(final_height)
            for x in range(final_width)
        ]
        write_png(directory / "source.png", source_width, source_height, source)
        write_png(directory / "frame.png", final_width, final_height, final)
        manifest = {
            "schema": "thords.m7-surface-sequence.v1",
            "target": "secondary",
            "result": "PASS",
            "frames": [
                {
                    "index": 0,
                    "file": "frame.png",
                    "sourceFile": "source.png",
                    "sourceWidth": source_width,
                    "sourceHeight": source_height,
                    "endFrame": 1,
                    "presenterComplete": True,
                    "presenter": {
                        "records": [
                            {
                                "surfaceRole": "bottom",
                                "bottomRect": {
                                    "enabled": True,
                                    "x": 0,
                                    "y": 0,
                                    "width": final_width,
                                    "height": final_height,
                                },
                                "rotatePrimaryVulkan180": False,
                            }
                        ]
                    },
                }
            ],
        }
        (directory / "manifest.json").write_text(json.dumps(manifest))
        (directory / "touch.json").write_text(json.dumps({"result": "PASS"}))
        report, passed = analyze(
            directory / "manifest.json",
            0,
            directory / "touch.json",
        )
        return (
            passed
            and report["gates"]["W06"]["status"] == "PASS"
            and report["knownGlyph"]["aspectDeltaPercent"] == 0.0
        )


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("manifest", nargs="?", type=Path)
    parser.add_argument("--frame-index", type=int, default=0)
    parser.add_argument("--touch-grid", type=Path)
    parser.add_argument("--output", type=Path)
    parser.add_argument("--self-test", action="store_true")
    args = parser.parse_args()
    if args.self_test:
        if not self_test():
            print("m7_surface_geometry self-test: FAIL", file=sys.stderr)
            return 1
        print("m7_surface_geometry self-test: PASS")
        return 0
    if args.manifest is None:
        parser.error("manifest is required unless --self-test is used")
    report, passed = analyze(args.manifest, args.frame_index, args.touch_grid)
    rendered = json.dumps(report, indent=2, sort_keys=True) + "\n"
    if args.output:
        args.output.write_text(rendered)
    else:
        sys.stdout.write(rendered)
    return 0 if passed else 2


if __name__ == "__main__":
    raise SystemExit(main())
