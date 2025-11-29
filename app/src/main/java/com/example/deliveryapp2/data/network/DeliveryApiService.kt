package com.example.deliveryapp2.data.network

import com.example.deliveryapp2.data.model.Order
import com.example.deliveryapp2.data.model.Store
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class StatusUpdate(val status: String) // 이 클래스가 있어야 함
// 요청/응답에 사용할 DTO (Data Transfer Object)

data class OrderRequest(
    val storeId: String,
    val items: List<String>,
    val totalPrice: Int
)

data class OrderResponse(
    val success: Boolean,
    val orderId: String,
    val message: String
)

interface DeliveryApiService {
    // --- 고객용 (Customer) ---

    // 매장 목록 가져오기
    @GET("stores")
    suspend fun getStores(): List<Store>

    // 주문 하기
    @POST("orders")
    suspend fun placeOrder(@Body request: OrderRequest): OrderResponse

    // 내 주문 내역 가져오기
    @GET("orders/my")
    suspend fun getMyOrders(): List<Order>


    // --- 점주용 (Owner) ---

    // 들어온 주문 목록 보기
    @GET("owner/orders")
    suspend fun getIncomingOrders(): List<Order>

    // 주문 상태 변경 (수락/거절/완료)
    @PUT("owner/orders/{orderId}/status")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: String,
        @Body status: StatusUpdate // String이 아니라 객체로 전달
    ): OrderResponse
}