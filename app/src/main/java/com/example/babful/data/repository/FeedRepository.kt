package com.example.babful.data.repository // ⚠️ 신규 패키지

import com.example.babful.data.model.FeedItem
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject // ⭐️ [신규]
import javax.inject.Singleton // ⭐️ [신규]
@Singleton
class FeedRepository @Inject constructor() {
    suspend fun getFeedItems(radius: Int, isRefresh: Boolean): List<FeedItem> {

        // ⭐️ [이동] 1.5초 딜레이 (VM의 딜레이가 Repository로 이동)
        // (실제로는 네트워크/DB 조회 시간이 될 것입니다.)
        delay(1500)

        // ⭐️ [이동] 14단계 ViewModel의 'loadFeedData' 로직이 그대로 여기로 옴
        val itemCount = 20
        val startId = radius * 1000
        val endId = startId + itemCount - 1

        val prefix = if (isRefresh) "새로고침된 " else "VM-"

        return (startId..endId).map { i ->
            val imageUrl = "https://picsum.photos/seed/$i/300/300"
            FeedItem(
                id = UUID.randomUUID().toString(),
                userName = "${prefix}${radius}km_user_${i % itemCount + 1}",
                userProfileImageUrl = "https://picsum.photos/seed/user_$i/100/100",
                postImageUrl = imageUrl,
                content = "이것은 ${radius}km 반경 내 $i 번째 피드입니다.",
                likesCount = (0..100).random()
            )
        }
    }
}