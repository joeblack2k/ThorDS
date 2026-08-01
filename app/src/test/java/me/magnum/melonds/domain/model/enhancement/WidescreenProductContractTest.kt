package me.magnum.melonds.domain.model.enhancement

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WidescreenProductContractTest {
    @Test
    fun productModeStaysTypedMainOnlyAndFailClosedInTheNativePresenter() {
        val jni = File("src/main/cpp/MelonDSAndroidJNI.cpp").readText()
        assertTrue(jni.contains("getWidescreenPresentationMode"))
        assertFalse(jni.contains("getDeveloperWidescreenProbe"))

        val presenter = File("src/main/cpp/renderer/VulkanSurfacePresenter.cpp").readText()
        val classifier = presenter
            .substringAfter("const bool widescreenRequested")
            .substringBefore("std::vector<DrawCall> drawCalls")
        assertTrue(classifier.contains("kWidescreenEnterSafeFrames = 2"))
        assertTrue(classifier.contains("inputs.topSourceHasStructured3d"))
        assertTrue(classifier.contains("inputs.liveSourceScreenSwap"))
        assertTrue(classifier.contains("kSmallStructuredOverlayVisiblePixelLimit = 4096"))
        assertTrue(classifier.contains("inputs.topStructuredAboveVisiblePixels"))
        assertTrue(classifier.contains("<= kSmallStructuredOverlayVisiblePixelLimit"))
        assertTrue(classifier.contains("inputs.capture3dSourceValid"))
        assertTrue(classifier.contains("surfaceState.widescreenWorldSafe = false"))
        assertTrue(classifier.contains("surfaceState.widescreenSessionLocked"))
        assertTrue(classifier.contains("if (surfaceState.widescreenWorldSafe)"))

        val thorTransform = presenter
            .substringAfter("if (config.rotatePrimaryVulkan180)")
            .substringBefore("if (surfaceState.mappedVertexMemory == nullptr)")
        assertTrue(thorTransform.contains("vertex.y = -vertex.y"))
        assertFalse(thorTransform.contains("vertex.x = -vertex.x"))

        val activity = File("src/main/java/me/magnum/melonds/ui/emulator/EmulatorActivity.kt").readText()
        assertTrue(activity.contains("viewModel.widescreenPresentationMode.value"))
        val externalPresentation =
            File("src/main/java/me/magnum/melonds/ui/emulator/render/ExternalPresentation.kt").readText()
        assertFalse(externalPresentation.contains("widescreenPresentationMode ="))
    }
}
