package com.example.babful.data.network

import com.example.babful.data.model.DeliveryItem
import com.example.babful.data.model.FeedItem
import com.example.babful.data.model.ShortsItem
import com.google.gson.annotations.SerializedName
import com.example.babful.data.model.StoreInfo // ⭐️ [신규]
import com.example.babful.data.model.User // ⭐️ [신규]
import com.example.babful.data.model.Transaction // ⭐️ [신규]
import com.example.babful.data.model.PaymentRequest // ⭐️ [신규]
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Path // ⭐️ [신규]
import java.util.Date // ⭐️ [신규]
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
// ⭐️ [신규] '구독' 요청 Body
data class SubscribeRequest(
    @SerializedName("store_id") val storeId: String
)

// ⭐️ [신규] 포인트 내역
data class Transaction(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("amount") val amount: Int,
    @SerializedName("type") val type: String,
    @SerializedName("timestamp") val timestamp: Date
)
// ⭐️ [신규] 포인트 사용 요청
data class PointUseRequest(
    @SerializedName("amount") val amount: Int,
    @SerializedName("reason") val reason: String
)

// ⭐️ [수정] 구글 응답 데이터 모델
data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    @SerializedName("overview_polyline")
    val overviewPolyline: Polyline
)

data class Polyline(
    val points: String
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
    // ⭐️ [신규] 6. '가게 정보' (GET)
    @GET("store/{storeId}")
    suspend fun getStoreInfo(@Path("storeId") storeId: String): StoreInfo

    // ⭐️ [신규] 7. '구독' (POST)
    @POST("subscribe")
    suspend fun subscribeStore(@Body request: SubscribeRequest)

    // ⭐️ [신규] 8. '구독 취소' (POST)
    @POST("unsubscribe")
    suspend fun unsubscribeStore(@Body request: SubscribeRequest)

    // ⭐️ [수정] 9. '포인트 내역' (model.Transaction 임포트)
    @GET("points/history")
    suspend fun getPointHistory(): List<Transaction>

    // ⭐️ [수정] 10. '포인트 사용' -> '결제/적립' (POST)
    @POST("payment/success")
    suspend fun processPayment(@Body request: PaymentRequest)

    // ⭐️ [수정] 11. '내 프로필 정보' (model.User 임포트)
    @GET("profile/me")
    suspend fun getProfileInfo(): User

    // ⭐️ [신규] 12. 길찾기 (Proxy)
    // 예: directions?origin=37.5,127.0&dest=37.4,127.1
    @GET("directions")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("dest") dest: String
    ): DirectionsResponse
}