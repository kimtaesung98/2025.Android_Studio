package com.example.babful.data.repository

import com.example.babful.data.model.PaymentRequest // ⭐️ [신규]
import com.example.babful.data.model.Transaction
import com.example.babful.data.model.User
import com.example.babful.data.network.ApiService
import com.example.babful.data.network.PointUseRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val apiService: ApiService,
    private val prefsRepo: UserPreferencesRepository // ⭐️ '로그아웃'을 위해 DataStore 주입
) {
    // 1. 내 정보 (포인트 잔액)
    suspend fun getProfileInfo(): User {
        return apiService.getProfileInfo()
    }

    // 2. 포인트 내역
    suspend fun getPointHistory(): List<Transaction> {
        return apiService.getPointHistory()
    }

    // 3. 로그아웃 (36단계 로직 이동)
    suspend fun logout() {
        prefsRepo.clearJwtToken()
    }
    // ⭐️ [신규] 4. '포인트 사용' API 호출 (38단계 API 연결)
    suspend fun usePoints(amount: Int, reason: String) {
        apiService.usePoints(PointUseRequest(amount = amount, reason = reason))
    }
}