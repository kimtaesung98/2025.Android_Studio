package com.example.appname.feed.domain.usecase

import com.example.appname.feed.domain.model.Comment
import com.example.appname.feed.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow

/**
 * [설계 의도 요약]
 * "특정 게시물의 댓글 목록을 가져온다"는 단일 비즈니스 로직(UseCase)을 캡슐화합니다.
 */
class GetCommentsUseCase(
    private val repository: FeedRepository
) {
    /**
     * @param postId 댓글을 가져올 포스트의 ID
     */
    operator fun invoke(postId: Int): Flow<List<Comment>> {
        // TODO: implement details (예: 비즈니스 로직 추가)
        return repository.getComments(postId)
    }
}