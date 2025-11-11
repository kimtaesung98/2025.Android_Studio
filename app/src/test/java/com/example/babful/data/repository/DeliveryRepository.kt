package com.example.babful.data.repository

import android.util.Log
import com.example.babful.data.db.DeliveryDao // ⭐️ [신규]
import com.example.babful.data.model.DeliveryItem
import com.example.babful.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeliveryRepository @Inject constructor(
    private val apiService: ApiService,
    private val deliveryDao: DeliveryDao // ⭐️ [신규] Hilt가 DAO를 주입
) {

    suspend fun getDeliveryItems(): List<DeliveryItem> {
        return try {
            // 1. (Online) 네트워크 호출
            val networkItems = apiService.getDeliveryItems()
            Log.d("DeliveryRepository", "Go API (/delivery) 호출 성공")

            // 2. 캐시 저장 (기존 캐시 삭제 후 삽입)
            deliveryDao.clearAllDeliveries()
            deliveryDao.insertAll(networkItems)

            // 3. 네트워크 데이터 반환
            networkItems
        } catch (e: Exception) {
            // 4. ⭐️ (Offline) 네트워크 실패 시
            Log.e("DeliveryRepository", "Go API 호출 실패, Room DB에서 캐시 조회", e)
            // ⭐️ Room DB에서 캐시된 데이터 반환
            deliveryDao.getAllDeliveries()
        }
    }
}