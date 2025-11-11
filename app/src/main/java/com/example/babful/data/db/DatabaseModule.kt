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

    // 1. Hilt에게 DB 인스턴스 생성법을 알려줌
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): BabfulDatabase {
        return Room.databaseBuilder(
            appContext,
            BabfulDatabase::class.java,
            "babful_android.db" // ⭐️ Go 서버의 DB 파일과 이름 구분
        ).build()
    }

    // 2. Hilt에게 3개의 DAO 인스턴스 생성법을 알려줌
    @Provides
    @Singleton
    fun provideFeedDao(database: BabfulDatabase): FeedDao = database.feedDao()

    @Provides
    @Singleton
    fun provideDeliveryDao(database: BabfulDatabase): DeliveryDao = database.deliveryDao()

    @Provides
    @Singleton
    fun provideShortsDao(database: BabfulDatabase): ShortsDao = database.shortsDao()
}