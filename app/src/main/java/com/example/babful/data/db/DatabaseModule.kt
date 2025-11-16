package com.example.babful.data.db

import android.content.Context
import androidx.room.Room
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
    fun provideDatabase(@ApplicationContext appContext: Context): BabfulDatabase {
        return Room.databaseBuilder(
            appContext,
            BabfulDatabase::class.java,
            "babful_android.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFeedDao(database: BabfulDatabase): FeedDao = database.feedDao()

    // ⭐️ [점검 2] 이 '@Provides' 함수가 누락되었거나 철자가 틀리지 않았는지 확인하세요.
    @Provides
    @Singleton
    fun provideDeliveryDao(database: BabfulDatabase): DeliveryDao = database.deliveryDao()

    @Provides
    @Singleton
    fun provideShortsDao(database: BabfulDatabase): ShortsDao = database.shortsDao()
}