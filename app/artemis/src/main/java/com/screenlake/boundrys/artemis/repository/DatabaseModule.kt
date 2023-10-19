package com.screenlake.boundrys.artemis.repository

import android.content.Context
import com.screenlake.boundrys.artemis.database.ScreenshotDatabase
import com.screenlake.boundrys.artemis.database.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    fun provideScrollDao(appDatabase: ScreenshotDatabase): ScreenshotZipDAO {
        return appDatabase.getScreenshotZipDao
    }


    @Provides
    fun provideScreenshotZipDao(appDatabase: ScreenshotDatabase): ScreenshotZipDAO {
        return appDatabase.getScreenshotZipDao
    }

    @Provides
    fun provideUserDao(appDatabase: ScreenshotDatabase): UserDAO {
        return appDatabase.getUserDao
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): ScreenshotDatabase {
        return ScreenshotDatabase.getInstance(appContext)
    }
}