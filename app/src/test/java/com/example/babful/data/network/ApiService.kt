package com.example.babful.data.network

import com.example.babful.data.model.FeedItem
import com.example.babful.data.model.DeliveryItem
import com.example.babful.data.model.ShortsItem // ⭐️ [신규]
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // (Feed 완료)
    @GET("feed")
    suspend fun getFeedItems(
        @Query("radius") radius: Int
    ): List<FeedItem>

    // (Delivery 완료)
    @GET("delivery")
    suspend fun getDeliveryItems(): List<DeliveryItem>

    // ⭐️ [신규] /shorts 엔드포인트 호출 함수
    @GET("shorts")
    suspend fun getShortsItems(): List<ShortsItem>
}