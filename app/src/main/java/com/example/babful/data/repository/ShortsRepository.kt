package com.example.babful.data.repository // ⚠️ 기존 repository 패키지 사용

import com.example.babful.data.model.ShortsItem
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 쇼츠 데이터 전문 저장소
 */
@Singleton
class ShortsRepository @Inject constructor() {

    /**
     * 쇼츠 데이터를 가져옵니다. (네트워크/DB 호출 시뮬레이션)
     */
    suspend fun getShortsItems(): List<ShortsItem> {

        // ⭐️ 1초 딜레이 (시뮬레이션)
        delay(1000)

        // ⭐️ [이동] 10단계 ShortsViewModel의 'loadShorts' 로직이 여기로 옴
        return (1..20).map { i ->
            ShortsItem(
                id = UUID.randomUUID().toString(),
                storeName = "Repo-쇼츠 가게 #$i", // ⭐️ Repository에서 왔음을 구분
                storeId = "store_$i"
            )
        }
    }
}