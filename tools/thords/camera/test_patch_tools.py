import unittest

import build_patch
import verify_patch


class CameraPatchToolsTest(unittest.TestCase):
    def test_target_bridge_only_removes_eq_condition(self) -> None:
        self.assertEqual(build_patch.TARGET_BRIDGE_PATCHES, verify_patch.TARGET_BRIDGE_PATCHES)
        for _, original, replacement in build_patch.TARGET_BRIDGE_PATCHES:
            self.assertEqual(0, original >> 28)
            self.assertEqual(0xE, replacement >> 28)
            self.assertEqual(original & 0x0FFFFFFF, replacement & 0x0FFFFFFF)

    def test_generic_hook_still_targets_camera_payload(self) -> None:
        branch = build_patch.arm_branch(build_patch.HOOK, build_patch.PAYLOAD)
        self.assertEqual(build_patch.PAYLOAD, verify_patch.branch_target(build_patch.HOOK, branch))

if __name__ == "__main__":
    unittest.main()
