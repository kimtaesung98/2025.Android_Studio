package com.example.appname.shorts.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.appname.shorts.data.local.model.ShortsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShortsDao {

    /**
     * (읽기) 모든 쇼츠를 Flow로 반환합니다. (SSOT 핵심)
     */
    @Query("SELECT * FROM shorts")
    fun getShortsItems(): Flow<List<ShortsEntity>>

    /**
     * (쓰기) 쇼츠 리스트를 DB에 덮어씁니다.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShortsItems(items: List<ShortsEntity>)

    /**
     * (삭제) 모든 쇼츠를 삭제합니다.
     */
    @Query("DELETE FROM shorts")
    suspend fun clearShortsItems()

    // TODO: 3단계 심화 - '좋아요' 클릭 시 로컬 DB도 즉시 갱신하는 쿼리
    // @Query("UPDATE shorts SET isLiked = :isLiked WHERE id = :id")
    // suspend fun updateLikeState(id: Int, isLiked: Boolean)
}