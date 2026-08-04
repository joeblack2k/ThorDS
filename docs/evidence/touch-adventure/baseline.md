# Thor lower-screen touch baseline

Captured before edits on 2026-08-04.

- Worktree: `/Users/nijssen/Documents/Projects/MelonDS-touch-adventure`
- Branch: `fix/thor-lower-touch-adventure-v1`
- Starting `HEAD`: `170784c6300af734099bfccd158c43edafc4325c`
- Starting `origin/feature/sm64ds-60fps-v3`: `170784c6300af734099bfccd158c43edafc4325c`
- Source branch was fetched with `git fetch origin --prune`.
- Worktree was clean before this evidence file was added.
- Original checkout with unrelated dirty ThorDS work was preserved at
  `/Users/nijssen/Documents/Projects/MelonDS`.

Historical touch commits audited before implementation:

- `c7a5df1f5b918b46d826b94b22dd668f08c7f2ba`
- `80388cf40225e0ddb262b9c219437a40f86a126f`
- `3aaf6d36da8070d7ded2e35e4817a5809ab2b5c4`
- `6ad362a463be60fadbf9dc5cad3861dd4318481a`
- `ffeb50657b8ade15e78e1dffc8677de8c2e1d825`

Initial source observations:

- The current `TouchscreenInputHandler` accepts an optional viewport provider,
  but the external presentation path must be checked for an active listener
  and a live rendered bottom-screen rect.
- Initial touches outside a provided viewport are currently clamped rather
  than rejected.
- `ACTION_CANCEL` does not currently release the touchscreen input.
- `EmulatorOverlayTracker` stores duplicate overlay additions in a mutable
  list.
- No build, device, or physical-finger result is claimed by this baseline.

Follow-up implementation evidence:

- `TouchPipelineTrace` keeps at most 32 touch sequences and records handler
  DOWN/MOVE/UP/CANCEL, viewport, mapped DS coordinates, and release stages.
- Debug action `DUMP_TOUCH_PIPELINE` emits the bounded JSON document through
  the debug log as `action=dump_touch_pipeline`.
- `:app:compileGitHubProdDebugKotlin -x :app:regenerateVulkanSpirv` passes.
- `:app:testGitHubProdReleaseUnitTest --tests
  me.magnum.melonds.ui.emulator.component.EmulatorOverlayTrackerTest
  -x :app:regenerateVulkanSpirv` passes.
- The normal Vulkan generation step remains blocked by the existing missing
  `melonDS-android-lib/src/GPU3D_Vulkan_InterpSpansShader.comp` in this
  worktree; this is not treated as product or touch acceptance evidence.

Hardware evidence on AYN Thor `6b0af897`:

- Display 0: `1080x1920`; presentation display 4: `1080x1240`.
- APK built and installed: `app-gitHub-prod-debug.apk`.
- Clean launch reached `EmulatorActivity` without an observed crash or ANR.
- A display-4 target at raw Android `(620,890)` arrived at local `(620,815)`.
- The trace recorded `displayId=4`, viewport `[0,0,1240,930]`, accepted
  `true`, and normalized DS `(128,168)`.
- Short tap, 50 ms DOWN/UP, 250 ms DOWN/UP, CANCEL, and a 250 ms zero-distance
  swipe all reached the handler and recorded release or cancel stages.
- This is ADB/input-pipeline evidence only. It does not claim ordinary physical
  finger success or semantic Adventure activation.
