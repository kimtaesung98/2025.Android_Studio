package com.example.appname.feed.domain.usecase

import com.example.appname.feed.domain.model.Post
import com.example.appname.feed.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow

/**
 * [설계 의도 요약]
 * "피드 게시물 목록을 가져온다"는 단일 비즈니스 로직(UseCase)을 캡슐화합니다.
 * ViewModel은 이 클래스를 주입받아 이 로직을 실행합니다.
 */
class GetFeedPostsUseCase(
    private val repository: FeedRepository // (1) 구현체가 아닌 인터페이스에 의존
) {
    /**
     * UseCase를 함수처럼 호출할 수 있게 해주는 invoke 연산자
     */
    operator fun invoke(): Flow<List<Post>> {
        // TODO: implement details (예: 특정 사용자를 필터링하거나, 목록을 정렬하는 등 '비즈니스 로직' 추가)
        return repository.getPosts()
    }
}