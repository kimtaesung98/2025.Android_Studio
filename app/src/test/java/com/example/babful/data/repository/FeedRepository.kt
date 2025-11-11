package com.example.babful.data.repository

import android.util.Log
import com.example.babful.data.db.FeedDao // ⭐️ [신규]
import com.example.babful.data.model.FeedItem
import com.example.babful.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(
    private val apiService: ApiService,
    private val feedDao: FeedDao // ⭐️ [신규] Hilt가 DAO를 주입
) {

    suspend fun getFeedItemsFromNetwork(radius: Int, isRefresh: Boolean): List<FeedItem> {
        return try {
            val networkItems = apiService.getFeedItems(radius = radius)
            Log.d("FeedRepository", "[SWR] Go API 호출 성공 (radius: $radius)")

            networkItems.forEach { it.radius = radius }

            if (isRefresh) {
                feedDao.clearAllFeeds() // 5km 새로고침 시 전체 캐시 삭제
            }
            feedDao.insertAll(networkItems)

            networkItems // 네트워크 데이터를 반환
        } catch (e: Exception) {
            Log.e("FeedRepository", "[SWR] Go API 호출 실패", e)
            throw e // ⭐️ 실패 시 ViewModel이 알 수 있도록 예외를 다시 던짐
        }
    }
    suspend fun getFeedItemsFromCache(radius: Int): List<FeedItem> {
        Log.d("FeedRepository", "[SWR] Room DB 캐시 조회 (radius: $radius)")
        return feedDao.getFeedsByRadius(radius)
    }
}