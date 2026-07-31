package me.magnum.melonds.github.services

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import me.magnum.melonds.domain.model.DownloadProgress
import me.magnum.melonds.domain.model.appupdate.AppUpdate
import me.magnum.melonds.domain.services.UpdateInstallManager

class NoUpdateInstallManager : UpdateInstallManager {
    companion object {
        const val IS_ENABLED = false
    }

    override fun downloadAndInstallUpdate(update: AppUpdate): Flow<DownloadProgress> = emptyFlow()
}
