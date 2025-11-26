package com.example.babful.data.repository

import com.example.babful.data.model.ActiveOrder // ⭐️ Import 확인
import com.example.babful.data.model.Order
import com.example.babful.data.model.MySubscription
import com.example.babful.data.model.Transaction
import com.example.babful.data.model.User
import com.example.babful.data.network.ApiService
import com.example.babful.data.store.UserPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {
    // 내 정보 가져오기
    suspend fun getProfileInfo(): User {
        return apiService.getProfileInfo()
    }

    // 로그아웃
    suspend fun logout() {
        userPreferences.clearAuthToken()
    }

    // 내 주문 내역
    suspend fun getMyOrders(): List<Order> {
        return try {
            apiService.getMyOrders()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 내 구독 목록
    suspend fun getMySubscriptions(): List<MySubscription> {
        return try {
            apiService.getMySubscriptions()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 포인트 내역
    suspend fun getPointHistory(): List<Transaction> {
        return try {
            apiService.getPointHistory()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ⭐️ [요청하신 부분] 진행 중인 주문 가져오기
    suspend fun getActiveOrder(): ActiveOrder? {
        return try {
            apiService.getActiveOrder()
        } catch (e: Exception) {
            null // 에러 발생 시(또는 주문 없을 시) null 반환
        }
    }
}