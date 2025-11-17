package com.example.babful.data.repository

import android.util.Log
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

    /**
     * [신규] 1. 네트워크에서 배달 데이터를 가져오고, Room에 캐시
     */
    suspend fun getDeliveryItemsFromNetwork(): List<DeliveryItem> {
        return try {
            val networkItems = apiService.getDeliveryItems()
            Log.d("DeliveryRepository", "[SWR] Go API (/delivery) 호출 성공")

            deliveryDao.clearAllDeliveries()
            deliveryDao.insertAll(networkItems)

            networkItems
        } catch (e: Exception) {
            Log.e("DeliveryRepository", "[SWR] Go API 호출 실패", e)
            throw e // ⭐️ 실패 시 ViewModel이 알 수 있도록 예외를 다시 던짐
        }
    }

    /**
     * [신규] 2. Room DB(캐시)에서만 배달 데이터를 가져옴
     */
    suspend fun getDeliveryItemsFromCache(): List<DeliveryItem> {
        Log.d("DeliveryRepository", "[SWR] Room DB 캐시 조회")
        return deliveryDao.getAllDeliveries()
    }

    // ⭐️ [신규] 경로 데이터 가져오기
    suspend fun getRoutePoints(startLat: Double, startLng: Double, endLat: Double, endLng: Double): String? {
        val origin = "$startLat,$startLng"
        val dest = "$endLat,$endLng"

        val response = apiService.getDirections(origin, dest)

        // 첫 번째 경로의 포인트 문자열 반환 (없으면 null)
        return response.routes.firstOrNull()?.overviewPolyline?.points
    }
}