package com.example.appname.feed.domain.usecase

import com.example.appname.feed.domain.repository.FeedRepository

/**
 * [설계 의도 요약]
 * "피드 게시물에 '댓글을 제출'한다"는 단일 비즈니스 로직(UseCase)을 캡슐화합니다.
 */
class SubmitCommentUseCase(
    private val repository: FeedRepository
) {
    /**
     * @param postId 댓글을 달 포스트의 ID
     * @param commentText 제출할 댓글 내용
     */
    suspend operator fun invoke(postId: Int, commentText: String): Result<Boolean> {
        // (1) UseCase는 비즈니스 로직(유효성 검사)을 포함할 수 있습니다.
        if (commentText.isBlank()) {
            return Result.failure(IllegalArgumentException("댓글 내용이 비어있습니다."))
        }

        // TODO: implement details (예: 비속어 필터링)

        // 2단계 '살 붙이기'에서는 이 UseCase가 Repository의
        // '댓글 제출' 관련 함수(아직 미정의)를 호출해야 합니다.
        // return repository.submitComment(postId, commentText)

        // (임시) 뼈대 단계에서는 임시 성공 반환
        println("SubmitCommentUseCase: Post $postId commented '$commentText' (Simulation)")
        return Result.success(true)
    }
}