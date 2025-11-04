package com.example.appname.feed.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.appname.feed.data.local.model.PostEntity
import kotlinx.coroutines.flow.Flow

/**
 * [설계 의도 요약]
 * 'posts' 테이블(PostEntity)에 접근하는 DAO(Data Access Object)입니다.
 * Room이 이 인터페이스의 구현체를 자동으로 생성합니다.
 */
@Dao
interface PostDao {

    /**
     * (읽기) 모든 게시물을 Flow로 반환합니다.
     * 테이블에 변경이 생기면, 이 Flow는 자동으로 새 데이터를 방출합니다. (SSOT 핵심)
     */
    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getPosts(): Flow<List<PostEntity>>

    /**
     * (쓰기) 게시물 리스트를 DB에 삽입합니다.
     * (SSOT) 네트워크에서 데이터를 가져오면 이 함수를 호출합니다.
     * onConflict = REPLACE: 기본키(id)가 겹치면, 기존 데이터를 새 데이터로 덮어씁니다.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    /**
     * (삭제) 모든 게시물을 삭제합니다. (예: 캐시 클리어)
     */
    @Query("DELETE FROM posts")
    suspend fun clearPosts()
}