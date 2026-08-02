#!/usr/bin/env python3
"""Guard the public SM64DS camera telemetry address contract."""

from pathlib import Path


SOURCE = (
    Path(__file__).resolve().parents[3]
    / "app/src/main/cpp/MelonInstance.cpp"
).read_text()


def main() -> None:
    required = {
        "kGenericCameraEntryAddress": "0x02009E70",
        "kModeSpecificOrbitEntryAddress": "0x0200BB28",
        "kLegacyDigitalYawAddress": "0x0200BCF0",
    }
    for name, address in required.items():
        assert f"{name} = {address}" in SOURCE, (name, address)

    assert "genericCameraEntryAddress" in SOURCE and "0x02009E70" in SOURCE
    assert "modeSpecificOrbitEntryAddress" in SOURCE and "0x0200BB28" in SOURCE
    assert "legacyDigitalYawAddress" in SOURCE and "0x0200BCF0" in SOURCE
    assert "genericCameraEntryWord == 0xEA01AF4Fu" in SOURCE
    assert "cameraHookWord" not in SOURCE
    assert "cameraLegacyHookWord" not in SOURCE


if __name__ == "__main__":
    main()
