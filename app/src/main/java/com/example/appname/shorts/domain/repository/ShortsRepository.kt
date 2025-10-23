package com.example.appname.shorts.domain.repository

import com.example.appname.shorts.domain.model.ShortsItem
import kotlinx.coroutines.flow.Flow

/**
 * [설계 의도 요약]
 * Shorts(쇼츠) 데이터에 접근하기 위한 '규칙(Interface)'을 정의합니다.
 */
interface ShortsRepository {

    /**
     * 모든 쇼츠 아이템 목록을 가져옵니다.
     */
    fun getShortsItems(): Flow<List<ShortsItem>>

    /**
     * 특정 쇼츠 아이템의 '좋아요' 상태를 토글(toggle)합니다.
     * @param itemId 토글할 아이템의 ID
     */
    suspend fun toggleLikeState(itemId: Int): Result<Boolean>

    // TODO: implement details (예: fun getComments(itemId: Int))
}