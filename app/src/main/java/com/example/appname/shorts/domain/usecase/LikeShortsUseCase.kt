package com.example.appname.shorts.domain.usecase

import com.example.appname.shorts.domain.repository.ShortsRepository

/**
 * [설계 의도 요약]
 * "쇼츠에 '좋아요'를 누른다"는 단일 비즈니스 로직(UseCase)을 캡슐화합니다.
 */
class LikeShortsUseCase(
    private val repository: ShortsRepository
) {
    suspend operator fun invoke(itemId: Int): Result<Boolean> {
        // TODO: implement details (예: 좋아요 가능 여부 확인 로직)
        return repository.toggleLikeState(itemId)
    }
}