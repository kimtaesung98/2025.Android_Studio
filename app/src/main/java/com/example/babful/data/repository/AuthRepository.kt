package com.example.babful.data.repository

import com.example.babful.data.network.ApiService
import com.example.babful.data.network.AuthRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService, // ⭐️ Go 서버 통신
    private val prefsRepo: UserPreferencesRepository // ⭐️ 토큰 저장소
) {

    // 1. 로그인
    suspend fun login(email: String, pass: String) {
        // 1. Go 서버에 로그인 요청
        val request = AuthRequest(email, pass)
        val response = apiService.login(request) // ⭐️ 토큰 수신

        // 2. DataStore에 토큰 저장
        prefsRepo.saveJwtToken(response.token)
    }

    // 2. 회원가입
    suspend fun register(email: String, pass: String) {
        val request = AuthRequest(email, pass)
        apiService.register(request) // ⭐️ 성공 시 201, 실패 시 예외 발생
    }
}