package com.example.appname.feed.domain.repository

import com.example.appname.feed.domain.model.Comment // 🚨 (1) Comment import
import com.example.appname.feed.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    // (1) 🚨 이 함수는 이제 Room DB의 Flow를 반환합니다. (SSOT)
    fun getPosts(): Flow<List<Post>>

    // (2) 🚨 [New] 네트워크에서 새 데이터를 가져오도록 '요청'하는 함수
    suspend fun refreshPosts(): Result<Boolean>

    suspend fun togglePostLike(postId: Int): Result<Boolean>
    suspend fun submitComment(postId: Int, commentText: String): Result<Boolean>
    fun getComments(postId: Int): Flow<List<Comment>>
}