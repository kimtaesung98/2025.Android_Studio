package com.example.appname.di

import android.content.Context
import androidx.room.Room
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.appname.feed.data.local.dao.PostDao
import com.example.appname.feed.data.local.model.PostEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * [설계 의도 요약]
 * Room Database의 추상 클래스입니다.
 * DB 버전 관리 및 Entity/Dao 등록을 담당합니다.
 */
// (1) 🚨 DB 버전 1, PostEntity 테이블 포함
@Database(entities = [PostEntity::class /* TODO: 다른 Entity 추가 */], version = 1)
abstract class AppDatabase : RoomDatabase() {
    // (2) 🚨 Hilt가 Dao를 주입할 수 있도록 추상 함수 제공
    abstract fun postDao(): PostDao
    // TODO: abstract fun shortsDao(): ShortsDao
}

/**
 * [설계 의도 요약]
 * Room Database 및 Dao를 Hilt가 주입할 수 있도록 '레시피'를 제공합니다.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * AppDatabase (DB 연결 통로) 인스턴스를 제공합니다.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database.db" // (3) 🚨 DB 파일명
        ).build()
    }

    /**
     * PostDao (DB 명령 인터페이스) 인스턴스를 제공합니다.
     * (Hilt가 AppDatabase를 주입받아 .postDao()를 호출)
     */
    @Provides
    @Singleton
    fun providePostDao(appDatabase: AppDatabase): PostDao {
        return appDatabase.postDao()
    }
}