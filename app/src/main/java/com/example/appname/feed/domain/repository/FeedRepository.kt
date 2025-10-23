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

    /**
     * 모든 피드 게시물 목록을 가져옵니다.
     * @return Flow<List<Post>> - 데이터 변경을 실시간으로 감지할 수 있는 Flow 형태로 반환
     */
    fun getPosts(): Flow<List<Post>>

    // TODO: implement details (예: fun updateLike(postId: Int), fun submitComment(postId: Int, comment: String))
}