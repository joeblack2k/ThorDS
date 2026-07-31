package me.magnum.melonds

import android.Manifest
import android.app.Application
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.magnum.melonds.common.UriFileHandler
import me.magnum.melonds.common.ThorDeviceDefaults
import me.magnum.melonds.common.uridelegates.UriHandler
import me.magnum.melonds.domain.repositories.SettingsRepository
import me.magnum.melonds.impl.AppLogFileRecorder
import me.magnum.melonds.impl.SettingsBackupManager
import me.magnum.melonds.impl.retroachievements.offline.HardcoreOfflineLossTracker
import me.magnum.melonds.migrations.Migrator
import javax.inject.Inject

@HiltAndroidApp
class MelonDSApplication : Application(), Configuration.Provider {
    companion object {
        const val NOTIFICATION_CHANNEL_ID_BACKGROUND_TASKS = "channel_cheat_importing"
        private const val NOTIFICATION_ID_HARDCORE_OFFLINE_LOSS = 2002

        init {
            System.loadLibrary("melonDS-android-frontend")
        }
    }

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var sharedPreferences: SharedPreferences
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var migrator: Migrator
    @Inject lateinit var uriHandler: UriHandler
    @Inject lateinit var hardcoreOfflineLossTracker: HardcoreOfflineLossTracker
    @Inject lateinit var settingsBackupManager: SettingsBackupManager
    @Inject lateinit var appLogFileRecorder: AppLogFileRecorder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        applyTheme()
        ThorDeviceDefaults.apply(sharedPreferences)
        performMigrations()
        settingsBackupManager.initializeMirror()
        appLogFileRecorder.start()
        recoverUnexpectedHardcoreOfflineLossIfNeeded()
        MelonDSAndroidInterface.setup(
            UriFileHandler(this, uriHandler),
            settingsRepository.getVulkanDriverConfiguration(applicationInfo.nativeLibraryDir),
        )
    }

    private fun createNotificationChannels() {
        val defaultChannel = NotificationChannelCompat.Builder(NOTIFICATION_CHANNEL_ID_BACKGROUND_TASKS, NotificationManagerCompat.IMPORTANCE_LOW)
            .setName(getString(R.string.notification_channel_background_tasks))
            .build()

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.createNotificationChannel(defaultChannel)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun applyTheme() {
        GlobalScope.launch(Dispatchers.Main) {
            settingsRepository.observeTheme().collect {
                AppCompatDelegate.setDefaultNightMode(it.nightMode)
            }
        }
    }

    private fun performMigrations() {
        migrator.performMigrations()
    }

    private fun recoverUnexpectedHardcoreOfflineLossIfNeeded() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val pendingLoss = hardcoreOfflineLossTracker.consumePendingUnlocks() ?: return
        if (pendingLoss.totalCount <= 0) return

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID_BACKGROUND_TASKS)
            .setSmallIcon(R.drawable.ic_melon_small)
            .setContentTitle(getString(R.string.offline_ra_hardcore_loss_notification_title))
            .setContentText(
                getString(
                    R.string.ra_pending_process_loss_notification_message,
                    pendingLoss.totalCount,
                    pendingLoss.achievementCount,
                    pendingLoss.leaderboardCount,
                    pendingLoss.gameTitle,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(this).notify(
                NOTIFICATION_ID_HARDCORE_OFFLINE_LOSS,
                notification,
            )
        } catch (_: SecurityException) {
            hardcoreOfflineLossTracker.markPendingSubmissions(
                userId = pendingLoss.userId,
                contentId = pendingLoss.contentId,
                gameTitle = pendingLoss.gameTitle,
                achievementCount = pendingLoss.achievementCount,
                leaderboardCount = pendingLoss.leaderboardCount,
            )
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        appLogFileRecorder.stop()
        MelonDSAndroidInterface.cleanup()
    }

    override val workManagerConfiguration: Configuration get() {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}
