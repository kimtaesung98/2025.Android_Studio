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
import com.example.appname.shorts.data.local.model.toDomainModel
import com.example.appname.shorts.data.local.model.toEntity
import kotlinx.coroutines.flow.map
/**
 * [설계 의도 요약]
 * ShortsRepository 인터페이스의 실제 구현체입니다.
 * 2단계 '살 붙이기' 단계에서 여기에 Retrofit API 또는 Room 로직이 추가됩니다.
 */
class ShortsRepositoryImpl @Inject constructor( /** 추상화를 해야되는 이유?*/
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


    override fun getShortsItems(): Flow<List<ShortsItem>> {
        return shortsDao.getShortsItems().map { entityList ->
            entityList.map { it.toDomainModel() }
        }
    }

    // (4) 🚨 [New] 네트워크 갱신 로직
    override suspend fun refreshShortsItems(): Result<Boolean> {
        return try {
            val response = shortsApi.getShorts() // 1. Retrofit API 호출
            if (response.isSuccessful) {
                val dtoList = response.body() ?: emptyList()
                val entityList = dtoList.map { it.toEntity() } // 2. DTO -> Entity
                shortsDao.clearShortsItems() // 3. Room 갱신
                shortsDao.insertShortsItems(entityList)
                Result.success(true)
            } else {
                Result.failure(Exception("Shorts 네트워크 오류"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // (5) 🚨 [Update] '좋아요' 로직: API 호출로 변경
    override suspend fun toggleLikeState(itemId: Int): Result<Boolean> {
        return try {
            val response = shortsApi.likeShort(itemId)
            if (response.isSuccessful) {
                // TODO: 3단계 심화 - 성공 시 Room DB의 'isLiked' 상태도 갱신
                Result.success(true)
            } else {
                Result.failure(Exception("좋아요 API 오류"))
            }
        } catch (e: Exception) { Result.failure(e) }
    }

    // (6) 🚨 [Update] '댓글' 로직: API 호출로 변경 (Room 캐시 미사용)
    override fun getComments(shortsId: Int): Flow<List<ShortsComment>> {
        // (단순화) 이 부분은 SSOT 없이, 매번 API를 호출하는 Flow로 임시 구현
        return kotlinx.coroutines.flow.flow {
            val response = shortsApi.getComments(shortsId)
            if(response.isSuccessful) {
                val dtoList = response.body() ?: emptyList()
                // (임시) DTO -> Domain 변환 (DTO와 Domain이 동일한 것으로 가정)
                val domainList = dtoList.map { ShortsComment(it.id, shortsId, it.author, it.content) }
                emit(domainList)
            } else {
                emit(emptyList())
            }
        }
    }

    // (7) 🚨 [Update] '댓글 제출' 로직: API 호출로 변경
    override suspend fun submitComment(shortsId: Int, commentText: String): Result<Boolean> {
        return try {
            val requestDto = CommentRequestDto(content = commentText)
            val response = shortsApi.submitComment(shortsId, requestDto)
            Result.success(response.isSuccessful)
        } catch (e: Exception) { Result.failure(e) }
    }
}