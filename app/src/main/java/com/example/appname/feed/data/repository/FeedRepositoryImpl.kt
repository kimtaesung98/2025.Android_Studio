package com.example.appname.feed.data.repository

import com.example.appname.R
import kotlinx.coroutines.flow.flowOf
import com.example.appname.feed.data.local.dao.PostDao
import com.example.appname.feed.data.local.model.toDomainModel
import com.example.appname.feed.data.remote.api.FeedApi
import com.example.appname.feed.data.remote.model.toEntity
import com.example.appname.feed.domain.model.Comment
import com.example.appname.feed.domain.model.Post
import com.example.appname.feed.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
/**
 * [설계 의도 요약]
 * FeedRepository 인터페이스의 실제 구현체입니다.
 * '어떻게' 데이터를 가져올지(네트워크, DB)를 여기서 결정합니다.
 * 2단계 '살 붙이기' 단계에서 여기에 Retrofit 또는 Room 로직이 추가됩니다.
 */
class FeedRepositoryImpl @Inject constructor(
    private val feedApi: FeedApi, // (1) 🚨 Hilt가 Retrofit API 주입
    private val postDao: PostDao  // (2) 🚨 Hilt가 Room DAO 주입
) : FeedRepository {

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
    private val dummyComments = mutableListOf(
        Comment(id = "c1", postId = 1, author = "Alice", content = "첫 번째 댓글입니다!"),
        Comment(id = "c2", postId = 1, author = "Bob", content = "Compose 정말 좋네요.")
    )
    /**
     * 피드 목록을 가져오는 로직의 실제 구현
     */


        // (3) 🚨 [Update] getPosts는 이제 Room DB(Dao)만 바라봅니다. (SSOT)
        override fun getPosts(): Flow<List<Post>> {
            // Dao(Flow<List<PostEntity>>) -> Domain(Flow<List<Post>>) 변환
            return postDao.getPosts().map { entityList ->
                entityList.map { it.toDomainModel() }
            }
        }

        // (4) 🚨 [New] 네트워크 갱신 로직
        override suspend fun refreshPosts(): Result<Boolean> {
            return try {
                // 1. Retrofit으로 네트워크에서 DTO 가져오기
                val response = feedApi.getPosts()
                if (response.isSuccessful) {
                    val postDtos = response.body() ?: emptyList()

                    // 2. DTO -> Entity로 변환
                    val postEntities = postDtos.map { it.toEntity() }

                    // 3. Room DB 갱신 (덮어쓰기)
                    postDao.clearPosts() // (선택적) 기존 데이터 삭제
                    postDao.insertPosts(postEntities)

                    Result.success(true)
                } else {
                    Result.failure(Exception("네트워크 오류: ${response.code()}"))
                }
            } catch (e: Exception) {
                // (예: 인터넷 없음)
                Result.failure(e)
            }
        }

        // (5) 🚨 [Update] '좋아요' 로직 (API 호출로 변경)
        override suspend fun togglePostLike(postId: Int): Result<Boolean> {
            return try {
                val response = feedApi.likePost(postId) // 1. Retrofit API 호출
                if (response.isSuccessful) {
                    // TODO: 3단계 심화 - 성공 시 Room DB의 'isLiked' 상태도 갱신
                    Result.success(true)
                } else {
                    Result.failure(Exception("좋아요 API 오류"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // 🚨 (2) [New] '댓글 제출' 함수 구현체 추가
    override suspend fun submitComment(postId: Int, commentText: String): Result<Boolean> {
        // TODO: implement details (API 호출)
        println("FeedRepositoryImpl: Submitting comment '$commentText' for post $postId (Simulation)")
        return Result.success(true)
    }

    override fun getComments(postId: Int): Flow<List<Comment>> {
        // TODO: implement details (API 또는 Room에서 postId에 맞는 댓글 필터링)

        // (임시) 1단계에서는 postId에 맞는 댓글을 필터링하여 Flow로 반환
        val commentsForPost = dummyComments.filter { it.postId == postId }
        return flowOf(commentsForPost)
    }
}