# Python — deterministic ARM patch build

Save as `tools/thords/60fps/build_arm_patch.py`.

```python
#!/usr/bin/env python3
from __future__ import annotations

import argparse
import hashlib
import json
import os
import shutil
import subprocess
from pathlib import Path

def find_tool(name: str) -> str:
    override = os.environ.get(name.upper().replace("-", "_"))
    if override:
        return override
    found = shutil.which(name)
    if found:
        return found

    sdk = Path(os.environ.get(
        "ANDROID_SDK_ROOT",
        os.environ.get("ANDROID_HOME", Path.home() / "Library/Android/sdk"),
    ))
    candidates = sorted(
        sdk.glob(f"ndk/*/toolchains/llvm/prebuilt/*/bin/{name}"),
        reverse=True,
    )
    if candidates:
        return str(candidates[0])
    raise FileNotFoundError(name)

def run(command: list[str]) -> None:
    subprocess.run(command, check=True)

def digest(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()

def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("source", type=Path)
    parser.add_argument("--out-dir", type=Path, required=True)
    parser.add_argument("--manifest", type=Path, required=True)
    args = parser.parse_args()

    clang = find_tool("clang")
    objcopy = find_tool("llvm-objcopy")
    objdump = find_tool("llvm-objdump")

    args.out_dir.mkdir(parents=True, exist_ok=True)
    obj = args.out_dir / "patch.o"
    binary = args.out_dir / "patch.bin"
    disassembly = args.out_dir / "patch.disasm"

    run([
        clang,
        "--target=armv5te-none-eabi",
        "-march=armv5te",
        "-marm",
        "-ffreestanding",
        "-fno-builtin",
        "-c",
        str(args.source),
        "-o",
        str(obj),
    ])
    run([
        objcopy,
        "-O", "binary",
        "--only-section=.text",
        str(obj),
        str(binary),
    ])
    with disassembly.open("w", encoding="utf-8") as handle:
        subprocess.run(
            [objdump, "-d", str(obj)],
            check=True,
            stdout=handle,
            text=True,
        )

    payload = {
        "schemaVersion": 1,
        "source": args.source.as_posix(),
        "sourceSha256": digest(args.source),
        "objectSha256": digest(obj),
        "binarySha256": digest(binary),
        "binarySize": binary.stat().st_size,
        "clang": subprocess.check_output(
            [clang, "--version"], text=True
        ).splitlines()[0],
    }
    args.manifest.write_text(
        json.dumps(payload, indent=2, sort_keys=True) + "\n",
        encoding="utf-8",
    )
    print(json.dumps(payload, indent=2))
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
```
