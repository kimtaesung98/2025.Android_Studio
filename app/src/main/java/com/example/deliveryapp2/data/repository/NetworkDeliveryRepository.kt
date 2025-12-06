package com.example.deliveryapp2.data.repository // 패키지명 확인 (deliveryapp2)

import android.util.Log
import com.example.deliveryapp2.data.model.DashboardStats
import com.example.deliveryapp2.data.model.MenuItem
import com.example.deliveryapp2.data.model.Order
import com.example.deliveryapp2.data.model.OrderStatus
import com.example.deliveryapp2.data.model.Store
import com.example.deliveryapp2.data.network.DeliveryApiService
import com.example.deliveryapp2.data.network.RetrofitClient.apiService
import com.example.deliveryapp2.data.network.StatusUpdate // [중요] Import 추가

class NetworkDeliveryRepository(
    private val api: DeliveryApiService
) : DeliveryRepository {

    private val mockRepo = MockDeliveryRepository()

    override suspend fun getStores(): List<Store> {
        return try {
            api.getStores()
        } catch (e: Exception) {
            Log.e("NetworkRepo", "Error fetching stores", e)
            mockRepo.getStores()
        }
    }

    override suspend fun getOrders(): List<Order> {
        return try {
            api.getMyOrders()
        } catch (e: Exception) {
            mockRepo.getOrders()
        }
    }

    override suspend fun getOwnerOrders(): List<Order> {
        return try {
            api.getIncomingOrders()
        } catch (e: Exception) {
            mockRepo.getOwnerOrders()
        }
    }
    
    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus) {
        try {
            // [수정된 부분]
            // 에러 원인: api.updateOrderStatus(orderId, status.name) <- String 전송함
            // 해결 방법: StatusUpdate 객체로 감싸서 전송
            api.updateOrderStatus(orderId, StatusUpdate(status.name))

        } catch (e: Exception) {
            Log.e("NetworkRepo", "Error updating status", e)
            mockRepo.updateOrderStatus(orderId, status)
        }
    }
    suspend fun getDashboardStats(): DashboardStats {
        return apiService.getDashboardStats()
    }
    suspend fun getMenus(storeId: String): List<MenuItem> = apiService.getMenus(storeId)
}