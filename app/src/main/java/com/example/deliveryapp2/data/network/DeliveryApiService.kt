package com.example.deliveryapp2.data.network

import com.example.deliveryapp2.data.model.MenuItem
import com.example.deliveryapp2.data.model.Order
import com.example.deliveryapp2.data.model.Store
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// DTOs
data class OrderRequest(val storeId: String, val items: List<String>, val totalPrice: Int, val deliveryAddress: String)
data class OrderResponse(val success: Boolean, val orderId: String, val message: String)
data class StatusUpdate(val status: String)
data class MenuAddResponse(val success: Boolean, val menu: MenuItem) // 추가

interface DeliveryApiService {
    // --- Customer ---
    @GET("stores")
    suspend fun getStores(): List<Store>

    @GET("stores/{storeId}/menus") // 추가: 매장별 메뉴 조회
    suspend fun getStoreMenus(@Path("storeId") storeId: String): List<MenuItem>

    @POST("orders")
    suspend fun placeOrder(@Body request: OrderRequest): OrderResponse

    @GET("orders/my")
    suspend fun getMyOrders(): List<Order>

    // --- Owner ---
    @GET("owner/orders")
    suspend fun getIncomingOrders(): List<Order>

    @PUT("owner/orders/{orderId}/status")
    suspend fun updateOrderStatus(@Path("orderId") orderId: String, @Body status: StatusUpdate): OrderResponse

    @POST("menus") // 추가: 메뉴 등록
    suspend fun addMenu(@Body menu: MenuItem): MenuAddResponse
}