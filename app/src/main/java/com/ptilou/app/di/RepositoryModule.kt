package com.ptilou.app.di

import com.ptilou.app.data.repository.GamificationRepository
import com.ptilou.app.data.repository.GamificationRepositoryImpl
import com.ptilou.app.data.repository.ProgressionRepository
import com.ptilou.app.data.repository.ProgressionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProgressionRepository(impl: ProgressionRepositoryImpl): ProgressionRepository

    @Binds
    @Singleton
    abstract fun bindGamificationRepository(impl: GamificationRepositoryImpl): GamificationRepository
}
