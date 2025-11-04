package com.example.appname.feed.domain.usecase

import com.example.appname.feed.domain.repository.FeedRepository
import javax.inject.Inject

/**
 * [설계 의도 요약]
 * "피드 목록을 새로고침한다"는 단일 비즈니스 로직(UseCase)을 캡슐화합니다.
 */
class RefreshPostsUseCase @Inject constructor( // 🚨 (1) Hilt 주입
    private val repository: FeedRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        return repository.refreshPosts()
    }
}