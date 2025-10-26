package com.example.appname.feed.domain.repository

import com.example.appname.feed.domain.model.Post
import kotlinx.coroutines.flow.Flow

/**
 * [설계 의도 요약]
 *  * Feed(피드) 데이터에 접근하기 위한 '규칙(Interface)'을 정의합니다.
 *  * UseCase는 이 인터페이스에만 의존하며, 실제 구현(Data Layer)은 몰라도 됩니다.
 * '단일 진실 공급원(Single Source of Truth)'의 관문 역할을 합니다.
 */
interface FeedRepository {

    fun getPosts(): Flow<List<Post>>

    // 🚨 (1) [New] '좋아요' 토글 함수 뼈대 추가
    suspend fun togglePostLike(postId: Int): Result<Boolean>

    // 🚨 (2) [New] '댓글 제출' 함수 뼈대 추가
    suspend fun submitComment(postId: Int, commentText: String): Result<Boolean>
}