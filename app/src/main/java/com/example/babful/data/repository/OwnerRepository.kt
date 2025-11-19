package com.example.babful.data.repository
import com.example.babful.data.model.CreateMenuRequest
import com.example.babful.data.model.CreateStoreRequest
import com.example.babful.data.model.Menu
import com.example.babful.data.model.Order
import com.example.babful.data.model.OwnerStore
import com.example.babful.data.model.UpdateOrderStatusRequest
import com.example.babful.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OwnerRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getMyStore(): OwnerStore = apiService.getMyStore()

    suspend fun createStore(name: String, desc: String, lat: Double, lng: Double) {
        apiService.createMyStore(CreateStoreRequest(name, desc, lat, lng))
    }
    // ⭐️ [신규] 메뉴 관련 함수 추가
    suspend fun createMenu(storeId: Int, name: String, price: Int) {
        apiService.createMenu(CreateMenuRequest(storeId, name, price))
    }
    suspend fun getMenus(storeId: Int): List<Menu> {
        return apiService.getStoreMenus(storeId)
    }
    suspend fun getOrders(): List<Order> {
        return apiService.getOwnerOrders()
    }
    // ⭐️ [신규] 상태 변경 함수
    suspend fun updateOrderStatus(orderId: Int, status: String) {
        apiService.updateOrderStatus(UpdateOrderStatusRequest(orderId, status))
    }
}