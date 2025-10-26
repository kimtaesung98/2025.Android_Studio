package com.example.appname.feed.domain.usecase

import com.example.appname.feed.domain.repository.FeedRepository

/**
 * [설계 의도 요약]
 * "피드 게시물에 '좋아요'를 누른다"는 단일 비즈니스 로직(UseCase)을 캡슐화합니다.
 */
class LikePostUseCase(
    private val repository: FeedRepository
) {
    /**
     * @param postId '좋아요'를 누를 포스트의 ID
     */
    suspend operator fun invoke(postId: Int): Result<Boolean> {
        // TODO: implement details (예: 좋아요 가능 여부 확인, 포인트 적립 등)

        // 2단계 '살 붙이기'에서는 이 UseCase가 Repository의
        // '좋아요' 관련 함수(아직 미정의)를 호출해야 합니다.
        // return repository.toggleLike(postId)

        // (임시) 뼈대 단계에서는 임시 성공 반환
        println("LikePostUseCase: Post $postId liked (Simulation)")
        return Result.success(true)
    }
}