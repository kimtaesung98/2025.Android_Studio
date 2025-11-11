package com.example.babful.data.repository // ⚠️ 기존 repository 패키지 사용

import com.example.babful.data.model.DeliveryItem
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 배달 데이터 전문 저장소 (FeedRepository와 동일한 구조)
 */
@Singleton
class DeliveryRepository @Inject constructor() {

    /**
     * 배달 주문 데이터를 가져옵니다. (네트워크/DB 호출 시뮬레이션)
     */
    suspend fun getDeliveryItems(): List<DeliveryItem> {

        // ⭐️ 1초 딜레이 (시뮬레이션)
        delay(1000)

        // ⭐️ [이동] 9단계 DeliveryViewModel의 'loadDeliveryOrders' 로직이 여기로 옴
        return (1..30).map { i ->
            val storeImgUrl = "https://picsum.photos/seed/store_$i/200/200"
            DeliveryItem(
                id = UUID.randomUUID().toString(),
                storeName = "Repo-맛있는 가게 #$i", // ⭐️ Repository에서 왔음을 구분
                storeImageUrl = storeImgUrl,
                estimatedTimeInMinutes = (10..60).random(),
                status = if (i % 3 == 0) "배달중" else "조리중"
            )
        }
    }
}