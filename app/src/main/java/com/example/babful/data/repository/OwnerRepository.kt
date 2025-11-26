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
    // 내 가게 정보 조회
    suspend fun getMyStore(): OwnerStore {
        return apiService.getMyStore()
    }

    // 가게 등록
    suspend fun createStore(name: String, desc: String) {
        // 좌표는 강남역 예시 고정
        val request = CreateStoreRequest(name, desc, 37.4979, 127.0276)
        apiService.createMyStore(request)
    }

    // 메뉴 관련
    suspend fun createMenu(storeId: Int, name: String, price: Int) {
        apiService.createMenu(CreateMenuRequest(storeId, name, price))
    }

    suspend fun getMenus(storeId: Int): List<Menu> {
        return apiService.getStoreMenus(storeId)
    }

    // 주문 관련
    suspend fun getOrders(): List<Order> {
        return apiService.getOwnerOrders()
    }

    suspend fun updateOrderStatus(orderId: Int, status: String) {
        apiService.updateOrderStatus(UpdateOrderStatusRequest(orderId, status))
    }
}