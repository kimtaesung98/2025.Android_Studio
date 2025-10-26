package com.example.appname.user.domain.repository

import com.example.appname.user.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun logout(): Result<Boolean>

    // 🚨 (1) [New] 현재 저장된 인증 토큰(또는 ID)을 Flow로 가져오기
    fun getAuthTokenFlow(): Flow<String?>

    // 🚨 (2) [New] 로그인 성공/실패 시 인증 토큰을 저장/삭제
    suspend fun saveAuthToken(token: String?)

    // 🚨 (3) [New] 토큰으로 실제 사용자 정보 가져오기 (앱 시작 시)
    suspend fun getUserProfile(token: String): Result<User>
}