package com.example.babful.data.network

import com.example.babful.data.model.DeliveryItem
import com.example.babful.data.model.FeedItem
import com.example.babful.data.model.ShortsItem
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// (AuthRequest, AuthResponse 31단계와 동일)
data class AuthRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
data class AuthResponse(
    @SerializedName("token") val token: String
)
// ⭐️ '좋아요' 요청 Body
data class LikeRequest(
    @SerializedName("feed_id") val feedId: String
)

interface ApiService {
    @GET("feed")
    suspend fun getFeedItems(@Query("radius") radius: Int): List<FeedItem>

    @GET("delivery")
    suspend fun getDeliveryItems(): List<DeliveryItem>

    @GET("shorts")
    suspend fun getShortsItems(): List<ShortsItem>
    
    @POST("login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @POST("register")
    suspend fun register(@Body request: AuthRequest)
    
    @POST("like")
    suspend fun likeFeedItem(@Body request: LikeRequest)
    
    // ⭐️ [신규] 5. '좋아요 취소' (POST)
    @POST("unlike")
    suspend fun unlikeFeedItem(@Body request: LikeRequest)
}