package me.magnum.melonds.github.repositories

import kotlinx.coroutines.test.runTest
import me.magnum.melonds.github.services.NoUpdateInstallManager
import org.junit.Assert.assertNull
import org.junit.Assert.assertFalse
import org.junit.Test

class NoUpdatesRepositoryTest {
    @Test
    fun neverReturnsAnUpdateCandidate() = runTest {
        assertNull(NoUpdatesRepository().checkNewUpdate().getOrThrow())
    }

    @Test
    fun updateInstallerIsDisabled() {
        assertFalse(NoUpdateInstallManager.IS_ENABLED)
    }
}
