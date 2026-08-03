#!/usr/bin/env python3
"""Guard the public SM64DS camera telemetry address contract."""

from pathlib import Path


SOURCE = (
    Path(__file__).resolve().parents[3]
    / "app/src/main/cpp/MelonInstance.cpp"
).read_text()
INPUT_SOURCE = (
    Path(__file__).resolve().parents[3]
    / "app/src/main/java/me/magnum/melonds/ui/emulator/input/InputProcessor.kt"
).read_text()


def main() -> None:
    required = {
        "kGenericCameraEntryAddress": "0x02009E70",
        "kCameraTargetBridgeAddAddress": "0x0200A790",
        "kCameraTargetBridgeLoadAddress": "0x0200A79C",
        "kCameraTargetBridgeApplyAddress": "0x0200A7A4",
        "kModeSpecificOrbitEntryAddress": "0x0200BB28",
        "kLegacyDigitalYawAddress": "0x0200BCF0",
    }
    for name, address in required.items():
        assert f"{name} = {address}" in SOURCE, (name, address)

    assert "genericCameraEntryAddress" in SOURCE and "0x02009E70" in SOURCE
    assert "modeSpecificOrbitEntryAddress" in SOURCE and "0x0200BB28" in SOURCE
    assert "legacyDigitalYawAddress" in SOURCE and "0x0200BCF0" in SOURCE
    assert "genericCameraEntryWord == 0xEA01AF4Fu" in SOURCE
    assert "cameraTargetBridgeAddWord == 0xE2880C01u" in SOURCE
    assert "cameraTargetBridgeLoadWord == 0xE1D028F4u" in SOURCE
    assert "cameraTargetBridgeApplyWord == 0xE0811002u" in SOURCE
    assert "cameraTargetBridgePresent" in SOURCE
    assert "cameraTargetX" in SOURCE and "cameraPositionX" in SOURCE
    assert "cameraEffectiveModePointer" in SOURCE
    assert "cameraSecondaryPointer" in SOURCE
    assert "cameraFlags & 0x100u" in SOURCE
    assert "slot2CameraRecenterAppliedSequence" in SOURCE
    assert "slot2CameraRecenterAppliedCount" in SOURCE
    assert "exactCameraPatchPresent" in SOURCE
    assert "ARM9Write16(cameraPointer + 0x184, 0)" in SOURCE
    assert "SMOOTH_CAMERA_FLAG_RECENTER_SOUND" not in INPUT_SOURCE
    assert "SMOOTH_CAMERA_FLAG_ENABLED.toShort()" in INPUT_SOURCE
    assert "keyEvent.repeatCount == 0" in INPUT_SOURCE
    assert "smoothCameraRecenterSequence = (smoothCameraRecenterSequence + 1).toShort()" in INPUT_SOURCE
    assert "cameraHookWord" not in SOURCE
    assert "cameraLegacyHookWord" not in SOURCE


if __name__ == "__main__":
    main()
