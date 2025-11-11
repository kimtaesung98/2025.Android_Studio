package com.example.babful.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.babful.data.model.FeedItem

@Dao
interface FeedDao {
    // ⭐️ [신규] 삽입 (ID 겹치면 덮어쓰기)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FeedItem>)

    // ⭐️ [신규] 반경별 조회 (오프라인 캐시용)
    @Query("SELECT * FROM feeds WHERE radius = :radius")
    suspend fun getFeedsByRadius(radius: Int): List<FeedItem>

    // ⭐️ [신규] 전체 삭제 (새로고침용)
    @Query("DELETE FROM feeds")
    suspend fun clearAllFeeds()
}