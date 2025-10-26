package com.example.appname.shorts.data.repository

import com.example.appname.shorts.domain.model.ShortsItem
import com.example.appname.shorts.domain.repository.ShortsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * [설계 의도 요약]
 * ShortsRepository 인터페이스의 실제 구현체입니다.
 * 2단계 '살 붙이기' 단계에서 여기에 Retrofit API 또는 Room 로직이 추가됩니다.
 */
class ShortsRepositoryImpl : ShortsRepository {

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