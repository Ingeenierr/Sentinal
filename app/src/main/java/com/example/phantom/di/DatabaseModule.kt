package com.example.phantom.di

import android.content.Context
import com.example.phantom.data.local.AppDatabase
import com.example.phantom.data.local.BehaviorDao
import com.example.phantom.data.local.DefensiveActionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideBehaviorDao(appDatabase: AppDatabase): BehaviorDao {
        return appDatabase.behaviorDao()
    }

    @Provides
    fun provideDefensiveActionDao(appDatabase: AppDatabase): DefensiveActionDao {
        return appDatabase.defensiveActionDao()
    }
}