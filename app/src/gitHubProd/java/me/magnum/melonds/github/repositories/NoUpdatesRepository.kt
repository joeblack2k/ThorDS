package me.magnum.melonds.github.repositories

import me.magnum.melonds.domain.model.appupdate.AppUpdate
import me.magnum.melonds.domain.repositories.UpdatesRepository

class NoUpdatesRepository : UpdatesRepository {
    override suspend fun checkNewUpdate(): Result<AppUpdate?> = Result.success(null)

    override fun skipUpdate(update: AppUpdate) = Unit

    override fun notifyUpdateDownloaded(update: AppUpdate) = Unit
}
