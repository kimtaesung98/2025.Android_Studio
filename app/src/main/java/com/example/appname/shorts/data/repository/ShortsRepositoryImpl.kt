package com.example.appname.shorts.data.repository

import com.example.appname.shorts.data.local.dao.ShortsDao
import com.example.appname.shorts.domain.model.ShortsComment // 🚨 (1) [New]
import com.example.appname.shorts.domain.model.ShortsItem
import com.example.appname.shorts.domain.repository.ShortsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject // 🚨 (1) [New]
import com.example.appname.shorts.data.local.model.toDomainModel
import com.example.appname.shorts.data.local.model.toEntity
import com.example.appname.shorts.data.remote.api.ShortsApi
import com.example.appname.shorts.data.remote.model.CommentRequestDto
import kotlinx.coroutines.flow.map
/**
 * [설계 의도 요약]
 * ShortsRepository 인터페이스의 실제 구현체입니다.
 * 2단계 '살 붙이기' 단계에서 여기에 Retrofit API 또는 Room 로직이 추가됩니다.
 */
abstract class ShortsRepositoryImpl @Inject constructor( /** 추상화를 해야되는 이유?*/
    private val shortsApi: ShortsApi, // (1) 🚨 Hilt가 Retrofit API 주입
    private val shortsDao: ShortsDao  // (2) 🚨 Hilt가 Room DAO 주입
) : ShortsRepository {

    // (1) 임시 더미 데이터 (원래 ViewModel에 있던 것)
    // 2단계에서는 이 데이터가 API나 DB에서 와야 함.
    private var dummyItems = listOf(
        ShortsItem(
            1,
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "짧은 영상 1",
            isLiked = true
        ),
        ShortsItem(
            2,
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            "짧은 영상 2"
        ),
        ShortsItem(
            3,
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            "짧은 영상 3",
            isLiked = false
        )
    )
    private val dummyComments = mutableListOf(
        ShortsComment(id = "sc1", shortsId = 1, author = "ShortsFan", content = "첫 번째 쇼츠네요!"),
        ShortsComment(id = "sc2", shortsId = 1, author = "Commenter", content = "재밌어요 ㅎㅎ")
    )

    // 🚨 (2) [New] '댓글 목록' 가져오기 함수 구현체
    override fun getComments(shortsId: Int): Flow<List<ShortsComment>> {
        // TODO: API 또는 Room에서 shortsId에 맞는 댓글 필터링
        val commentsForShorts = dummyComments.filter { it.shortsId == shortsId }
        return flowOf(commentsForShorts)
    }

    // 🚨 (3) [New] '댓글 제출' 함수 구현체
    override suspend fun submitComment(shortsId: Int, commentText: String): Result<Boolean> {
        // TODO: API로 댓글 제출
        dummyComments.add(
            ShortsComment(
                id = "sc${dummyComments.size + 1}",
                shortsId = shortsId,
                author = "NewUser", // (임시) 'User' 모듈의 로그인 정보 사용 필요
                content = commentText
            )
        )
        return Result.success(true)
    }

    override fun getShortsItems(): Flow<List<ShortsItem>> {
        // TODO: implement details
        return flowOf(dummyItems)
    }

    override suspend fun toggleLikeState(itemId: Int): Result<Boolean> {
        // TODO: implement details
        // (임시) 1단계에서는 메모리상의 데이터를 직접 수정 (2단계에서는 API 호출)
        dummyItems = dummyItems.map {
            if (it.id == itemId) it.copy(isLiked = !it.isLiked) else it
        }
        return Result.success(true)
    }
}