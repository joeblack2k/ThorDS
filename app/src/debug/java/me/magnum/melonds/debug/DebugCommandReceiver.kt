package me.magnum.melonds.debug

import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.view.InputDevice
import android.view.MotionEvent
import androidx.core.content.edit
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.magnum.melonds.MelonDSAndroidInterface
import me.magnum.melonds.MelonEmulator
import me.magnum.melonds.common.ThorDeviceDefaults
import me.magnum.melonds.domain.model.ControllerConfiguration
import me.magnum.melonds.domain.model.Input
import me.magnum.melonds.domain.model.SaveStateSlot
import me.magnum.melonds.domain.model.VideoRenderer
import me.magnum.melonds.domain.model.enhancement.SharedPreferencesProfilePreferencesRepository
import me.magnum.melonds.impl.emulator.debug.RendererDebugCaptureKind
import me.magnum.melonds.impl.emulator.debug.RendererDebugCapturePresets
import me.magnum.melonds.impl.emulator.debug.RendererDebugCaptureLogger
import me.magnum.melonds.impl.emulator.debug.RendererDebugBridge
import me.magnum.melonds.impl.emulator.debug.RendererDebugCaptureResult
import me.magnum.melonds.impl.emulator.debug.RendererParityComparator
import me.magnum.melonds.ui.emulator.EmulatorActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.LinkedHashSet
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal class DebugCommandReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        receiverScope.launch {
            try {
                val success = DebugCommandExecutionLock.withLock {
                    handleIntent(context.applicationContext, intent)
                }
                pendingResult.setResultCode(if (success) RESULT_SUCCESS else RESULT_FAILURE)
                pendingResult.setResultData("success=${if (success) 1 else 0}")
            } catch (error: Exception) {
                Log.w(TAG, "Debug command failed: action=${intent.action}", error)
                pendingResult.setResultCode(RESULT_FAILURE)
                pendingResult.setResultData("success=0 error=${error.javaClass.simpleName}")
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun handleIntent(context: Context, intent: Intent): Boolean {
        val entryPoint = DebugCommandEntryPoint.resolve(context)
        return when (intent.action) {
            context.debugCommandAction(ACTION_SET_RENDERER_SUFFIX) -> { handleSetRenderer(entryPoint, intent); true }
            context.debugCommandAction(ACTION_SET_IR_SUFFIX) -> { handleSetInternalResolution(entryPoint, intent); true }
            context.debugCommandAction(ACTION_SET_JIT_SUFFIX) -> { handleSetJit(entryPoint, intent); true }
            context.debugCommandAction(ACTION_SET_ARM9_PERCENT_SUFFIX) -> { handleSetArm9Percent(context, intent); true }
            context.debugCommandAction(ACTION_SET_SM64DS_CADENCE_PROBE_SUFFIX) -> { handleSetSm64dsCadenceProbe(context, intent); true }
            context.debugCommandAction(ACTION_SET_SM64DS_POSE_INTERPOLATION_SUFFIX) -> { handleSetSm64dsPoseInterpolation(context, intent); true }
            context.debugCommandAction(ACTION_DUMP_ARM9_TELEMETRY_SUFFIX) -> { handleDumpArm9Telemetry(); true }
            context.debugCommandAction(ACTION_DUMP_SM64DS_GAME_LOOP_SUFFIX) -> { handleDumpSm64dsGameLoopTelemetry(); true }
            context.debugCommandAction(ACTION_SET_SM64DS_SEMANTIC_MONITOR_SUFFIX) -> { handleSetSm64dsSemanticMonitor(intent); true }
            context.debugCommandAction(ACTION_DUMP_SM64DS_SEMANTIC_TELEMETRY_SUFFIX) -> { handleDumpSm64dsSemanticTelemetry(); true }
            context.debugCommandAction(ACTION_SET_BGOBJ_LOG_SUFFIX) -> { handleSetBgObjLog(entryPoint, intent); true }
            context.debugCommandAction(ACTION_SET_LATCH_TRACE_SUFFIX) -> { handleSetLatchTrace(entryPoint, intent); true }
            context.debugCommandAction(ACTION_SET_FAST_FORWARD_SUFFIX) -> { handleSetFastForward(intent); true }
            context.debugCommandAction(ACTION_SET_THORDS_SAFE_MODE_SUFFIX) -> { handleSetThorDSSafeMode(entryPoint, intent); true }
            context.debugCommandAction(ACTION_SET_SLOT2_ANALOG_SUFFIX) -> { handleSetSlot2Analog(intent); true }
            context.debugCommandAction(ACTION_SET_SLOT2_ANALOG_MAPPING_SUFFIX) -> { handleSetSlot2AnalogMapping(entryPoint, intent); true }
            context.debugCommandAction(ACTION_RUN_ANALOG_SWEEP_SUFFIX) -> handleRunAnalogSweep(context, entryPoint, intent)
            context.debugCommandAction(ACTION_RUN_ANALOG_GAMEPLAY_TRIAL_SUFFIX) -> handleRunAnalogGameplayTrial(context, entryPoint, intent)
            context.debugCommandAction(ACTION_RUN_LIVE_CAMERA_TRIAL_SUFFIX) -> handleRunLiveCameraTrial(context, intent)
            context.debugCommandAction(ACTION_SET_VULKAN_FALLBACKS_SUFFIX) -> { handleSetVulkanFallbacks(intent); true }
            context.debugCommandAction(ACTION_TOUCH_SCREEN_SUFFIX) -> { handleTouchScreen(intent); true }
            context.debugCommandAction(ACTION_TAP_INPUT_SUFFIX) -> { handleTapInput(intent); true }
            context.debugCommandAction(ACTION_BACKFLIP_SUFFIX) -> { handleBackflip(intent); true }
            context.debugCommandAction(ACTION_LAUNCH_ROM_SUFFIX) -> handleLaunchRom(context, intent)
            context.debugCommandAction(ACTION_WAIT_ROM_READY_SUFFIX) -> handleWaitRomReady(intent)
            context.debugCommandAction(ACTION_SAVE_STATE_SUFFIX) -> handleSaveState(context, entryPoint, intent)
            context.debugCommandAction(ACTION_LOAD_STATE_SUFFIX) -> handleLoadState(context, entryPoint, intent)
            context.debugCommandAction(ACTION_STEP_FRAME_SUFFIX) -> handleStepFrame(entryPoint, intent)
            context.debugCommandAction(ACTION_STEP_FRAMES_SUFFIX) -> handleStepFrame(entryPoint, intent)
            context.debugCommandAction(ACTION_RUN_M7_PRESENTER_TRACE_SUFFIX) -> handleRunM7PresenterTrace(context, entryPoint, intent)
            context.debugCommandAction(ACTION_GRANT_M7_CASTLE_KEY_SUFFIX) -> handleGrantM7CastleKey(context)
            context.debugCommandAction(ACTION_RUN_M7_SURFACE_SEQUENCE_SUFFIX) -> handleRunM7SurfaceSequence(context, entryPoint, intent)
            context.debugCommandAction(ACTION_DUMP_RENDERER_CAPTURE_SUFFIX) -> handleDumpRendererCapture(context, entryPoint, intent)
            else -> {
                Log.w(TAG, "Ignored unknown action=${intent.action}")
                false
            }
        }
    }

    private fun handleSetRenderer(entryPoint: DebugCommandEntryPoint, intent: Intent) {
        val rendererName = intent.firstStringExtra(EXTRA_RENDERER, EXTRA_VALUE)
            ?: throw IllegalArgumentException("Missing renderer extra")
        val renderer = parseRenderer(rendererName)
            ?: throw IllegalArgumentException("Unsupported renderer=$rendererName")
        entryPoint.sharedPreferences().edit(commit = true) {
            putString(KEY_VIDEO_RENDERER, renderer.name.lowercase(Locale.US))
        }
        val refreshed = DebugCommandStateStore.requestSettingsRefresh()
        Log.w(TAG, "action=set_renderer renderer=${renderer.name.lowercase(Locale.US)} refreshed=${if (refreshed) 1 else 0}")
    }

    private fun handleSetInternalResolution(entryPoint: DebugCommandEntryPoint, intent: Intent) {
        val scale = intent.firstIntExtra(EXTRA_SCALE, EXTRA_IR, EXTRA_VALUE)
        require(scale in 1..8) { "Unsupported internal resolution=$scale" }
        entryPoint.sharedPreferences().edit(commit = true) {
            putString(KEY_VIDEO_INTERNAL_RESOLUTION, scale.toString())
        }
        val refreshed = DebugCommandStateStore.requestSettingsRefresh()
        Log.w(TAG, "action=set_ir scale=$scale refreshed=${if (refreshed) 1 else 0}")
    }

    private fun handleSetJit(entryPoint: DebugCommandEntryPoint, intent: Intent) {
        val enabled = intent.firstBooleanExtra(EXTRA_ENABLED, EXTRA_VALUE)
            ?: throw IllegalArgumentException("Missing enabled extra")
        entryPoint.sharedPreferences().edit(commit = true) {
            putBoolean(KEY_ENABLE_JIT, enabled)
        }
        val refreshed = DebugCommandStateStore.requestSettingsRefresh()
        Log.w(TAG, "action=set_jit enabled=${if (enabled) 1 else 0} refreshed=${if (refreshed) 1 else 0}")
    }

    private fun handleSetArm9Percent(context: Context, intent: Intent) {
        val romKey = intent.firstStringExtra(EXTRA_ROM_KEY)
            ?: throw IllegalArgumentException("Missing rom_key extra")
        val percent = intent.firstNullableIntExtra(EXTRA_PERCENT, EXTRA_VALUE)
            ?: throw IllegalArgumentException("Missing percent extra")
        require(percent in setOf(100, 125, 150, 175, 200)) {
            "Unsupported ARM9 percent=$percent"
        }
        val repository = SharedPreferencesProfilePreferencesRepository(context)
        val current = repository.read(romKey)
        repository.write(romKey, current.copy(requestedArm9Percent = percent))
        val relaunched = DebugCommandStateStore.requestCurrentRomRelaunch()
        Log.w(
            TAG,
            "action=set_arm9_percent romKey=$romKey percent=$percent relaunched=${if (relaunched) 1 else 0}",
        )
    }

    private suspend fun handleSetSm64dsCadenceProbe(context: Context, intent: Intent) {
        val romKey = intent.firstStringExtra(EXTRA_ROM_KEY)
            ?: throw IllegalArgumentException("Missing rom_key extra")
        require(romKey == SM64DS_EU_ROM_KEY) { "Cadence probe requires the exact EU SM64DS identity" }
        val enabled = intent.firstBooleanExtra(EXTRA_ENABLED, EXTRA_VALUE)
            ?: throw IllegalArgumentException("Missing enabled extra")
        val repository = SharedPreferencesProfilePreferencesRepository(context)
        val current = repository.read(romKey)
        repository.write(
            romKey,
            current.copy(enabledEnhancements = current.enabledEnhancements + ("60fps-dev-cadence" to enabled)),
        )
        val relaunched = DebugCommandStateStore.requestCurrentRomRelaunch()
        Log.w(
            TAG,
            "action=set_sm64ds_cadence_probe enabled=${if (enabled) 1 else 0} relaunched=${if (relaunched) 1 else 0}",
        )
    }

    private fun handleSetSm64dsPoseInterpolation(context: Context, intent: Intent) {
        val romKey = intent.firstStringExtra(EXTRA_ROM_KEY)
            ?: throw IllegalArgumentException("Missing rom_key extra")
        require(romKey == SM64DS_EU_ROM_KEY) { "Pose interpolation requires the exact EU SM64DS identity" }
        val enabled = intent.firstBooleanExtra(EXTRA_ENABLED, EXTRA_VALUE)
            ?: throw IllegalArgumentException("Missing enabled extra")
        val repository = SharedPreferencesProfilePreferencesRepository(context)
        val current = repository.read(romKey)
        repository.write(
            romKey,
            current.copy(enabledEnhancements = current.enabledEnhancements + ("z-player-pose-interpolation" to enabled)),
        )
        val relaunched = DebugCommandStateStore.requestCurrentRomRelaunch()
        Log.w(
            TAG,
            "action=set_sm64ds_pose_interpolation enabled=${if (enabled) 1 else 0} relaunched=${if (relaunched) 1 else 0}",
        )
    }

    private fun handleDumpArm9Telemetry() {
        Log.w(TAG, "action=dump_arm9_telemetry value=${MelonEmulator.getArm9OverclockTelemetry()}")
    }

    private fun handleDumpSm64dsGameLoopTelemetry() {
        Log.w(TAG, "action=dump_sm64ds_game_loop value=${MelonEmulator.getSm64dsGameLoopTelemetry()}")
    }

    private fun handleSetBgObjLog(entryPoint: DebugCommandEntryPoint, intent: Intent) {
        val enabled = intent.firstBooleanExtra(EXTRA_ENABLED, EXTRA_VALUE)
            ?: throw IllegalArgumentException("Missing enabled extra")
        entryPoint.sharedPreferences().edit(commit = true) {
            putBoolean(KEY_RENDERER_DEBUG_BGOBJ_ENABLED, enabled)
        }
        val refreshed = DebugCommandStateStore.requestSettingsRefresh()
        Log.w(TAG, "action=set_bgobj_log enabled=${if (enabled) 1 else 0} refreshed=${if (refreshed) 1 else 0}")
    }

    private fun handleSetLatchTrace(entryPoint: DebugCommandEntryPoint, intent: Intent) {
        val enabled = intent.firstBooleanExtra(EXTRA_ENABLED, EXTRA_VALUE)
            ?: throw IllegalArgumentException("Missing enabled extra")
        entryPoint.sharedPreferences().edit(commit = true) {
            putBoolean(KEY_RENDERER_DEBUG_LATCH_TRACE_ENABLED, enabled)
        }
        val refreshed = DebugCommandStateStore.requestSettingsRefresh()
        Log.w(TAG, "action=set_latch_trace enabled=${if (enabled) 1 else 0} refreshed=${if (refreshed) 1 else 0}")
    }

    private fun handleSetFastForward(intent: Intent) {
        val enabled = intent.firstBooleanExtra(EXTRA_ENABLED, EXTRA_VALUE)
            ?: throw IllegalArgumentException("Missing enabled extra")
        MelonEmulator.setFastForwardEnabled(enabled)
        Log.w(TAG, "action=set_fast_forward enabled=${if (enabled) 1 else 0}")
    }

    private fun handleSetThorDSSafeMode(entryPoint: DebugCommandEntryPoint, intent: Intent) {
        val enabled = intent.firstBooleanExtra(EXTRA_ENABLED, EXTRA_VALUE)
            ?: throw IllegalArgumentException("Missing enabled extra")
        entryPoint.sharedPreferences().edit(commit = true) {
            putBoolean(ThorDeviceDefaults.SAFE_MODE_KEY, enabled)
        }
        Log.w(TAG, "action=set_thords_safe_mode enabled=${if (enabled) 1 else 0}")
    }

    private fun handleSetSlot2Analog(intent: Intent) {
        val x = intent.firstFloatExtra(EXTRA_X, EXTRA_VALUE_X, EXTRA_VALUE)
            ?: throw IllegalArgumentException("Missing x/value extra")
        val y = intent.firstFloatExtra(EXTRA_Y, EXTRA_VALUE_Y, EXTRA_VALUE)
            ?: throw IllegalArgumentException("Missing y/value extra")
        val clampedX = x.coerceIn(-1f, 1f)
        val clampedY = y.coerceIn(-1f, 1f)
        MelonEmulator.setSlot2AnalogInput(clampedX, clampedY)
        Log.w(TAG, "action=set_slot2_analog x=$clampedX y=$clampedY")
    }

    private fun handleSetSm64dsSemanticMonitor(intent: Intent) {
        val enabled = intent.firstBooleanExtra(EXTRA_ENABLED, EXTRA_VALUE)
            ?: throw IllegalArgumentException("Missing enabled extra")
        MelonEmulator.setSm64dsSemanticMonitorEnabled(enabled)
        Log.w(TAG, "action=set_sm64ds_semantic_monitor enabled=${if (enabled) 1 else 0}")
    }

    private fun handleDumpSm64dsSemanticTelemetry() {
        Log.w(TAG, "action=dump_sm64ds_semantic_telemetry json=${MelonEmulator.getSm64dsSemanticTelemetry()}")
    }

    private fun handleSetSlot2AnalogMapping(entryPoint: DebugCommandEntryPoint, intent: Intent) {
        val settingsRepository = entryPoint.settingsRepository()
        val currentConfiguration = settingsRepository.getControllerConfiguration()
        val currentMapping = currentConfiguration.slot2AnalogMapping

        val nextMapping = currentMapping.copy(
            axisXCode = intent.firstNullableIntExtra(EXTRA_AXIS_X, EXTRA_AXIS, EXTRA_X) ?: currentMapping.axisXCode,
            axisYCode = intent.firstNullableIntExtra(EXTRA_AXIS_Y, EXTRA_AXIS, EXTRA_Y) ?: currentMapping.axisYCode,
            invertX = intent.firstBooleanExtra(EXTRA_INVERT_X) ?: currentMapping.invertX,
            invertY = intent.firstBooleanExtra(EXTRA_INVERT_Y) ?: currentMapping.invertY,
            deadzone = (intent.firstFloatExtra(EXTRA_DEADZONE) ?: currentMapping.deadzone).coerceIn(0f, 1f),
            deviceId = if (intent.hasExtra(EXTRA_DEVICE_ID)) {
                intent.firstNullableIntExtra(EXTRA_DEVICE_ID)?.takeIf { it >= 0 }
            } else {
                currentMapping.deviceId
            },
        )

        val updatedConfiguration = ControllerConfiguration(
            configList = currentConfiguration.inputMapper.map { it.copy() },
            slot2AnalogMapping = nextMapping,
        )
        settingsRepository.setControllerConfiguration(updatedConfiguration)
        val refreshed = DebugCommandStateStore.requestSettingsRefresh()
        Log.w(
            TAG,
            "action=set_slot2_analog_mapping axisX=${nextMapping.axisXCode} axisY=${nextMapping.axisYCode} invertX=${if (nextMapping.invertX) 1 else 0} invertY=${if (nextMapping.invertY) 1 else 0} deadzone=${"%.3f".format(Locale.US, nextMapping.deadzone)} deviceId=${nextMapping.deviceId ?: -1} refreshed=${if (refreshed) 1 else 0}",
        )
    }

    private suspend fun handleRunAnalogSweep(
        context: Context,
        entryPoint: DebugCommandEntryPoint,
        intent: Intent,
    ): Boolean {
        if (!DebugCommandStateStore.isRunningRom()) {
            Log.w(TAG, "action=run_analog_sweep ready=0")
            return false
        }

        val timeoutMs = intent.firstNullableIntExtra(EXTRA_TIMEOUT_MS)
            ?.coerceIn(250, MAX_RECEIVER_WAIT_TIMEOUT_MS)
            ?: DEFAULT_ANALOG_STEP_TIMEOUT_MS
        val mapping = entryPoint.settingsRepository().getControllerConfiguration().slot2AnalogMapping
        val samples = JSONArray()
        var passed = true

        MelonEmulator.pauseEmulation()
        DebugCommandStateStore.setDebugPauseHeld(true)

        for (direction in 0 until ANALOG_SWEEP_DIRECTIONS) {
            val angle = direction * 2.0 * PI / ANALOG_SWEEP_DIRECTIONS
            for (magnitude in ANALOG_SWEEP_MAGNITUDES) {
                val rawX = (cos(angle) * magnitude).toFloat()
                val rawY = (sin(angle) * magnitude).toFloat()
                val handled = dispatchControllerMotion(rawX, rawY)
                val step = stepRendererFrames(entryPoint, 1, timeoutMs)
                val (processedX, processedY) = mapping.processRadial(rawX, rawY)
                samples.put(
                    JSONObject()
                        .put("direction", direction)
                        .put("magnitude", magnitude)
                        .put("rawX", rawX)
                        .put("rawY", rawY)
                        .put("processedX", processedX)
                        .put("processedY", processedY)
                        .put("handled", handled)
                        .put("startFrame", step.startFrame)
                        .put("endFrame", step.endFrame)
                        .put("frameReady", step.ready)
                        .put("frameAdvanced", step.advanced),
                )
                passed = passed && handled && step.ready && step.advanced
            }
        }

        val deadzoneSamples = JSONArray()
        val deadzone = mapping.normalizedDeadzone()
        for (rawX in floatArrayOf(deadzone * 0.5f, deadzone, (deadzone + 0.01f).coerceAtMost(1f))) {
            val handled = dispatchControllerMotion(rawX, 0f)
            val step = stepRendererFrames(entryPoint, 1, timeoutMs)
            val (processedX, processedY) = mapping.processRadial(rawX, 0f)
            deadzoneSamples.put(
                JSONObject()
                    .put("rawX", rawX)
                    .put("processedX", processedX)
                    .put("processedY", processedY)
                    .put("handled", handled)
                    .put("startFrame", step.startFrame)
                    .put("endFrame", step.endFrame)
                    .put("frameReady", step.ready)
                    .put("frameAdvanced", step.advanced),
            )
            passed = passed && handled && step.ready && step.advanced
        }

        val cameraSteps = JSONArray()
        suspend fun cameraStep(
            name: String,
            cameraX: Float,
            hatX: Float = 0f,
        ): Boolean {
            val handled = dispatchControllerMotion(cameraX = cameraX, hatX = hatX)
            val step = stepRendererFrames(entryPoint, 1, timeoutMs)
            cameraSteps.put(
                JSONObject()
                    .put("name", name)
                    .put("handled", handled)
                    .put("startFrame", step.startFrame)
                    .put("endFrame", step.endFrame)
                    .put("frameReady", step.ready)
                    .put("frameAdvanced", step.advanced),
            )
            return handled && step.ready && step.advanced
        }

        passed = cameraStep("camera_left_press", cameraX = -0.8f) && passed
        val dpadDownHandled = cameraStep("camera_left_with_dpad", cameraX = -0.8f, hatX = -1f)
        passed = dpadDownHandled && passed
        passed = cameraStep("camera_neutral_dpad_held", cameraX = 0f, hatX = -1f) && passed
        val dpadUpHandled = cameraStep("all_neutral", cameraX = 0f)
        passed = dpadUpHandled && passed

        val refreshed = DebugCommandStateStore.requestSettingsRefresh()
        if (refreshed) {
            delay(250L)
        }
        val recreatedHandled = dispatchControllerMotion(0f, 0f)
        val recreatedStep = stepRendererFrames(entryPoint, 1, timeoutMs)
        passed = passed && refreshed && recreatedHandled && recreatedStep.ready && recreatedStep.advanced

        val output = JSONObject()
            .put("schemaVersion", 1)
            .put("directions", ANALOG_SWEEP_DIRECTIONS)
            .put("magnitudes", JSONArray(ANALOG_SWEEP_MAGNITUDES.toList()))
            .put("deadzone", deadzone)
            .put("samples", samples)
            .put("deadzoneSamples", deadzoneSamples)
            .put("cameraSteps", cameraSteps)
            .put("dpadDownHandled", dpadDownHandled)
            .put("dpadUpHandled", dpadUpHandled)
            .put("pipelineRefreshRequested", refreshed)
            .put("pipelineNeutralHandled", recreatedHandled)
            .put("pipelineFrameReady", recreatedStep.ready)
            .put("pipelineFrameAdvanced", recreatedStep.advanced)
            .put("result", if (passed) "PASS" else "PARTIAL")

        val outputFile = File(context.cacheDir, ANALOG_SWEEP_OUTPUT_FILE)
        outputFile.writeText(output.toString(2))
        Log.w(
            TAG,
            "action=run_analog_sweep samples=${samples.length()} deadzoneSamples=${deadzoneSamples.length()} refreshed=${if (refreshed) 1 else 0} result=${if (passed) "PASS" else "PARTIAL"}",
        )
        return passed
    }

    private suspend fun dispatchControllerMotion(
        leftX: Float = 0f,
        leftY: Float = 0f,
        cameraX: Float = 0f,
        cameraY: Float = 0f,
        hatX: Float = 0f,
        hatY: Float = 0f,
    ): Boolean {
        val event = createControllerMotionEvent(leftX, leftY, cameraX, cameraY, hatX, hatY)
        return try {
            DebugCommandStateStore.dispatchGenericMotionEvent(event)
        } finally {
            event.recycle()
        }
    }

    private suspend fun handleRunAnalogGameplayTrial(
        context: Context,
        entryPoint: DebugCommandEntryPoint,
        intent: Intent,
    ): Boolean {
        if (!DebugCommandStateStore.isRunningRom()) {
            Log.w(TAG, "action=run_analog_gameplay_trial ready=0 result=PARTIAL")
            return false
        }

        val rawX = (intent.firstFloatExtra(EXTRA_X, EXTRA_VALUE_X) ?: 0f).coerceIn(-1f, 1f)
        val rawY = (intent.firstFloatExtra(EXTRA_Y, EXTRA_VALUE_Y) ?: 0f).coerceIn(-1f, 1f)
        val cameraX = (intent.firstFloatExtra(EXTRA_CAMERA_X) ?: 0f).coerceIn(-1f, 1f)
        val cameraY = (intent.firstFloatExtra(EXTRA_CAMERA_Y) ?: 0f).coerceIn(-1f, 1f)
        val frames = (intent.firstNullableIntExtra(EXTRA_FRAMES, EXTRA_STEP_FRAMES) ?: 30).coerceIn(1, 600)
        val timeoutMs = (intent.firstNullableIntExtra(EXTRA_TIMEOUT_MS) ?: 8_000)
            .coerceIn(250, MAX_RECEIVER_WAIT_TIMEOUT_MS)
        val mapping = entryPoint.settingsRepository().getControllerConfiguration().slot2AnalogMapping

        MelonEmulator.pauseEmulation()
        DebugCommandStateStore.setDebugPauseHeld(true)
        val neutralBeforeHandled = dispatchControllerMotion()
        val warmup = stepRendererFrames(entryPoint, 1, timeoutMs)
        val before = RendererDebugBridge.captureCurrentFrame()
        val inputHandled = dispatchControllerMotion(rawX, rawY, cameraX, cameraY)
        val motion = stepRendererFrames(entryPoint, frames, timeoutMs)
        val neutralAfterHandled = dispatchControllerMotion()
        val after = RendererDebugBridge.captureCurrentFrame()

        val expectedPixels = RendererDebugBridge.CAPTURE_WIDTH * RendererDebugBridge.CAPTURE_HEIGHT
        val frameShapeValid = before?.size == expectedPixels && after?.size == expectedPixels
        val topPixels = RendererDebugBridge.CAPTURE_WIDTH * (RendererDebugBridge.CAPTURE_HEIGHT / 2)
        val topReport = if (frameShapeValid) {
            RendererParityComparator.compareFrames(
                before.copyOfRange(0, topPixels),
                after.copyOfRange(0, topPixels),
            )
        } else {
            null
        }
        val bottomReport = if (frameShapeValid) {
            RendererParityComparator.compareFrames(
                before.copyOfRange(topPixels, expectedPixels),
                after.copyOfRange(topPixels, expectedPixels),
            )
        } else {
            null
        }
        val passed = neutralBeforeHandled &&
            inputHandled &&
            neutralAfterHandled &&
            warmup.ready &&
            warmup.advanced &&
            motion.ready &&
            motion.advanced &&
            frameShapeValid
        val (processedX, processedY) = mapping.processRadial(rawX, rawY)

        val output = JSONObject()
            .put("schemaVersion", 1)
            .put("rawX", rawX)
            .put("rawY", rawY)
            .put("cameraX", cameraX)
            .put("cameraY", cameraY)
            .put("processedX", processedX)
            .put("processedY", processedY)
            .put("framesRequested", frames)
            .put("startFrame", motion.startFrame)
            .put("endFrame", motion.endFrame)
            .put("inputHandled", inputHandled)
            .put("neutralBeforeHandled", neutralBeforeHandled)
            .put("neutralAfterHandled", neutralAfterHandled)
            .put("frameReady", motion.ready)
            .put("frameAdvanced", motion.advanced)
            .put("frameShapeValid", frameShapeValid)
            .put("topChangedPixels", topReport?.mismatchedPixels ?: -1)
            .put("topMeanChannelDelta", topReport?.meanChannelDelta ?: -1.0)
            .put("bottomChangedPixels", bottomReport?.mismatchedPixels ?: -1)
            .put("bottomMeanChannelDelta", bottomReport?.meanChannelDelta ?: -1.0)
            .put("beforeFrameHash", before?.contentHashCode() ?: 0)
            .put("afterFrameHash", after?.contentHashCode() ?: 0)
            .put("responseObserved", (topReport?.mismatchedPixels ?: 0) > 0)
            .put("result", if (passed) "PASS" else "PARTIAL")
        File(context.cacheDir, ANALOG_GAMEPLAY_TRIAL_OUTPUT_FILE).writeText(output.toString(2))
        Log.w(
            TAG,
            "action=run_analog_gameplay_trial frames=$frames changedTop=${topReport?.mismatchedPixels ?: -1} result=${if (passed) "PASS" else "PARTIAL"}",
        )
        return passed
    }

    private suspend fun handleRunLiveCameraTrial(
        context: Context,
        intent: Intent,
    ): Boolean {
        if (!DebugCommandStateStore.isRunningRom()) {
            Log.w(TAG, "action=run_live_camera_trial ready=0 result=PARTIAL")
            return false
        }

        val cameraX = (intent.firstFloatExtra(EXTRA_CAMERA_X) ?: 0f).coerceIn(-1f, 1f)
        val cameraY = (intent.firstFloatExtra(EXTRA_CAMERA_Y) ?: 0f).coerceIn(-1f, 1f)
        val holdMs = (intent.firstNullableIntExtra(EXTRA_DURATION_MS) ?: 1_000)
            .coerceIn(100, 5_000)
        val before = RendererDebugBridge.captureCurrentFrame()
        val cameraStateBefore = MelonEmulator.getSlot2CameraStateTelemetry()
        val gameLoopBefore = MelonEmulator.getSm64dsGameLoopTelemetry()
        val inputHandled = dispatchControllerMotion(cameraX = cameraX, cameraY = cameraY)
        delay(holdMs.toLong())
        val after = RendererDebugBridge.captureCurrentFrame()
        val cameraStateDuring = MelonEmulator.getSlot2CameraStateTelemetry()
        val gameLoopDuring = MelonEmulator.getSm64dsGameLoopTelemetry()
        val neutralHandled = dispatchControllerMotion()
        delay(CAMERA_NEUTRAL_SETTLE_MS)
        val cameraStateAfter = MelonEmulator.getSlot2CameraStateTelemetry()
        val gameLoopAfter = MelonEmulator.getSm64dsGameLoopTelemetry()

        val expectedPixels = RendererDebugBridge.CAPTURE_WIDTH * RendererDebugBridge.CAPTURE_HEIGHT
        val frameShapeValid = before?.size == expectedPixels && after?.size == expectedPixels
        val changedPixels = if (frameShapeValid) {
            before!!.indices.count { before[it] != after!![it] }
        } else {
            -1
        }
        val output = JSONObject()
            .put("schemaVersion", 1)
            .put("cameraX", cameraX)
            .put("cameraY", cameraY)
            .put("holdMs", holdMs)
            .put("inputHandled", inputHandled)
            .put("neutralHandled", neutralHandled)
            .put("cameraStateBefore", cameraStateBefore)
            .put("cameraStateDuring", cameraStateDuring)
            .put("cameraStateAfter", cameraStateAfter)
            .put("gameLoopBefore", JSONObject(gameLoopBefore))
            .put("gameLoopDuring", JSONObject(gameLoopDuring))
            .put("gameLoopAfter", JSONObject(gameLoopAfter))
            .put("frameShapeValid", frameShapeValid)
            .put("changedPixels", changedPixels)
            .put("beforeFrameHash", before?.contentHashCode() ?: 0)
            .put("afterFrameHash", after?.contentHashCode() ?: 0)
            .put("result", if (inputHandled && neutralHandled && frameShapeValid) "PASS" else "PARTIAL")
        File(context.cacheDir, LIVE_CAMERA_TRIAL_OUTPUT_FILE).writeText(output.toString(2))
        Log.w(
            TAG,
            "action=run_live_camera_trial cameraX=$cameraX cameraY=$cameraY holdMs=$holdMs changedPixels=$changedPixels result=${if (inputHandled && neutralHandled && frameShapeValid) "PASS" else "PARTIAL"}",
        )
        return inputHandled && neutralHandled && frameShapeValid
    }

    private fun createControllerMotionEvent(
        leftX: Float,
        leftY: Float,
        cameraX: Float,
        cameraY: Float,
        hatX: Float,
        hatY: Float,
    ): MotionEvent {
        val now = SystemClock.uptimeMillis()
        val pointerProperties = MotionEvent.PointerProperties().apply {
            id = 0
            toolType = MotionEvent.TOOL_TYPE_UNKNOWN
        }
        val pointerCoords = MotionEvent.PointerCoords().apply {
            setAxisValue(MotionEvent.AXIS_X, leftX.coerceIn(-1f, 1f))
            setAxisValue(MotionEvent.AXIS_Y, leftY.coerceIn(-1f, 1f))
            setAxisValue(MotionEvent.AXIS_Z, cameraX.coerceIn(-1f, 1f))
            setAxisValue(MotionEvent.AXIS_RZ, cameraY.coerceIn(-1f, 1f))
            setAxisValue(MotionEvent.AXIS_HAT_X, hatX.coerceIn(-1f, 1f))
            setAxisValue(MotionEvent.AXIS_HAT_Y, hatY.coerceIn(-1f, 1f))
        }
        return MotionEvent.obtain(
            now,
            now,
            MotionEvent.ACTION_MOVE,
            1,
            arrayOf(pointerProperties),
            arrayOf(pointerCoords),
            0,
            0,
            1f,
            1f,
            0,
            0,
            InputDevice.SOURCE_JOYSTICK,
            0,
        )
    }

    private fun handleSetVulkanFallbacks(intent: Intent) {
        val forceTimelineOff = intent.firstBooleanExtra(EXTRA_TIMELINE_OFF)
            ?: intent.firstBooleanExtra(EXTRA_TIMELINE)?.not()
            ?: false
        val forceDynamicIndexingOff = intent.firstBooleanExtra(EXTRA_DYNAMIC_INDEXING_OFF)
            ?: intent.firstBooleanExtra(EXTRA_DYNAMIC_INDEXING)?.not()
            ?: false
        MelonDSAndroidInterface.setVulkanCompatibilityOverrides(
            disableTimelineSemaphores = forceTimelineOff,
            disableDynamicTextureIndexing = forceDynamicIndexingOff,
        )
        val refreshed = DebugCommandStateStore.requestSettingsRefresh()
        Log.w(
            TAG,
            "action=set_vulkan_fallbacks timelineOff=${if (forceTimelineOff) 1 else 0} dynamicIndexingOff=${if (forceDynamicIndexingOff) 1 else 0} refreshed=${if (refreshed) 1 else 0}",
        )
    }

    private suspend fun handleTouchScreen(intent: Intent) {
        val x = intent.firstNullableIntExtra(EXTRA_X, EXTRA_VALUE_X, EXTRA_VALUE)
            ?.coerceIn(0, 255)
            ?: DEFAULT_TOUCH_X
        val y = intent.firstNullableIntExtra(EXTRA_Y, EXTRA_VALUE_Y)
            ?.coerceIn(0, 191)
            ?: DEFAULT_TOUCH_Y
        val durationMs = (intent.firstNullableIntExtra(EXTRA_DURATION_MS) ?: DEFAULT_TOUCH_DURATION_MS)
            .coerceIn(1, 2_000)
        MelonEmulator.onInputDown(Input.TOUCHSCREEN)
        MelonEmulator.onScreenTouch(x, y)
        try {
            delay(durationMs.toLong())
        } finally {
            MelonEmulator.onInputUp(Input.TOUCHSCREEN)
            MelonEmulator.onScreenRelease()
        }
        Log.w(TAG, "action=touch_screen x=$x y=$y durationMs=$durationMs")
    }

    private suspend fun handleTapInput(intent: Intent) {
        val rawInput = intent.firstStringExtra(EXTRA_INPUT, EXTRA_VALUE)
            ?: throw IllegalArgumentException("Missing input extra")
        val input = Input.SYSTEM_BUTTONS.firstOrNull { it.name.equals(rawInput, ignoreCase = true) }
            ?: throw IllegalArgumentException("Unsupported system input=$rawInput")
        val durationMs = (intent.firstNullableIntExtra(EXTRA_DURATION_MS) ?: DEFAULT_INPUT_DURATION_MS)
            .coerceIn(1, 2_000)
        val pauseAfter = intent.getBooleanExtra(EXTRA_PAUSE_AFTER, false)
        MelonEmulator.onInputDown(input)
        delay(durationMs.toLong())
        if (pauseAfter) {
            DebugCommandStateStore.setDebugPauseHeld(true)
            MelonEmulator.pauseEmulation()
        }
        MelonEmulator.onInputUp(input)
        Log.w(
            TAG,
            "action=tap_input input=${input.name.lowercase(Locale.US)} durationMs=$durationMs pauseAfter=${if (pauseAfter) 1 else 0}",
        )
    }

    private suspend fun handleBackflip(intent: Intent) {
        val durationMs = (intent.firstNullableIntExtra(EXTRA_DURATION_MS) ?: 4_000)
            .coerceIn(500, 10_000)
        try {
            MelonEmulator.onInputDown(Input.R)
            delay(1_000)
            MelonEmulator.onInputDown(Input.B)
            delay(150)
            MelonEmulator.onInputUp(Input.B)
            delay(durationMs.toLong())
        } finally {
            MelonEmulator.onInputUp(Input.B)
            MelonEmulator.onInputUp(Input.R)
            MelonEmulator.setSlot2AnalogInput(0f, 0f)
        }
        Log.w(TAG, "action=backflip durationMs=$durationMs")
    }

    private suspend fun handleLaunchRom(context: Context, intent: Intent): Boolean {
        val romUri = intent.data ?: intent.firstStringExtra(EXTRA_ROM_URI, EXTRA_URI, EXTRA_PATH)?.let { Uri.parse(it) }
            ?: throw IllegalArgumentException("Missing ROM URI. Provide intent data or rom_uri.")
        if (
            DebugCommandStateStore.isRunningRom() &&
            DebugCommandStateStore.getLastRomUri(context) == romUri
        ) {
            Log.w(TAG, "action=launch_rom reused_current_rom uri=$romUri")
            return true
        }
        val launchUri = getLaunchUri(context, romUri)
        val waitReady = intent.firstBooleanExtra(EXTRA_WAIT_ROM_READY, EXTRA_WAIT_READY)
            ?: false
        val pauseAfterReady = intent.getBooleanExtra(EXTRA_PAUSE_AFTER, false)
        val widescreenProbe = intent.getBooleanExtra(EXTRA_WIDESCREEN_PROBE, false)
        val vulkanRotate180 = intent.getBooleanExtra(EXTRA_VULKAN_ROTATE_180, false)
        val requestedTimeoutMs = intent.firstNullableIntExtra(EXTRA_WAIT_TIMEOUT_MS, EXTRA_TIMEOUT_MS)
            ?.coerceAtLeast(1)
            ?: DEFAULT_ROM_READY_TIMEOUT_MS

        if (waitReady) {
            DebugCommandStateStore.requestPauseAfterNextRunningRom(pauseAfterReady)
        }

        startEmulatorActivityFromDebugCommand(
            context = context,
            launchIntent = Intent(context, EmulatorActivity::class.java).apply {
                action = context.debugCommandAction(ACTION_LAUNCH_ROM_SUFFIX)
                data = launchUri
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                putExtra(EmulatorActivity.EXTRA_DEVELOPER_WIDESCREEN_PROBE, widescreenProbe)
                putExtra(EmulatorActivity.EXTRA_DEVELOPER_VULKAN_ROTATE_180, vulkanRotate180)
            },
        )

        delay(LAUNCH_ACTIVITY_SEEN_TIMEOUT_MS)
        val activitySeen = DebugCommandStateStore.hasEmulatorActivity()
        val ready = DebugCommandStateStore.isRunningRom()
        if (ready && waitReady) {
            applyPauseAfterReady(pauseAfterReady)
        }
        Log.w(
            TAG,
            "action=launch_rom uri=$romUri waitReady=${if (waitReady) 1 else 0} activitySeen=${if (activitySeen) 1 else 0} ready=${if (ready) 1 else 0} pauseAfter=${if (pauseAfterReady) 1 else 0} widescreenProbe=${if (widescreenProbe) 1 else 0} vulkanRotate180=${if (vulkanRotate180) 1 else 0} requestedTimeoutMs=$requestedTimeoutMs deferredReady=1",
        )
        return activitySeen
    }

    private fun getLaunchUri(context: Context, romUri: Uri): Uri {
        if (romUri.scheme != "file") return romUri
        val file = romUri.path?.let(::File) ?: return romUri
        val sharedDirectory = File(context.cacheDir, "shared_saves").canonicalFile
        if (file.canonicalFile.parentFile != sharedDirectory) return romUri
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    private fun startEmulatorActivityFromDebugCommand(context: Context, launchIntent: Intent) {
        val options = ActivityOptions.makeBasic()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val mode = if (Build.VERSION.SDK_INT >= 36) {
                ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOW_ALWAYS
            } else {
                @Suppress("DEPRECATION")
                ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
            }
            options.setPendingIntentBackgroundActivityStartMode(mode)
            options.setPendingIntentCreatorBackgroundActivityStartMode(mode)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_LAUNCH_ROM,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        pendingIntent.send(
            context,
            0,
            null,
            null,
            null,
            null,
            options.toBundle(),
        )
    }

    private suspend fun handleWaitRomReady(intent: Intent): Boolean {
        val pauseAfterReady = intent.getBooleanExtra(EXTRA_PAUSE_AFTER, false)
        val requestedTimeoutMs = intent.firstNullableIntExtra(EXTRA_WAIT_TIMEOUT_MS, EXTRA_TIMEOUT_MS)
            ?.coerceAtLeast(1)
            ?: DEFAULT_ROM_READY_TIMEOUT_MS
        val timeoutMs = requestedTimeoutMs.coerceAtMost(MAX_RECEIVER_WAIT_TIMEOUT_MS)
        val ready = DebugCommandStateStore.waitForRunningRom(timeoutMs.toLong())
        if (ready) {
            applyPauseAfterReady(pauseAfterReady)
        }
        Log.w(
            TAG,
            "action=wait_rom_ready ready=${if (ready) 1 else 0} pauseAfter=${if (pauseAfterReady) 1 else 0} timeoutMs=$timeoutMs requestedTimeoutMs=$requestedTimeoutMs",
        )
        return ready
    }

    private suspend fun handleLoadState(
        context: Context,
        entryPoint: DebugCommandEntryPoint,
        intent: Intent,
    ): Boolean {
        val waitReady = intent.firstBooleanExtra(EXTRA_WAIT_ROM_READY, EXTRA_WAIT_READY)
            ?: true
        val requestedTimeoutMs = intent.firstNullableIntExtra(EXTRA_WAIT_TIMEOUT_MS, EXTRA_TIMEOUT_MS)
            ?.coerceAtLeast(1)
            ?: DEFAULT_ROM_READY_TIMEOUT_MS
        val timeoutMs = requestedTimeoutMs.coerceAtMost(MAX_RECEIVER_WAIT_TIMEOUT_MS)
        val pauseAfterLoad = intent.getBooleanExtra(EXTRA_PAUSE_AFTER, false)
        if (waitReady) {
            val ready = DebugCommandStateStore.waitForRunningRom(timeoutMs.toLong())
            if (!ready) {
                Log.w(
                    TAG,
                    "action=load_state waitReady=1 ready=0 success=0 pauseAfter=${if (pauseAfterLoad) 1 else 0} timeoutMs=$timeoutMs requestedTimeoutMs=$requestedTimeoutMs",
                )
                return false
            }
        }
        val stateUri = resolveStateUri(context, entryPoint, intent, preferExistingSlotFallback = true)
            ?: throw IllegalArgumentException("Missing load target. Provide slot or path.")
        MelonEmulator.pauseEmulation()
        val success = try {
            MelonEmulator.loadState(stateUri)
        } finally {
            if (pauseAfterLoad) {
                DebugCommandStateStore.setDebugPauseHeld(true)
            } else {
                DebugCommandStateStore.setDebugPauseHeld(false)
                MelonEmulator.resumeEmulation()
            }
        }
        Log.w(
            TAG,
            "action=load_state uri=$stateUri waitReady=${if (waitReady) 1 else 0} success=${if (success) 1 else 0} pauseAfter=${if (pauseAfterLoad) 1 else 0} timeoutMs=$timeoutMs requestedTimeoutMs=$requestedTimeoutMs",
        )
        return success
    }

    private suspend fun handleSaveState(
        context: Context,
        entryPoint: DebugCommandEntryPoint,
        intent: Intent,
    ): Boolean {
        val stateUri = resolveStateUri(context, entryPoint, intent, preferExistingSlotFallback = false)
            ?: throw IllegalArgumentException("Missing save target. Provide slot or path.")
        val pauseAfterSave = intent.getBooleanExtra(EXTRA_PAUSE_AFTER, false)
        MelonEmulator.pauseEmulation()
        val success = try {
            MelonEmulator.saveState(stateUri)
        } finally {
            if (pauseAfterSave) {
                DebugCommandStateStore.setDebugPauseHeld(true)
            } else {
                DebugCommandStateStore.setDebugPauseHeld(false)
                MelonEmulator.resumeEmulation()
            }
        }
        Log.w(
            TAG,
            "action=save_state uri=$stateUri success=${if (success) 1 else 0} pauseAfter=${if (pauseAfterSave) 1 else 0}",
        )
        return success
    }

    private suspend fun handleStepFrame(
        entryPoint: DebugCommandEntryPoint,
        intent: Intent,
    ): Boolean {
        val frames = intent.firstNullableIntExtra(EXTRA_STEP_FRAMES, EXTRA_FRAMES, EXTRA_VALUE)
            ?.coerceAtLeast(1)
            ?: 1
        val timeoutMs = intent.firstNullableIntExtra(EXTRA_TIMEOUT_MS, EXTRA_DURATION_MS, EXTRA_RESUME_MS)
            ?.coerceAtLeast(1)
            ?: 5_000
        val step = stepRendererFrames(entryPoint, frames, timeoutMs)
        Log.w(
            TAG,
            "action=step_frame renderer=${step.renderer.name.lowercase(Locale.US)} frames=$frames startFrame=${step.startFrame} endFrame=${step.endFrame} ready=${if (step.ready) 1 else 0} advanced=${if (step.advanced) 1 else 0}",
        )
        return step.ready && step.advanced
    }

    private suspend fun handleRunM7PresenterTrace(
        context: Context,
        entryPoint: DebugCommandEntryPoint,
        intent: Intent,
    ): Boolean {
        if (!DebugCommandStateStore.isRunningRom()) {
            Log.w(TAG, "action=run_m7_presenter_trace ready=0")
            return false
        }

        entryPoint.sharedPreferences().edit(commit = true) {
            putBoolean(KEY_RENDERER_DEBUG_TOOLS_ENABLED, true)
        }
        if (DebugCommandStateStore.requestSettingsRefresh()) {
            delay(350L)
        }
        if (entryPoint.settingsRepository().getCurrentVideoRenderer() != VideoRenderer.VULKAN) {
            Log.w(TAG, "action=run_m7_presenter_trace renderer=vulkan required=1")
            return false
        }

        val recordCount = intent.firstNullableIntExtra(EXTRA_CAPTURE_COUNT, EXTRA_VALUE)
            ?.coerceIn(1, MAX_PRESENTER_TRACE_RECORDS)
            ?: DEFAULT_PRESENTER_TRACE_RECORDS
        val pauseMs = intent.firstNullableIntExtra(EXTRA_DURATION_MS, EXTRA_RESUME_MS)
            ?.coerceIn(1, MAX_PRESENTER_TRACE_PAUSE_MS)
            ?: DEFAULT_PRESENTER_TRACE_PAUSE_MS
        val timeoutMs = intent.firstNullableIntExtra(EXTRA_TIMEOUT_MS)
            ?.coerceIn(1, MAX_PRESENTER_TRACE_TIMEOUT_MS)
            ?: DEFAULT_PRESENTER_TRACE_TIMEOUT_MS
        val pauseWasHeld = DebugCommandStateStore.isDebugPauseHeld()
        val startedTimestampNs = SystemClock.elapsedRealtimeNanos()
        var pauseStartedTimestampNs = 0L
        var pauseEndedTimestampNs = 0L

        RendererDebugBridge.startVulkanPresenterMetadataCapture(recordCount)
        try {
            DebugCommandStateStore.setDebugPauseHeld(false)
            MelonEmulator.resumeEmulation()
            delay(PRESENTER_TRACE_PRE_PAUSE_MS)

            pauseStartedTimestampNs = SystemClock.elapsedRealtimeNanos()
            MelonEmulator.pauseEmulation()
            delay(pauseMs.toLong())
            pauseEndedTimestampNs = SystemClock.elapsedRealtimeNanos()
            MelonEmulator.resumeEmulation()

            val deadlineMs = SystemClock.elapsedRealtime() + timeoutMs
            while (!RendererDebugBridge.isVulkanPresenterMetadataCaptureComplete() &&
                SystemClock.elapsedRealtime() < deadlineMs
            ) {
                delay(PRESENTER_TRACE_POLL_MS)
            }
        } finally {
            if (pauseWasHeld) {
                DebugCommandStateStore.setDebugPauseHeld(true)
                MelonEmulator.pauseEmulation()
            } else {
                DebugCommandStateStore.setDebugPauseHeld(false)
                MelonEmulator.resumeEmulation()
            }
        }

        val nativeJson = RendererDebugBridge.getVulkanPresenterMetadataCaptureJson()
            ?: return false
        val trace = JSONObject(nativeJson)
        val completed = trace.optBoolean("complete", false)
        trace.put(
            "scenario",
            JSONObject()
                .put("name", "world-pause-world")
                .put("startedTimestampNs", startedTimestampNs)
                .put("pauseStartedTimestampNs", pauseStartedTimestampNs)
                .put("pauseEndedTimestampNs", pauseEndedTimestampNs),
        )
        val outputDir = File(context.filesDir, "debug-evidence").apply { mkdirs() }
        val outputFile = File(outputDir, M7_PRESENTER_TRACE_OUTPUT_FILE)
        outputFile.writeText(trace.toString(2))
        Log.w(
            TAG,
            "action=run_m7_presenter_trace complete=${if (completed) 1 else 0} records=${trace.optInt("recordCount", 0)} file=${outputFile.name}",
        )
        return completed
    }

    private suspend fun handleRunM7SurfaceSequence(
        context: Context,
        entryPoint: DebugCommandEntryPoint,
        intent: Intent,
    ): Boolean {
        if (!DebugCommandStateStore.isRunningRom()) {
            Log.w(TAG, "action=run_m7_surface_sequence ready=0")
            return false
        }

        entryPoint.sharedPreferences().edit(commit = true) {
            putBoolean(KEY_RENDERER_DEBUG_TOOLS_ENABLED, true)
        }
        if (DebugCommandStateStore.requestSettingsRefresh()) {
            delay(350L)
        }
        if (entryPoint.settingsRepository().getCurrentVideoRenderer() != VideoRenderer.VULKAN) {
            return false
        }

        val summaryOnly = intent.firstBooleanExtra(EXTRA_SUMMARY_ONLY) ?: false
        val maxCaptureCount = if (summaryOnly) {
            MAX_M7_SUMMARY_SEQUENCE_FRAMES
        } else {
            MAX_M7_SURFACE_SEQUENCE_FRAMES
        }
        val captureCount = intent.firstNullableIntExtra(EXTRA_CAPTURE_COUNT, EXTRA_FRAMES, EXTRA_VALUE)
            ?.coerceIn(1, maxCaptureCount)
            ?: DEFAULT_M7_SURFACE_SEQUENCE_FRAMES
        val secondary = intent.firstBooleanExtra(EXTRA_SECONDARY) ?: false
        val leftX = (intent.firstFloatExtra(EXTRA_X, EXTRA_VALUE_X) ?: 0f).coerceIn(-1f, 1f)
        val leftY = (intent.firstFloatExtra(EXTRA_Y, EXTRA_VALUE_Y) ?: 0f).coerceIn(-1f, 1f)
        val cameraX = (intent.firstFloatExtra(EXTRA_CAMERA_X) ?: 0f).coerceIn(-1f, 1f)
        val cameraY = (intent.firstFloatExtra(EXTRA_CAMERA_Y) ?: 0f).coerceIn(-1f, 1f)
        val transitionInput = intent.firstStringExtra(EXTRA_INPUT)?.let { rawInput ->
            Input.SYSTEM_BUTTONS.firstOrNull { it.name.equals(rawInput, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unsupported system input=$rawInput")
        }
        val transitionInputFrames = (intent.firstNullableIntExtra(EXTRA_INPUT_FRAMES) ?: 1)
            .coerceIn(1, captureCount)
        val warmupFrames = (intent.firstNullableIntExtra(EXTRA_WARMUP_FRAMES) ?: 0)
            .coerceIn(0, MAX_M7_SURFACE_SEQUENCE_WARMUP_FRAMES)
        val timeoutMs = (intent.firstNullableIntExtra(EXTRA_TIMEOUT_MS) ?: DEFAULT_ANALOG_STEP_TIMEOUT_MS)
            .coerceIn(250, MAX_RECEIVER_WAIT_TIMEOUT_MS)
        val outputDir = File(context.filesDir, M7_SURFACE_SEQUENCE_OUTPUT_DIR).apply {
            deleteRecursively()
            mkdirs()
        }
        val frames = JSONArray()
        val pauseWasHeld = DebugCommandStateStore.isDebugPauseHeld()
        var passed = true
        var transitionInputHeld = false
        fun summarizePixels(pixels: IntArray, width: Int, height: Int): JSONObject {
            var hash = 1125899906842597L
            var alphaMin = 255
            var alphaMax = 0
            var opaquePixels = 0
            var nonBlackPixels = 0
            var redSum = 0L
            var greenSum = 0L
            var blueSum = 0L
            pixels.forEach { pixel ->
                val alpha = pixel ushr 24
                val red = pixel ushr 16 and 0xff
                val green = pixel ushr 8 and 0xff
                val blue = pixel and 0xff
                hash = hash * 31L + (pixel.toLong() and 0xffffffffL)
                alphaMin = minOf(alphaMin, alpha)
                alphaMax = maxOf(alphaMax, alpha)
                if (alpha == 255) opaquePixels++
                if (red != 0 || green != 0 || blue != 0) nonBlackPixels++
                redSum += red
                greenSum += green
                blueSum += blue
            }
            val pixelCount = pixels.size.coerceAtLeast(1)
            return JSONObject()
                .put("width", width)
                .put("height", height)
                .put("pixelCount", pixels.size)
                .put("pixelHash64", java.lang.Long.toUnsignedString(hash))
                .put("alphaMin", alphaMin)
                .put("alphaMax", alphaMax)
                .put("opaquePixels", opaquePixels)
                .put("nonBlackPixels", nonBlackPixels)
                .put("meanRed", redSum.toDouble() / pixelCount)
                .put("meanGreen", greenSum.toDouble() / pixelCount)
                .put("meanBlue", blueSum.toDouble() / pixelCount)
        }

        MelonEmulator.pauseEmulation()
        DebugCommandStateStore.setDebugPauseHeld(true)
        try {
            dispatchControllerMotion()
            if (warmupFrames > 0) {
                stepRendererFrames(entryPoint, warmupFrames, timeoutMs)
            }
            for (index in 0 until captureCount) {
                if (index == 0 && transitionInput != null) {
                    MelonEmulator.onInputDown(transitionInput)
                    transitionInputHeld = true
                }
                RendererDebugBridge.startVulkanPresenterMetadataCapture(PRESENTER_RECORDS_PER_DUAL_FRAME)
                val inputHandled = dispatchControllerMotion(leftX, leftY, cameraX, cameraY)
                val step = stepRendererFrames(entryPoint, 1, timeoutMs)
                if (transitionInputHeld && index + 1 >= transitionInputFrames) {
                    MelonEmulator.onInputUp(transitionInput!!)
                    transitionInputHeld = false
                }
                val presenterDeadlineMs = SystemClock.elapsedRealtime() + timeoutMs
                while (!RendererDebugBridge.isVulkanPresenterMetadataCaptureComplete() &&
                    SystemClock.elapsedRealtime() < presenterDeadlineMs
                ) {
                    delay(PRESENTER_TRACE_POLL_MS)
                }
                val presenter = RendererDebugBridge.getVulkanPresenterMetadataCaptureJson()
                    ?.let(::JSONObject)
                val bitmap = DebugCommandStateStore.captureSurfaceBitmap(secondary)
                val keyframe = !summaryOnly ||
                    index == 0 ||
                    index == captureCount / 2 ||
                    index == captureCount - 1
                val outputFile = File(outputDir, "frame_%04d.png".format(Locale.US, index))
                val finalPixels = bitmap?.let {
                    IntArray(it.width * it.height).also { pixels ->
                        it.getPixels(pixels, 0, it.width, 0, 0, it.width, it.height)
                    }
                }
                val finalSummary = if (bitmap != null && finalPixels != null) {
                    summarizePixels(finalPixels, bitmap.width, bitmap.height)
                } else {
                    null
                }
                val pngWritten = if (bitmap != null && keyframe) {
                    val written = outputFile.outputStream().use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    }
                    written
                } else {
                    false
                }
                bitmap?.recycle()
                val sourceDimensions = if (secondary) {
                    intArrayOf(RendererDebugBridge.CAPTURE_WIDTH, RendererDebugBridge.CAPTURE_HEIGHT / 2)
                } else {
                    RendererDebugBridge.captureCurrent3dDimensions()
                }
                val sourcePixels = if (secondary) {
                    RendererDebugBridge.captureCurrentPackedBottomPrimary()
                } else {
                    RendererDebugBridge.captureCurrent3dFrame()
                }
                val sourceWidth = sourceDimensions?.getOrNull(0) ?: 0
                val sourceHeight = sourceDimensions?.getOrNull(1) ?: 0
                val sourceFile = File(outputDir, "source_%04d.png".format(Locale.US, index))
                fun writePixels(file: File, pixels: IntArray?, width: Int, height: Int): Boolean {
                    if (pixels == null || width <= 0 || height <= 0 || pixels.size != width * height) {
                        return false
                    }
                    val sourceBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    sourceBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
                    val written = file.outputStream().use { stream ->
                        sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    }
                    sourceBitmap.recycle()
                    return written
                }
                val sourceCaptured = sourcePixels != null &&
                    sourceWidth > 0 &&
                    sourceHeight > 0 &&
                    sourcePixels.size == sourceWidth * sourceHeight
                val sourceSummary = if (sourceCaptured) {
                    summarizePixels(sourcePixels, sourceWidth, sourceHeight)
                } else {
                    null
                }
                val sourceWritten = if (keyframe) {
                    writePixels(sourceFile, sourcePixels, sourceWidth, sourceHeight)
                } else {
                    false
                }
                val uiOverlayFile = File(outputDir, "ui_overlay_%04d.png".format(Locale.US, index))
                val uiControlFile = File(outputDir, "ui_control_%04d.png".format(Locale.US, index))
                val uiOverlayWritten = summaryOnly || secondary || writePixels(
                    uiOverlayFile,
                    RendererDebugBridge.captureCurrentPackedPlane(0, 1),
                    RendererDebugBridge.CAPTURE_WIDTH,
                    RendererDebugBridge.CAPTURE_HEIGHT / 2,
                )
                val uiControlWritten = summaryOnly || secondary || writePixels(
                    uiControlFile,
                    RendererDebugBridge.captureCurrentPackedPlane(0, 2),
                    RendererDebugBridge.CAPTURE_WIDTH,
                    RendererDebugBridge.CAPTURE_HEIGHT / 2,
                )
                val presenterComplete = presenter?.optBoolean("complete", false) == true
                val framePassed = inputHandled &&
                    step.advanced &&
                    presenterComplete &&
                    finalSummary != null &&
                    sourceCaptured &&
                    (!keyframe || pngWritten) &&
                    (!keyframe || sourceWritten) &&
                    uiOverlayWritten &&
                    uiControlWritten
                passed = passed && framePassed
                frames.put(
                    JSONObject()
                        .put("index", index)
                        .put("file", if (pngWritten) outputFile.name else JSONObject.NULL)
                        .put("pngBytes", if (pngWritten) outputFile.length() else 0L)
                        .put("finalSummary", finalSummary ?: JSONObject.NULL)
                        .put("sourceFile", if (sourceWritten) sourceFile.name else JSONObject.NULL)
                        .put("sourceWidth", sourceWidth)
                        .put("sourceHeight", sourceHeight)
                        .put("sourcePngBytes", if (sourceWritten) sourceFile.length() else 0L)
                        .put("sourceSummary", sourceSummary ?: JSONObject.NULL)
                        .put("uiOverlayFile", if (summaryOnly || secondary) JSONObject.NULL else uiOverlayFile.name)
                        .put("uiOverlayPngBytes", if (uiOverlayWritten && !summaryOnly && !secondary) uiOverlayFile.length() else 0L)
                        .put("uiControlFile", if (summaryOnly || secondary) JSONObject.NULL else uiControlFile.name)
                        .put("uiControlPngBytes", if (uiControlWritten && !summaryOnly && !secondary) uiControlFile.length() else 0L)
                        .put("inputHandled", inputHandled)
                        .put("startFrame", step.startFrame)
                        .put("endFrame", step.endFrame)
                        .put("frameReady", step.ready)
                        .put("frameAdvanced", step.advanced)
                        .put("presenterComplete", presenterComplete)
                        .put("presenterRecordCount", presenter?.optInt("recordCount", 0) ?: 0)
                        .put("presenter", presenter ?: JSONObject.NULL),
                )
            }
        } finally {
            if (transitionInputHeld) {
                MelonEmulator.onInputUp(transitionInput!!)
            }
            dispatchControllerMotion()
            if (pauseWasHeld) {
                DebugCommandStateStore.setDebugPauseHeld(true)
                MelonEmulator.pauseEmulation()
            } else {
                DebugCommandStateStore.setDebugPauseHeld(false)
                MelonEmulator.resumeEmulation()
            }
        }

        val manifest = JSONObject()
            .put("schema", "thords.m7-surface-sequence.v1")
            .put("target", if (secondary) "secondary" else "main")
            .put("captureCount", captureCount)
            .put("summaryOnly", summaryOnly)
            .put("transitionInput", transitionInput?.name ?: JSONObject.NULL)
            .put("transitionInputFrames", if (transitionInput == null) 0 else transitionInputFrames)
            .put("warmupFrames", warmupFrames)
            .put("leftX", leftX)
            .put("leftY", leftY)
            .put("cameraX", cameraX)
            .put("cameraY", cameraY)
            .put("frames", frames)
            .put("result", if (passed) "PASS" else "PARTIAL")
        File(outputDir, M7_SURFACE_SEQUENCE_MANIFEST).writeText(manifest.toString(2))
        Log.w(
            TAG,
            "action=run_m7_surface_sequence target=${if (secondary) "secondary" else "main"} captures=${frames.length()} summaryOnly=${if (summaryOnly) 1 else 0} result=${if (passed) "PASS" else "PARTIAL"}",
        )
        return passed
    }

    private fun handleGrantM7CastleKey(context: Context): Boolean {
        if (!DebugCommandStateStore.isRunningRom()) {
            Log.w(TAG, "action=grant_m7_castle_key ready=0 result=PARTIAL")
            return false
        }

        val pauseWasHeld = DebugCommandStateStore.isDebugPauseHeld()
        MelonEmulator.pauseEmulation()
        DebugCommandStateStore.setDebugPauseHeld(true)
        val values = try {
            RendererDebugBridge.setMainRamBitsForDebug(
                M7_CASTLE_KEY_ADDRESS,
                M7_CASTLE_KEY_MASK,
            )
        } finally {
            if (!pauseWasHeld) {
                DebugCommandStateStore.setDebugPauseHeld(false)
                MelonEmulator.resumeEmulation()
            }
        }
        val oldValue = values?.getOrNull(0)
        val newValue = values?.getOrNull(1)
        val passed = oldValue != null &&
            newValue == oldValue.or(M7_CASTLE_KEY_MASK) &&
            newValue.and(M7_CASTLE_KEY_MASK) != 0
        File(context.cacheDir, M7_CASTLE_KEY_OUTPUT_FILE).writeText(
            JSONObject()
                .put("schema", "thords.m7-castle-key.v1")
                .put("address", Integer.toUnsignedLong(M7_CASTLE_KEY_ADDRESS))
                .put("setMask", Integer.toUnsignedLong(M7_CASTLE_KEY_MASK))
                .put("oldValue", oldValue?.let { Integer.toUnsignedLong(it) } ?: JSONObject.NULL)
                .put("newValue", newValue?.let { Integer.toUnsignedLong(it) } ?: JSONObject.NULL)
                .put("result", if (passed) "PASS" else "PARTIAL")
                .toString(2),
        )
        Log.w(TAG, "action=grant_m7_castle_key result=${if (passed) "PASS" else "PARTIAL"}")
        return passed
    }

    private suspend fun stepRendererFrames(
        entryPoint: DebugCommandEntryPoint,
        frames: Int,
        timeoutMs: Int,
    ): DebugFrameStep {
        val renderer = entryPoint.settingsRepository().getCurrentVideoRenderer()
        val startFrame = RendererDebugBridge.getCurrentFrameIndexForDebug()

        DebugCommandStateStore.setDebugPauseHeld(true)
        MelonEmulator.pauseEmulation()
        var allStepsRequested = true
        for (index in 0 until frames) {
            val frameBeforeStep = RendererDebugBridge.getCurrentFrameIndexForDebug()
            if (!MelonEmulator.debugStepFrame()) {
                allStepsRequested = false
                break
            }
            waitForRendererFrameOrTimeout(
                renderer = renderer,
                startFrame = frameBeforeStep,
                resumeFrames = 1,
                timeoutMs = timeoutMs.toLong(),
            )
        }
        MelonEmulator.pauseEmulation()
        waitForRendererReadyOrTimeout(
            renderer = renderer,
            minFrame = RendererDebugBridge.getCurrentFrameIndexForDebug(),
            timeoutMs = timeoutMs.toLong(),
        )
        DebugCommandStateStore.setDebugPauseHeld(true)

        val endFrame = RendererDebugBridge.getCurrentFrameIndexForDebug()
        val ready = renderer != VideoRenderer.VULKAN || RendererDebugBridge.isCurrentFrameReadyForDebug()
        return DebugFrameStep(
            renderer = renderer,
            startFrame = startFrame,
            endFrame = endFrame,
            ready = ready,
            advanced = allStepsRequested && (startFrame < 0 || endFrame == startFrame + frames),
        )
    }

    private suspend fun handleDumpRendererCapture(
        context: Context,
        entryPoint: DebugCommandEntryPoint,
        intent: Intent,
    ): Boolean {
        entryPoint.sharedPreferences().edit(commit = true) {
            putBoolean(KEY_RENDERER_DEBUG_TOOLS_ENABLED, true)
        }
        val refreshed = DebugCommandStateStore.requestSettingsRefresh()
        if (refreshed) {
            delay(350L)
        }

        val renderer = entryPoint.settingsRepository().getCurrentVideoRenderer()
        val outputDir = File(context.cacheDir, "renderer-debug-captures")
        val pauseWasHeld = DebugCommandStateStore.isDebugPauseHeld()
        val resumeMs = intent.firstNullableIntExtra(EXTRA_RESUME_MS, EXTRA_DURATION_MS)?.coerceAtLeast(0) ?: 0
        val resumeFrames = intent.firstNullableIntExtra(EXTRA_RESUME_FRAMES, EXTRA_FRAMES)?.coerceAtLeast(0) ?: 0
        val burstCount = intent.firstNullableIntExtra(EXTRA_BURST_COUNT, EXTRA_CAPTURE_COUNT)?.coerceAtLeast(1) ?: 1
        val burstStepMs = intent.firstNullableIntExtra(EXTRA_BURST_STEP_MS, EXTRA_STEP_MS)?.coerceAtLeast(0)
            ?: if (burstCount > 1) 0 else resumeMs
        val burstStepFrames = intent.firstNullableIntExtra(EXTRA_BURST_STEP_FRAMES, EXTRA_STEP_FRAMES)?.coerceAtLeast(0)
            ?: if (burstCount > 1) 1 else resumeFrames
        val burstLiveOverride = intent.firstBooleanExtra(EXTRA_BURST_LIVE, EXTRA_LIVE_BURST)
        val captureIdBase = intent.firstStringExtra(EXTRA_CAPTURE_ID_BASE, EXTRA_CAPTURE_ID)
            ?.takeIf { it.isNotBlank() }
        val captureKinds = parseCaptureKinds(
            rawKinds = intent.firstStringExtra(EXTRA_CAPTURE_KINDS, EXTRA_KINDS),
            defaultKinds = if (burstCount > 1) {
                setOf(RendererDebugCaptureKind.SCREEN_FRAME)
            } else {
                RendererDebugCaptureKind.allKinds
            },
        )
        val captureKindsFirst = parseCaptureKinds(
            rawKinds = intent.firstStringExtra(EXTRA_CAPTURE_KINDS_FIRST, EXTRA_FIRST_KINDS),
            defaultKinds = captureKinds,
        )
        val captureKindsRest = parseCaptureKinds(
            rawKinds = intent.firstStringExtra(EXTRA_CAPTURE_KINDS_REST, EXTRA_REST_KINDS),
            defaultKinds = captureKinds,
        )
        val burstLive = burstLiveOverride
            ?: (!requiresPausedBurstCapture(captureKindsFirst) && !requiresPausedBurstCapture(captureKindsRest))
        val captureOutputDir = if (burstCount > 1) {
            File(outputDir, "burst_${System.currentTimeMillis()}").apply { mkdirs() }
        } else {
            outputDir
        }
        val results = try {
            if (burstLive) {
                performLiveBurstCapture(
                    renderer = renderer,
                    pauseWasHeld = pauseWasHeld,
                    resumeMs = resumeMs,
                    resumeFrames = resumeFrames,
                    burstCount = burstCount,
                    burstStepMs = burstStepMs,
                burstStepFrames = burstStepFrames,
                captureOutputDir = captureOutputDir,
                captureIdBase = captureIdBase,
                captureKindsFirst = captureKindsFirst,
                captureKindsRest = captureKindsRest,
            )
        } else {
                performPausedBurstCapture(
                    renderer = renderer,
                    pauseWasHeld = pauseWasHeld,
                    resumeMs = resumeMs,
                    resumeFrames = resumeFrames,
                    burstCount = burstCount,
                    burstStepMs = burstStepMs,
                burstStepFrames = burstStepFrames,
                captureOutputDir = captureOutputDir,
                captureIdBase = captureIdBase,
                captureKindsFirst = captureKindsFirst,
                captureKindsRest = captureKindsRest,
            )
            }
        } finally {
            if (pauseWasHeld) {
                DebugCommandStateStore.setDebugPauseHeld(true)
                MelonEmulator.pauseEmulation()
            } else {
                DebugCommandStateStore.setDebugPauseHeld(false)
                MelonEmulator.resumeEmulation()
            }
        }
        if (results.size > 1) {
            File(captureOutputDir, "burst_manifest.txt").writeText(
                buildString {
                    appendLine("renderer=${renderer.name.lowercase(Locale.US)}")
                    appendLine("captures=${results.size}")
                    appendLine("liveBurst=${if (burstLive) 1 else 0}")
                    appendLine("stepFrames=$burstStepFrames")
                    appendLine("stepMs=$burstStepMs")
                    appendLine("captureKindsFirst=${captureKindsFirst.joinToString(separator = ",") { it.name.lowercase(Locale.US) }}")
                    appendLine("captureKindsRest=${captureKindsRest.joinToString(separator = ",") { it.name.lowercase(Locale.US) }}")
                    results.forEachIndexed { index, result ->
                        appendLine("capture[$index]=${result.captureId} success=${if (result.success) 1 else 0}")
                    }
                },
            )
        }
        val successCount = results.count { it.success }
        val firstCaptureId = results.firstOrNull()?.captureId ?: "none"
        Log.w(
            TAG,
            "action=dump_renderer_capture renderer=${renderer.name.lowercase(Locale.US)} refreshed=${if (refreshed) 1 else 0} paused=${if (pauseWasHeld) 1 else 0} liveBurst=${if (burstLive) 1 else 0} resumeMs=$resumeMs resumeFrames=$resumeFrames burstCount=$burstCount burstStepMs=$burstStepMs burstStepFrames=$burstStepFrames captureKindsFirst=${captureKindsFirst.joinToString(separator = ",") { it.name.lowercase(Locale.US) }} captureKindsRest=${captureKindsRest.joinToString(separator = ",") { it.name.lowercase(Locale.US) }} captureId=$firstCaptureId success=$successCount/${results.size} outputDir=${captureOutputDir.absolutePath}",
        )
        return successCount == results.size
    }

    private suspend fun performPausedBurstCapture(
        renderer: VideoRenderer,
        pauseWasHeld: Boolean,
        resumeMs: Int,
        resumeFrames: Int,
        burstCount: Int,
        burstStepMs: Int,
        burstStepFrames: Int,
        captureOutputDir: File,
        captureIdBase: String?,
        captureKindsFirst: Set<RendererDebugCaptureKind>,
        captureKindsRest: Set<RendererDebugCaptureKind>,
    ): List<RendererDebugCaptureResult> {
        if (!pauseWasHeld) {
            MelonEmulator.pauseEmulation()
        } else if (resumeMs > 0 || resumeFrames > 0) {
            DebugCommandStateStore.setDebugPauseHeld(false)
            val startFrame = RendererDebugBridge.getCurrentFrameIndexForDebug()
            if (shouldPrepareRendererSnapshot(renderer, captureKindsFirst)) {
                RendererDebugBridge.requestPreparedRendererSnapshot()
            }
            MelonEmulator.resumeEmulation()
            waitForRendererFrameOrTimeout(
                renderer = renderer,
                startFrame = startFrame,
                resumeFrames = resumeFrames,
                timeoutMs = resumeMs.toLong(),
            )
            MelonEmulator.pauseEmulation()
            waitForRendererReadyOrTimeout(
                renderer = renderer,
                minFrame = RendererDebugBridge.getCurrentFrameIndexForDebug(),
                timeoutMs = resumeMs.coerceAtLeast(1_000).toLong(),
            )
        }

        val captureBaseId = captureIdBase ?: java.lang.Long.toHexString(System.currentTimeMillis())
        return buildList<RendererDebugCaptureResult> {
            repeat(burstCount) { index ->
                val captureIdOverride = if (burstCount > 1) {
                    "${captureBaseId}_frame_${index.toString().padStart(4, '0')}"
                } else {
                    captureBaseId.takeIf { captureIdBase != null }
                }
                waitForRendererReadyOrTimeout(
                    renderer = renderer,
                    minFrame = RendererDebugBridge.getCurrentFrameIndexForDebug(),
                    timeoutMs = 1_000L,
                )
                add(
                    RendererDebugCaptureLogger.dumpPauseMenuCapture(
                        configuredRenderer = renderer,
                        outputDir = captureOutputDir,
                        captureIdOverride = captureIdOverride,
                        captureKinds = if (index == 0) captureKindsFirst else captureKindsRest,
                        freezeRendererSnapshot = true,
                    ),
                )
                if (index + 1 < burstCount) {
                    DebugCommandStateStore.setDebugPauseHeld(false)
                    val startFrame = RendererDebugBridge.getCurrentFrameIndexForDebug()
                    if (shouldPrepareRendererSnapshot(renderer, captureKindsRest)) {
                        RendererDebugBridge.requestPreparedRendererSnapshot()
                    }
                    MelonEmulator.resumeEmulation()
                    waitForRendererFrameOrTimeout(
                        renderer = renderer,
                        startFrame = startFrame,
                        resumeFrames = burstStepFrames,
                        timeoutMs = burstStepMs.toLong(),
                    )
                    MelonEmulator.pauseEmulation()
                    waitForRendererReadyOrTimeout(
                        renderer = renderer,
                        minFrame = RendererDebugBridge.getCurrentFrameIndexForDebug(),
                        timeoutMs = burstStepMs.coerceAtLeast(1_000).toLong(),
                    )
                }
            }
        }
    }

    private suspend fun performLiveBurstCapture(
        renderer: VideoRenderer,
        pauseWasHeld: Boolean,
        resumeMs: Int,
        resumeFrames: Int,
        burstCount: Int,
        burstStepMs: Int,
        burstStepFrames: Int,
        captureOutputDir: File,
        captureIdBase: String?,
        captureKindsFirst: Set<RendererDebugCaptureKind>,
        captureKindsRest: Set<RendererDebugCaptureKind>,
    ): List<RendererDebugCaptureResult> {
        if (
            captureKindsFirst == captureKindsRest
            && !requiresPausedBurstCapture(captureKindsFirst)
            && !requiresPausedBurstCapture(captureKindsRest)
        ) {
            DebugCommandStateStore.setDebugPauseHeld(false)
            MelonEmulator.resumeEmulation()
            val captureBaseId = captureIdBase ?: java.lang.Long.toHexString(System.currentTimeMillis())
            val timeoutMs = when {
                burstStepMs > 0 -> burstStepMs.toLong() * burstCount.toLong() + 5_000L
                else -> (burstCount.toLong() * maxOf(burstStepFrames, 1).toLong() * 1_000L) / 24L + 5_000L
            }
            return RendererDebugCaptureLogger.dumpDenseScreenBurstCapture(
                configuredRenderer = renderer,
                outputDir = captureOutputDir,
                captureIdBase = captureBaseId,
                burstCount = burstCount,
                burstStepFrames = burstStepFrames,
                timeoutMs = timeoutMs,
                captureKinds = captureKindsFirst,
            )
        }

        DebugCommandStateStore.setDebugPauseHeld(false)
        MelonEmulator.resumeEmulation()

        val initialStepFrames = if (resumeFrames > 0) resumeFrames else 0
        val initialStepMs = if (resumeMs > 0) resumeMs else 0
        if (initialStepFrames > 0 || initialStepMs > 0) {
            val startFrame = RendererDebugBridge.getCurrentFrameIndexForDebug()
            if (shouldPrepareRendererSnapshot(renderer, captureKindsFirst)) {
                RendererDebugBridge.requestPreparedRendererSnapshot()
            }
            waitForRendererFrameOrTimeout(
                renderer = renderer,
                startFrame = startFrame,
                resumeFrames = initialStepFrames,
                timeoutMs = initialStepMs.toLong(),
            )
        }

        val captureBaseId = captureIdBase ?: java.lang.Long.toHexString(System.currentTimeMillis())
        return buildList<RendererDebugCaptureResult> {
            var lastObservedFrame = RendererDebugBridge.getCurrentFrameIndexForDebug()
            if (burstCount > 0 && requiresPausedBurstCapture(captureKindsFirst)) {
                MelonEmulator.pauseEmulation()
                add(
                    RendererDebugCaptureLogger.dumpPauseMenuCapture(
                        configuredRenderer = renderer,
                        outputDir = captureOutputDir,
                        captureIdOverride = if (burstCount > 1) {
                            "${captureBaseId}_frame_${"0000"}"
                        } else {
                            captureBaseId.takeIf { captureIdBase != null }
                        },
                        captureKinds = captureKindsFirst,
                        freezeRendererSnapshot = true,
                    ),
                )
                DebugCommandStateStore.setDebugPauseHeld(false)
                MelonEmulator.resumeEmulation()
                lastObservedFrame = RendererDebugBridge.getCurrentFrameIndexForDebug()
            }

            val startIndex = if (burstCount > 0 && requiresPausedBurstCapture(captureKindsFirst)) 1 else 0
            for (index in startIndex until burstCount) {
                if (index > 0 || startIndex > 0) {
                    if (shouldPrepareRendererSnapshot(
                            renderer = renderer,
                            captureKinds = if (index == 0) captureKindsFirst else captureKindsRest,
                        )
                    ) {
                        RendererDebugBridge.requestPreparedRendererSnapshot()
                    }
                    waitForRendererAdvanceOrTimeout(
                        renderer = renderer,
                        lastObservedFrame = lastObservedFrame,
                        advanceFrames = burstStepFrames,
                        timeoutMs = burstStepMs.toLong(),
                    )
                }
                val captureIdOverride = if (burstCount > 1) {
                    "${captureBaseId}_frame_${index.toString().padStart(4, '0')}"
                } else {
                    captureBaseId.takeIf { captureIdBase != null }
                }
                add(
                    RendererDebugCaptureLogger.dumpPauseMenuCapture(
                        configuredRenderer = renderer,
                        outputDir = captureOutputDir,
                        captureIdOverride = captureIdOverride,
                        captureKinds = if (index == 0) captureKindsFirst else captureKindsRest,
                        freezeRendererSnapshot = shouldFreezeRendererSnapshot(
                            renderer = renderer,
                            captureKinds = if (index == 0) captureKindsFirst else captureKindsRest,
                        ),
                    ),
                )
                lastObservedFrame = RendererDebugBridge.getCurrentFrameIndexForDebug()
            }
        }
    }

    private suspend fun resolveStateUri(
        context: Context,
        entryPoint: DebugCommandEntryPoint,
        intent: Intent,
        preferExistingSlotFallback: Boolean,
    ): Uri? {
        intent.firstStringExtra(EXTRA_PATH, EXTRA_URI)?.let { pathOrUri ->
            return parseUri(pathOrUri)
        }

        val slot = intent.firstNullableIntExtra(EXTRA_SLOT, EXTRA_VALUE) ?: return null
        require(slot in 0..8) { "Unsupported save state slot=$slot" }

        val romUri = resolveRomUriForSlot(context, intent) ?: return null
        val rom = entryPoint.romsRepository().getRomAtUri(romUri) ?: return null
        val resolvedUri = entryPoint.saveStatesRepository().getRomSaveStateUri(
            rom,
            SaveStateSlot(slot, exists = true, lastUsedDate = null, screenshot = null),
        )
        if (!preferExistingSlotFallback) {
            return resolvedUri
        }

        val fallbackUri = resolveExistingSlotFallbackUri(
            preferredUri = resolvedUri,
            romFileName = rom.fileName,
            slot = slot,
        ) ?: return resolvedUri
        Log.w(TAG, "action=slot_fallback slot=$slot preferred=$resolvedUri fallback=$fallbackUri")
        return fallbackUri
    }

    private suspend fun resolveRomUriForSlot(context: Context, intent: Intent): Uri? {
        intent.firstStringExtra(EXTRA_ROM_URI)?.let { return Uri.parse(it) }

        var romUri = DebugCommandStateStore.getLastRomUri(context)
        if (romUri != null) {
            return romUri
        }

        val deadlineAt = System.nanoTime() + ROM_URI_RESOLVE_TIMEOUT_MS * 1_000_000L
        while (romUri == null && System.nanoTime() < deadlineAt) {
            delay(ROM_URI_RESOLVE_STEP_MS)
            romUri = DebugCommandStateStore.getLastRomUri(context)
        }
        return romUri
    }

    private fun resolveExistingSlotFallbackUri(
        preferredUri: Uri,
        romFileName: String,
        slot: Int,
    ): Uri? {
        if (preferredUri.scheme != "file") {
            return null
        }
        val preferredPath = preferredUri.path ?: return null
        val preferredFile = File(preferredPath)
        if (preferredFile.exists() && preferredFile.length() > 0L) {
            return null
        }
        val parentDirectory = preferredFile.parentFile
            ?.takeIf { it.exists() && it.isDirectory }
            ?: return null

        val romName = romFileName.substringBeforeLast('.', romFileName).trim()
        if (romName.isEmpty()) {
            return null
        }
        val candidateFile = buildAlternativeSaveStateNames(romName).asSequence()
            .map { candidateName -> File(parentDirectory, "$candidateName.ml$slot") }
            .firstOrNull { file -> file.exists() && file.length() > 0L }
            ?: return null
        return Uri.fromFile(candidateFile)
    }

    private fun buildAlternativeSaveStateNames(romName: String): List<String> {
        val normalized = romName.trim()
        if (normalized.isEmpty()) {
            return emptyList()
        }

        val names = LinkedHashSet<String>()
        val analogSuffixes = listOf(" Analog", " (Analog)", " [Analog]", "[Analog]")
        analogSuffixes.forEach { suffix ->
            if (normalized.endsWith(suffix, ignoreCase = true)) {
                val stripped = normalized.dropLast(suffix.length).trimEnd()
                if (stripped.isNotEmpty()) {
                    names.add(stripped)
                }
            }
        }
        if (!normalized.endsWith(" Analog", ignoreCase = true)) {
            names.add("$normalized Analog")
        }
        return names.toList()
    }

    private fun applyPauseAfterReady(pauseAfterReady: Boolean) {
        if (pauseAfterReady) {
            DebugCommandStateStore.setDebugPauseHeld(true)
            MelonEmulator.pauseEmulation()
        } else {
            DebugCommandStateStore.setDebugPauseHeld(false)
            MelonEmulator.resumeEmulation()
        }
    }

    private fun parseRenderer(value: String): VideoRenderer? {
        return when (value.trim().lowercase(Locale.US)) {
            "software", "soft" -> VideoRenderer.SOFTWARE
            "opengl", "gl" -> VideoRenderer.OPENGL
            "vulkan", "vk" -> VideoRenderer.VULKAN
            else -> null
        }
    }

    private fun parseCaptureKinds(
        rawKinds: String?,
        defaultKinds: Set<RendererDebugCaptureKind>,
    ): Set<RendererDebugCaptureKind> {
        val value = rawKinds?.trim().orEmpty()
        if (value.isEmpty()) {
            return defaultKinds
        }

        val parsed = LinkedHashSet<RendererDebugCaptureKind>()
        value.split(',')
            .map { it.trim().lowercase(Locale.US) }
            .filter { it.isNotEmpty() }
            .forEach { token ->
                when (token) {
                    "all" -> parsed.addAll(RendererDebugCaptureKind.allKinds)
                    "vulkanexact", "vulkan_exact", "vulkan-exact", "exactframe", "exact_frame", "exact-frame" ->
                        parsed.addAll(RendererDebugCapturePresets.vulkanExactFrame)
                    "screen", "screenframe" -> parsed.add(RendererDebugCaptureKind.SCREEN_FRAME)
                    "packed" -> {
                        parsed.add(RendererDebugCaptureKind.PACKED_TOP_PRIMARY)
                        parsed.add(RendererDebugCaptureKind.PACKED_BOTTOM_PRIMARY)
                    }
                    "packedtop", "packed_top", "packedtopprimary" -> parsed.add(RendererDebugCaptureKind.PACKED_TOP_PRIMARY)
                    "packedbottom", "packed_bottom", "packedbottomprimary" -> parsed.add(RendererDebugCaptureKind.PACKED_BOTTOM_PRIMARY)
                    "packedtopplane1", "packed_top_plane1" -> parsed.add(RendererDebugCaptureKind.PACKED_TOP_PLANE1)
                    "packedtopcontrol", "packed_top_control" -> parsed.add(RendererDebugCaptureKind.PACKED_TOP_CONTROL)
                    "packedbottomplane1", "packed_bottom_plane1" -> parsed.add(RendererDebugCaptureKind.PACKED_BOTTOM_PLANE1)
                    "packedbottomcontrol", "packed_bottom_control" -> parsed.add(RendererDebugCaptureKind.PACKED_BOTTOM_CONTROL)
                    "capture3dsource", "capture3dsource", "capture3dsourceds", "capture3dsourceframe" ->
                        parsed.add(RendererDebugCaptureKind.CAPTURE3D_SOURCE_DS_FRAME)
                    "capturelinemask", "capturelineuses3dmask", "capture_line_uses_3d_mask" ->
                        parsed.add(RendererDebugCaptureKind.CAPTURE_LINE_USES_3D_MASK)
                    "comp4top", "comp4_top", "comp4topplaceholder", "comp4_top_placeholder" ->
                        parsed.add(RendererDebugCaptureKind.COMP4_TOP_PLACEHOLDER)
                    "comp4bottom", "comp4_bottom", "comp4bottomplaceholder", "comp4_bottom_placeholder" ->
                        parsed.add(RendererDebugCaptureKind.COMP4_BOTTOM_PLACEHOLDER)
                    "capturefallback", "capturefallbackmask", "capture_fallback_mask", "fallbackmask" ->
                        parsed.add(RendererDebugCaptureKind.CAPTURE_FALLBACK_MASK)
                    "softpackedmeta", "softpackedframemeta", "soft_packed_frame_meta", "softpackedframejson" ->
                        parsed.add(RendererDebugCaptureKind.SOFT_PACKED_FRAME_META_JSON)
                    "renderer3d", "3d", "renderer3dframe" -> parsed.add(RendererDebugCaptureKind.RENDERER3D_FRAME)
                    "capture3d", "3dcapture", "renderer3dcapture", "renderer3dcaptureframe" -> parsed.add(RendererDebugCaptureKind.RENDERER3D_CAPTURE_FRAME)
                    "depth", "renderer3ddepth" -> parsed.add(RendererDebugCaptureKind.RENDERER3D_DEPTH)
                    "attr", "attributes", "renderer3dattr" -> parsed.add(RendererDebugCaptureKind.RENDERER3D_ATTR)
                    "coverage", "renderer3dcoverage" -> parsed.add(RendererDebugCaptureKind.RENDERER3D_COVERAGE)
                    else -> throw IllegalArgumentException("Unsupported capture kind=$token")
                }
            }
        return if (parsed.isEmpty()) defaultKinds else parsed
    }

    private suspend fun waitForRendererFrameOrTimeout(
        renderer: VideoRenderer,
        startFrame: Int,
        resumeFrames: Int,
        timeoutMs: Long,
    ) {
        val effectiveTimeoutMs = when {
            timeoutMs > 0L -> timeoutMs
            resumeFrames > 0 -> 5_000L
            else -> 0L
        }

        if (effectiveTimeoutMs <= 0L) {
            return
        }

        val targetFrame = if (resumeFrames > 0 && startFrame >= 0) {
            startFrame + resumeFrames
        } else {
            Int.MIN_VALUE
        }
        if (targetFrame == Int.MIN_VALUE) {
            delay(effectiveTimeoutMs)
            return
        }
        val deadlineAt = System.nanoTime() + effectiveTimeoutMs * 1_000_000L
        while (System.nanoTime() < deadlineAt) {
            val currentFrame = RendererDebugBridge.getCurrentFrameIndexForDebug()
            val hasReachedTargetFrame = currentFrame >= targetFrame
            val rendererReady = renderer != VideoRenderer.VULKAN || RendererDebugBridge.isCurrentFrameReadyForDebug()
            if (hasReachedTargetFrame && rendererReady) {
                return
            }
            delay(8L)
        }
    }

    private suspend fun waitForRendererReadyOrTimeout(
        renderer: VideoRenderer,
        minFrame: Int,
        timeoutMs: Long,
    ) {
        if (renderer != VideoRenderer.VULKAN)
            return

        val effectiveTimeoutMs = timeoutMs.coerceAtLeast(1L)
        val deadlineAt = System.nanoTime() + effectiveTimeoutMs * 1_000_000L
        while (System.nanoTime() < deadlineAt) {
            val currentFrame = RendererDebugBridge.getCurrentFrameIndexForDebug()
            if (currentFrame >= minFrame && RendererDebugBridge.isCurrentFrameReadyForDebug()) {
                return
            }
            delay(8L)
        }
    }

    private suspend fun waitForRendererAdvanceOrTimeout(
        renderer: VideoRenderer,
        lastObservedFrame: Int,
        advanceFrames: Int,
        timeoutMs: Long,
    ) {
        val effectiveTimeoutMs = when {
            timeoutMs > 0L -> timeoutMs
            advanceFrames > 0 -> 5_000L
            else -> 0L
        }

        if (effectiveTimeoutMs <= 0L) {
            return
        }

        val currentFrame = RendererDebugBridge.getCurrentFrameIndexForDebug()
        val targetAdvance = if (advanceFrames > 0) advanceFrames else 1
        val referenceFrame = maxOf(lastObservedFrame, currentFrame)
        val targetFrame = if (referenceFrame >= 0) {
            referenceFrame + targetAdvance
        } else {
            Int.MIN_VALUE
        }

        if (targetFrame == Int.MIN_VALUE) {
            delay(effectiveTimeoutMs)
            return
        }

        val deadlineAt = System.nanoTime() + effectiveTimeoutMs * 1_000_000L
        while (System.nanoTime() < deadlineAt) {
            val nextFrame = RendererDebugBridge.getCurrentFrameIndexForDebug()
            val hasReachedTargetFrame = nextFrame >= targetFrame
            val rendererReady = renderer != VideoRenderer.VULKAN || RendererDebugBridge.isCurrentFrameReadyForDebug()
            if (hasReachedTargetFrame && rendererReady) {
                return
            }
            delay(8L)
        }
    }

    private fun requiresPausedBurstCapture(captureKinds: Set<RendererDebugCaptureKind>): Boolean {
        return captureKinds.any {
            it != RendererDebugCaptureKind.SCREEN_FRAME
                && it != RendererDebugCaptureKind.PACKED_TOP_PRIMARY
                && it != RendererDebugCaptureKind.PACKED_BOTTOM_PRIMARY
                && it != RendererDebugCaptureKind.RENDERER3D_CAPTURE_FRAME
                && it != RendererDebugCaptureKind.SOFT_PACKED_FRAME_META_JSON
        }
    }

    private fun shouldFreezeRendererSnapshot(
        renderer: VideoRenderer,
        captureKinds: Set<RendererDebugCaptureKind>,
    ): Boolean {
        if (renderer != VideoRenderer.VULKAN) {
            return true
        }
        return captureKinds.any { it != RendererDebugCaptureKind.SCREEN_FRAME }
    }

    private fun shouldPrepareRendererSnapshot(
        renderer: VideoRenderer,
        captureKinds: Set<RendererDebugCaptureKind>,
    ): Boolean {
        if (renderer != VideoRenderer.OPENGL) {
            return false
        }
        return captureKinds.any {
            it == RendererDebugCaptureKind.RENDERER3D_FRAME
                || it == RendererDebugCaptureKind.RENDERER3D_CAPTURE_FRAME
                || it == RendererDebugCaptureKind.RENDERER3D_DEPTH
                || it == RendererDebugCaptureKind.RENDERER3D_ATTR
                || it == RendererDebugCaptureKind.RENDERER3D_COVERAGE
        }
    }

    private fun parseUri(pathOrUri: String): Uri {
        val file = File(pathOrUri)
        return if (file.isAbsolute) {
            Uri.fromFile(file)
        } else {
            Uri.parse(pathOrUri)
        }
    }

    private fun Intent.firstStringExtra(vararg keys: String): String? {
        return keys.firstNotNullOfOrNull { key ->
            getStringExtra(key)?.takeIf { value -> value.isNotBlank() }
        }
    }

    private fun Intent.firstIntExtra(vararg keys: String): Int {
        return firstNullableIntExtra(*keys)
            ?: throw IllegalArgumentException("Missing integer extra. Tried keys=${keys.joinToString()}")
    }

    private fun Intent.firstNullableIntExtra(vararg keys: String): Int? {
        keys.forEach { key ->
            if (!hasExtra(key)) {
                return@forEach
            }

            val raw = extras?.get(key)
            when (raw) {
                is Int -> return raw
                is String -> raw.toIntOrNull()?.let { return it }
            }
        }

        return null
    }

    private fun Intent.firstBooleanExtra(vararg keys: String): Boolean? {
        keys.forEach { key ->
            if (!hasExtra(key)) {
                return@forEach
            }

            val raw = extras?.get(key)
            when (raw) {
                is Boolean -> return raw
                is String -> when (raw.trim().lowercase(Locale.US)) {
                    "1", "true", "on", "yes", "enabled" -> return true
                    "0", "false", "off", "no", "disabled" -> return false
                }
                is Int -> return raw != 0
            }
        }

        return null
    }

    private fun Intent.firstFloatExtra(vararg keys: String): Float? {
        keys.forEach { key ->
            if (!hasExtra(key)) {
                return@forEach
            }

            val raw = extras?.get(key)
            when (raw) {
                is Float -> return raw
                is Double -> return raw.toFloat()
                is Int -> return raw.toFloat()
                is String -> raw.toFloatOrNull()?.let { return it }
            }
        }

        return null
    }

    private companion object {
        private const val TAG = "DebugCommand"
        private const val RESULT_FAILURE = 0
        private const val RESULT_SUCCESS = 1
        private const val REQUEST_CODE_LAUNCH_ROM = 1
        private const val KEY_VIDEO_RENDERER = "video_renderer"
        private const val KEY_VIDEO_INTERNAL_RESOLUTION = "video_internal_resolution"
        private const val KEY_ENABLE_JIT = "enable_jit"
        private const val KEY_RENDERER_DEBUG_TOOLS_ENABLED = "video_renderer_debug_tools_enabled"
        private const val KEY_RENDERER_DEBUG_BGOBJ_ENABLED = "video_renderer_debug_bgobj_enabled"
        private const val KEY_RENDERER_DEBUG_LATCH_TRACE_ENABLED = "video_renderer_debug_latch_trace_enabled"

        private const val EXTRA_RENDERER = "renderer"
        private const val EXTRA_ROM_KEY = "rom_key"
        private const val EXTRA_PERCENT = "percent"
        private const val EXTRA_SCALE = "scale"
        private const val EXTRA_IR = "ir"
        private const val EXTRA_ENABLED = "enabled"
        private const val EXTRA_X = "x"
        private const val EXTRA_Y = "y"
        private const val EXTRA_VALUE_X = "value_x"
        private const val EXTRA_VALUE_Y = "value_y"
        private const val EXTRA_AXIS = "axis"
        private const val EXTRA_AXIS_X = "axis_x"
        private const val EXTRA_AXIS_Y = "axis_y"
        private const val EXTRA_CAMERA_X = "camera_x"
        private const val EXTRA_CAMERA_Y = "camera_y"
        private const val EXTRA_INVERT_X = "invert_x"
        private const val EXTRA_INVERT_Y = "invert_y"
        private const val EXTRA_DEADZONE = "deadzone"
        private const val EXTRA_DEVICE_ID = "device_id"
        private const val EXTRA_SECONDARY = "secondary"
        private const val EXTRA_TIMELINE = "timeline"
        private const val EXTRA_TIMELINE_OFF = "timeline_off"
        private const val EXTRA_DYNAMIC_INDEXING = "dynamic_indexing"
        private const val EXTRA_DYNAMIC_INDEXING_OFF = "dynamic_indexing_off"
        private const val EXTRA_SLOT = "slot"
        private const val EXTRA_PATH = "path"
        private const val EXTRA_URI = "uri"
        private const val EXTRA_ROM_URI = "rom_uri"
        private const val EXTRA_PAUSE_AFTER = "pause_after"
        private const val EXTRA_WAIT_ROM_READY = "wait_rom_ready"
        private const val EXTRA_WAIT_READY = "wait_ready"
        private const val EXTRA_WAIT_TIMEOUT_MS = "wait_timeout_ms"
        private const val EXTRA_RESUME_MS = "resume_ms"
        private const val EXTRA_RESUME_FRAMES = "resume_frames"
        private const val EXTRA_DURATION_MS = "duration_ms"
        private const val EXTRA_TIMEOUT_MS = "timeout_ms"
        private const val EXTRA_FRAMES = "frames"
        private const val EXTRA_BURST_COUNT = "burst_count"
        private const val EXTRA_CAPTURE_COUNT = "capture_count"
        private const val EXTRA_BURST_STEP_MS = "burst_step_ms"
        private const val EXTRA_STEP_MS = "step_ms"
        private const val EXTRA_BURST_STEP_FRAMES = "burst_step_frames"
        private const val EXTRA_STEP_FRAMES = "step_frames"
        private const val EXTRA_BURST_LIVE = "burst_live"
        private const val EXTRA_LIVE_BURST = "live_burst"
        private const val EXTRA_INPUT = "input"
        private const val EXTRA_INPUT_FRAMES = "input_frames"
        private const val EXTRA_WARMUP_FRAMES = "warmup_frames"
        private const val EXTRA_CAPTURE_KINDS = "capture_kinds"
        private const val EXTRA_KINDS = "kinds"
        private const val EXTRA_CAPTURE_ID_BASE = "capture_id_base"
        private const val EXTRA_CAPTURE_ID = "capture_id"
        private const val EXTRA_CAPTURE_KINDS_FIRST = "capture_kinds_first"
        private const val EXTRA_FIRST_KINDS = "first_kinds"
        private const val EXTRA_CAPTURE_KINDS_REST = "capture_kinds_rest"
        private const val EXTRA_REST_KINDS = "rest_kinds"
        private const val EXTRA_SUMMARY_ONLY = "summary_only"
        private const val EXTRA_VALUE = "value"
        private const val DEFAULT_ROM_READY_TIMEOUT_MS = 8_000
        private const val MAX_RECEIVER_WAIT_TIMEOUT_MS = 8_000
        private const val LAUNCH_ACTIVITY_SEEN_TIMEOUT_MS = 2_000L
        private const val ROM_URI_RESOLVE_TIMEOUT_MS = 4_000L
        private const val ROM_URI_RESOLVE_STEP_MS = 100L
        private const val DEFAULT_TOUCH_X = 128
        private const val DEFAULT_TOUCH_Y = 96
        private const val DEFAULT_TOUCH_DURATION_MS = 80
        private const val DEFAULT_INPUT_DURATION_MS = 80
        private const val CAMERA_NEUTRAL_SETTLE_MS = 100L
        private const val DEFAULT_ANALOG_STEP_TIMEOUT_MS = 1_000
        private const val ANALOG_SWEEP_DIRECTIONS = 16
        private const val ANALOG_SWEEP_OUTPUT_FILE = "analog-end-to-end.json"
        private const val ANALOG_GAMEPLAY_TRIAL_OUTPUT_FILE = "analog-gameplay-trial.json"
        private const val LIVE_CAMERA_TRIAL_OUTPUT_FILE = "live-camera-trial.json"
        private const val M7_PRESENTER_TRACE_OUTPUT_FILE = "m7-presenter-trace.json"
        private const val M7_CASTLE_KEY_OUTPUT_FILE = "m7-castle-key.json"
        private const val M7_SURFACE_SEQUENCE_OUTPUT_DIR = "debug-evidence/m7-surface-sequence"
        private const val M7_SURFACE_SEQUENCE_MANIFEST = "manifest.json"
        private const val DEFAULT_PRESENTER_TRACE_RECORDS = 192
        private const val MAX_PRESENTER_TRACE_RECORDS = 512
        private const val DEFAULT_PRESENTER_TRACE_PAUSE_MS = 400
        private const val MAX_PRESENTER_TRACE_PAUSE_MS = 2_000
        private const val DEFAULT_PRESENTER_TRACE_TIMEOUT_MS = 7_000
        private const val MAX_PRESENTER_TRACE_TIMEOUT_MS = 8_000
        private const val PRESENTER_TRACE_PRE_PAUSE_MS = 1_000L
        private const val PRESENTER_TRACE_POLL_MS = 25L
        private const val DEFAULT_M7_SURFACE_SEQUENCE_FRAMES = 8
        private const val MAX_M7_SURFACE_SEQUENCE_FRAMES = 16
        private const val MAX_M7_SUMMARY_SEQUENCE_FRAMES = 384
        private const val MAX_M7_SURFACE_SEQUENCE_WARMUP_FRAMES = 32
        private const val PRESENTER_RECORDS_PER_DUAL_FRAME = 2
        // European SM64DS SAVE_DATA[1] initial-castle-key flag, used only by the M7 debug fixture.
        private const val M7_CASTLE_KEY_ADDRESS = 0x0209CAA4
        private const val M7_CASTLE_KEY_MASK = 0x40
        private val ANALOG_SWEEP_MAGNITUDES = floatArrayOf(0.25f, 0.50f, 0.75f, 1.00f)

        private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        private const val ACTION_SET_RENDERER_SUFFIX = "SET_RENDERER"
        private const val ACTION_SET_IR_SUFFIX = "SET_IR"
        private const val ACTION_SET_JIT_SUFFIX = "SET_JIT"
        private const val ACTION_SET_ARM9_PERCENT_SUFFIX = "SET_ARM9_PERCENT"
        private const val ACTION_SET_SM64DS_CADENCE_PROBE_SUFFIX = "SET_SM64DS_CADENCE_PROBE"
        private const val ACTION_SET_SM64DS_POSE_INTERPOLATION_SUFFIX = "SET_SM64DS_POSE_INTERPOLATION"
        private const val ACTION_DUMP_ARM9_TELEMETRY_SUFFIX = "DUMP_ARM9_TELEMETRY"
        private const val ACTION_DUMP_SM64DS_GAME_LOOP_SUFFIX = "DUMP_SM64DS_GAME_LOOP"
        private const val ACTION_SET_SM64DS_SEMANTIC_MONITOR_SUFFIX = "SET_SM64DS_SEMANTIC_MONITOR"
        private const val ACTION_DUMP_SM64DS_SEMANTIC_TELEMETRY_SUFFIX = "DUMP_SM64DS_SEMANTIC_TELEMETRY"
        private const val ACTION_SET_BGOBJ_LOG_SUFFIX = "SET_BGOBJ_LOG"
        private const val ACTION_SET_LATCH_TRACE_SUFFIX = "SET_LATCH_TRACE"
        private const val ACTION_SET_FAST_FORWARD_SUFFIX = "SET_FAST_FORWARD"
        private const val ACTION_SET_THORDS_SAFE_MODE_SUFFIX = "SET_THORDS_SAFE_MODE"
        private const val ACTION_SET_SLOT2_ANALOG_SUFFIX = "SET_SLOT2_ANALOG"
        private const val ACTION_SET_SLOT2_ANALOG_MAPPING_SUFFIX = "SET_SLOT2_ANALOG_MAPPING"
        private const val ACTION_RUN_ANALOG_SWEEP_SUFFIX = "RUN_ANALOG_SWEEP"
        private const val ACTION_RUN_ANALOG_GAMEPLAY_TRIAL_SUFFIX = "RUN_ANALOG_GAMEPLAY_TRIAL"
        private const val ACTION_RUN_LIVE_CAMERA_TRIAL_SUFFIX = "RUN_LIVE_CAMERA_TRIAL"
        private const val ACTION_SET_VULKAN_FALLBACKS_SUFFIX = "SET_VULKAN_FALLBACKS"
        private const val ACTION_TOUCH_SCREEN_SUFFIX = "TOUCH_SCREEN"
        private const val ACTION_TAP_INPUT_SUFFIX = "TAP_INPUT"
        private const val ACTION_BACKFLIP_SUFFIX = "BACKFLIP"
        private const val ACTION_LAUNCH_ROM_SUFFIX = "LAUNCH_ROM"
        private const val EXTRA_WIDESCREEN_PROBE = "widescreen_probe"
        private const val EXTRA_VULKAN_ROTATE_180 = "vulkan_rotate_180"
        private const val ACTION_WAIT_ROM_READY_SUFFIX = "WAIT_ROM_READY"
        private const val ACTION_SAVE_STATE_SUFFIX = "SAVE_STATE"
        private const val ACTION_LOAD_STATE_SUFFIX = "LOAD_STATE"
        private const val ACTION_STEP_FRAME_SUFFIX = "STEP_FRAME"
        private const val ACTION_STEP_FRAMES_SUFFIX = "STEP_FRAMES"
        private const val ACTION_RUN_M7_PRESENTER_TRACE_SUFFIX = "RUN_M7_PRESENTER_TRACE"
        private const val ACTION_GRANT_M7_CASTLE_KEY_SUFFIX = "GRANT_M7_CASTLE_KEY"
        private const val ACTION_RUN_M7_SURFACE_SEQUENCE_SUFFIX = "RUN_M7_SURFACE_SEQUENCE"
        private const val ACTION_DUMP_RENDERER_CAPTURE_SUFFIX = "DUMP_RENDERER_CAPTURE"
        private const val SM64DS_EU_ROM_KEY = "asmp:0:ba3c4052e00c5cc31df5d5534c39de1b"
    }

    private data class DebugFrameStep(
        val renderer: VideoRenderer,
        val startFrame: Int,
        val endFrame: Int,
        val ready: Boolean,
        val advanced: Boolean,
    )

    private fun Context.debugCommandAction(suffix: String): String {
        return "$packageName.$suffix"
    }
}
