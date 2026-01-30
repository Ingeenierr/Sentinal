package com.example.phantom.di

import com.example.phantom.data.repository.BehaviorRepositoryImpl
import com.example.phantom.data.repository.DefensiveActionRepositoryImpl
import com.example.phantom.domain.repository.BehaviorRepository
import com.example.phantom.domain.repository.DefensiveActionRepository
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
    abstract fun bindBehaviorRepository(
        behaviorRepositoryImpl: BehaviorRepositoryImpl
    ): BehaviorRepository

    @Binds
    @Singleton
    abstract fun bindDefensiveActionRepository(
        defensiveActionRepositoryImpl: DefensiveActionRepositoryImpl
    ): DefensiveActionRepository
}