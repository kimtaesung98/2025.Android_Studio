package com.example.babful.data.repository

import com.example.babful.data.model.PaymentRequest
import com.example.babful.data.model.StoreInfo
import com.example.babful.data.network.ApiService
import com.example.babful.data.network.SubscribeRequest
import javax.inject.Inject
import javax.inject.Singleton
import com.example.babful.data.model.Menu // ⭐️ [신규]
@Singleton
class StoreRepository @Inject constructor(
    private val apiService: ApiService
    // (이 예제에서는 StoreDao (Room)는 사용하지 않음)
) {
    suspend fun getStoreInfo(storeId: String): StoreInfo {
        // ⭐️ Go 서버로부터 isSubscribed가 포함된 가게 정보를 받아옴
        return apiService.getStoreInfo(storeId)
    }

    suspend fun subscribeStore(storeId: String) {
        apiService.subscribeStore(SubscribeRequest(storeId = storeId))
    }

    suspend fun unsubscribeStore(storeId: String) {
        apiService.unsubscribeStore(SubscribeRequest(storeId = storeId))
    }

    // ⭐️ [수정] storeId 인자 추가
    suspend fun processPayment(storeId: Int, amountPaid: Int, orderId: String) {
        apiService.processPayment(
            PaymentRequest(
                storeId = storeId,
                amountPaid = amountPaid,
                orderId = orderId
            )
        )
    }

    // ⭐️ [신규] 가게 메뉴 목록 가져오기
    suspend fun getMenus(storeId: String): List<Menu> {
        // API는 Int를 받으므로 형변환 (실제 앱에선 String/Int 통일 필요)
        // 여기서는 간단히 toInt() 사용 (ID가 숫자인 경우만 동작)
        return try {
            apiService.getStoreMenus(storeId.toInt())
        } catch (e: NumberFormatException) {
            emptyList() // ID가 "store_1" 같은 문자열이면 빈 목록 반환 (예외처리)
        }
    }
}