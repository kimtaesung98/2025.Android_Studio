package com.example.appname.shorts.domain.usecase

import com.example.appname.shorts.domain.repository.ShortsRepository
import javax.inject.Inject

class SubmitShortsCommentUseCase @Inject constructor(
    private val repository: ShortsRepository
) {
    suspend operator fun invoke(shortsId: Int, commentText: String): Result<Boolean> {
        if (commentText.isBlank()) {
            return Result.failure(IllegalArgumentException("댓글을 입력해주세요."))
        }
        return repository.submitComment(shortsId, commentText)
    }
}