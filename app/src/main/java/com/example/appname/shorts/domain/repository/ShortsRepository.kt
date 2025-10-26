package com.example.appname.shorts.domain.repository

import com.example.appname.shorts.domain.model.ShortsComment // 🚨 (1) [New]
import com.example.appname.shorts.domain.model.ShortsItem
import kotlinx.coroutines.flow.Flow

interface ShortsRepository {
    fun getShortsItems(): Flow<List<ShortsItem>>
    suspend fun toggleLikeState(itemId: Int): Result<Boolean>

    // 🚨 (2) [New] '댓글 목록' 가져오기 함수 뼈대
    fun getComments(shortsId: Int): Flow<List<ShortsComment>>

    // 🚨 (3) [New] '댓글 제출' 함수 뼈대
    suspend fun submitComment(shortsId: Int, commentText: String): Result<Boolean>
}