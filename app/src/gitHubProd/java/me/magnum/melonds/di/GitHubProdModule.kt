package me.magnum.melonds.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.magnum.melonds.domain.repositories.UpdatesRepository
import me.magnum.melonds.domain.services.UpdateInstallManager
import me.magnum.melonds.github.repositories.NoUpdatesRepository
import me.magnum.melonds.github.services.NoUpdateInstallManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GitHubProdModule {
    @Provides
    @Singleton
    fun provideUpdateInstallManager(): UpdateInstallManager = NoUpdateInstallManager()

    @Provides
    @Singleton
    fun provideUpdatesRepository(): UpdatesRepository = NoUpdatesRepository()
}
