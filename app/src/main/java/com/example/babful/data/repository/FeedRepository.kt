package com.example.babful.data.repository

import android.util.Log
import com.example.babful.data.db.FeedDao
import com.example.babful.data.model.FeedItem
import com.example.babful.data.network.ApiService
import com.example.babful.data.network.LikeRequest // ⭐️ [신규]
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(
    private val apiService: ApiService,
    private val feedDao: FeedDao
) {

    // (getFeedItemsFromNetwork - 24단계 SWR)
    suspend fun getFeedItemsFromNetwork(radius: Int, isRefresh: Boolean): List<FeedItem> {
        val networkItems = apiService.getFeedItems(radius = radius)
        Log.d("FeedRepository", "Go API 호출 성공 (radius: $radius)")
        
        // (API 응답에 isLiked가 포함되므로, Room에 저장하기 전에 가공)
        networkItems.forEach { 
            it.radius = radius 
            // ⭐️ (Room은 isLiked를 @Ignore 하므로, Room 저장 시 isLiked 상태는 유실됨)
        } 

        if (isRefresh) {
            feedDao.clearAllFeeds()
        }
        feedDao.insertAll(networkItems)
        
        return networkItems
    }

    // (getFeedItemsFromCache - 24단계 SWR)
    suspend fun getFeedItemsFromCache(radius: Int): List<FeedItem> {
        Log.d("FeedRepository", "Room DB 캐시 조회 (radius: $radius)")
        // ⭐️ (Room에 저장된 isLiked는 항상 false(기본값)임)
        return feedDao.getFeedsByRadius(radius)
    }
    
    suspend fun likeFeedItem(feedId: String) {
        apiService.likeFeedItem(LikeRequest(feedId = feedId))
    }
    
    // ⭐️ [신규] '좋아요 취소' API 호출
    suspend fun unlikeFeedItem(feedId: String) {
        apiService.unlikeFeedItem(LikeRequest(feedId = feedId))
    }
}