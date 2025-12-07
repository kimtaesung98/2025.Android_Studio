package com.example.deliveryapp2.data.network

import com.example.deliveryapp2.data.model.DashboardStats
import com.example.deliveryapp2.data.model.MenuItem
import com.example.deliveryapp2.data.model.Order
import com.example.deliveryapp2.data.model.Store
import retrofit2.Response // Response 타입을 쓰려면 필요
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// --- DTOs (서버 응답 형태와 100% 일치해야 함) ---
data class OrderRequest(val storeId: String, val items: List<String>, val totalPrice: Int, val deliveryAddress: String)
data class OrderResponse(val success: Boolean, val orderId: String, val message: String?, val error: String?)
data class StatusUpdate(val status: String)

// [수정] 서버는 {"success":true, "menuId":"..."} 를 줍니다.
data class MenuAddResponse(val success: Boolean, val menuId: String)

interface DeliveryApiService {

    // --- Customer (고객) ---

    @GET("stores")
    suspend fun getStores(): List<Store>

    @GET("stores/{storeId}/menus")
    suspend fun getStoreMenus(@Path("storeId") storeId: String): List<MenuItem>

    @POST("orders")
    suspend fun placeOrder(@Body request: OrderRequest): OrderResponse

    // 🟢 [수정] 서버 경로와 일치시킴 ("orders/my" -> "orders")
    @GET("orders")
    suspend fun getMyOrders(): List<Order>

    // --- Owner (점주) ---

    @GET("owner/orders")
    suspend fun getIncomingOrders(): List<Order>

    @PUT("owner/orders/{orderId}/status")
    suspend fun updateOrderStatus(@Path("orderId") orderId: String, @Body status: StatusUpdate): OrderResponse

    // 🟢 [수정] 중복된 addMenu 제거 및 하나로 통일
    @POST("menus")
    suspend fun addMenu(@Body menu: MenuItem): MenuAddResponse

    // [추가] 대시보드 통계 요청
    @GET("owner/dashboard")
    suspend fun getDashboardStats(): DashboardStats

    // (이 함수는 위 getStoreMenus와 중복될 수 있으나, 용도가 다르다면 유지.
    // 보통 getStoreMenus 하나로 통일하는 게 좋습니다.)
    @GET("stores/{storeId}/menus")
    suspend fun getMenus(@Path("storeId") storeId: String): List<MenuItem>
}