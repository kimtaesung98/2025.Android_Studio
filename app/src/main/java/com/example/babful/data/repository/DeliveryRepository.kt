package com.example.babful.data.repository

import com.example.babful.data.db.DeliveryDao
import com.example.babful.data.model.DeliveryItem
import com.example.babful.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeliveryRepository @Inject constructor(
    private val apiService: ApiService,
    private val deliveryDao: DeliveryDao
) {
    // 1. 로컬 캐시에서 가져오기
    suspend fun getDeliveryItemsFromCache(): List<DeliveryItem> {
        return deliveryDao.getAllDeliveries()
    }

    // 2. 네트워크에서 가져와서 캐싱하기
    suspend fun getDeliveryItemsFromNetwork(): List<DeliveryItem> {
        val items = apiService.getDeliveryItems()
        if (items.isNotEmpty()) {
            deliveryDao.clearAllDeliveries()
            deliveryDao.insertAll(items)
        }
        return items
    }

    // 🗑️ [삭제됨] getRoutePoints 함수 (지도 경로 그리기 로직 제거)
}