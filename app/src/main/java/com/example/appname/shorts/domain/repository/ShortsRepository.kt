package com.example.appname.shorts.domain.repository

import com.example.appname.shorts.domain.model.ShortsComment // 🚨 (1) [New]
import com.example.appname.shorts.domain.model.ShortsItem
import kotlinx.coroutines.flow.Flow

interface ShortsRepository {
    // (1) 🚨 이 함수는 이제 Room DB의 Flow를 반환합니다. (SSOT)
    fun getShortsItems(): Flow<List<ShortsItem>>

    // (2) 🚨 [New] 네트워크에서 새 데이터를 가져오도록 '요청'하는 함수
    suspend fun refreshShortsItems(): Result<Boolean>
    suspend fun toggleLikeState(itemId: Int): Result<Boolean>

    // 🚨 (2) [New] '댓글 목록' 가져오기 함수 뼈대
    fun getComments(shortsId: Int): Flow<List<ShortsComment>>

    // 🚨 (3) [New] '댓글 제출' 함수 뼈대
    suspend fun submitComment(shortsId: Int, commentText: String): Result<Boolean>
}