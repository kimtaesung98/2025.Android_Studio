package com.example.babful.data.repository

import com.example.babful.data.model.StoreInfo
import com.example.babful.data.network.ApiService
import com.example.babful.data.network.SubscribeRequest
import javax.inject.Inject
import javax.inject.Singleton

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
}