package com.example.appname.feed.domain.repository

import com.example.appname.feed.domain.model.Comment // 🚨 (1) Comment import
import com.example.appname.feed.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface FeedRepository {

    fun getPosts(): Flow<List<Post>>
    suspend fun togglePostLike(postId: Int): Result<Boolean>
    suspend fun submitComment(postId: Int, commentText: String): Result<Boolean>

    // 🚨 (2) [New] '댓글 목록' 가져오기 함수 뼈대 추가
    fun getComments(postId: Int): Flow<List<Comment>>
}