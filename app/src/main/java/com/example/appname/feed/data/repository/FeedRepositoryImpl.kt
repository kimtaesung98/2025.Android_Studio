package com.example.appname.feed.data.repository

import com.example.appname.R
import com.example.appname.feed.domain.model.Post
import com.example.appname.feed.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * [설계 의도 요약]
 * FeedRepository 인터페이스의 실제 구현체입니다.
 * '어떻게' 데이터를 가져올지(네트워크, DB)를 여기서 결정합니다.
 * 2단계 '살 붙이기' 단계에서 여기에 Retrofit 또는 Room 로직이 추가됩니다.
 */
class FeedRepositoryImpl : FeedRepository {

    // (1) 2단계 '살 붙이기' 전까지 사용할 임시 더미 데이터
    private val dummyPosts = listOf(
        Post(
            1,
            "Gemini (from Repo)",
            "Clean Architecture 적용 완료!",
            R.drawable.ic_launcher_background,
            isLiked = true
        ),
        Post(
            2,
            "Android Studio (from Repo)",
            "이제 Repository에서 데이터를 가져옵니다.",
            R.drawable.ic_launcher_background
        )
    )

    /**
     * 피드 목록을 가져오는 로직의 실제 구현
     */
    override fun getPosts(): Flow<List<Post>> {
        // TODO: implement details
        // 2단계 '살 붙이기' 에서는 flowOf 대신,
        // Retrofit API 호출이나 Room DB 쿼리 결과를 Flow로 반환해야 함.
        return flowOf(dummyPosts)
    }


    // 🚨 (1) [New] '좋아요' 토글 함수 구현체 추가
    override suspend fun togglePostLike(postId: Int): Result<Boolean> {
        // TODO: implement details (API 호출)
        println("FeedRepositoryImpl: Toggling like for post $postId (Simulation)")
        // (임시) 2단계 '살 붙이기'에서는 dummyPosts의 isLiked를 직접 수정
        return Result.success(true)
    }

    // 🚨 (2) [New] '댓글 제출' 함수 구현체 추가
    override suspend fun submitComment(postId: Int, commentText: String): Result<Boolean> {
        // TODO: implement details (API 호출)
        println("FeedRepositoryImpl: Submitting comment '$commentText' for post $postId (Simulation)")
        return Result.success(true)
    }
}