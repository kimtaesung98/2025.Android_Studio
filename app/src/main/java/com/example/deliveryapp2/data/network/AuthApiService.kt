package com.example.deliveryapp2.data.network

import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val role: String,
    val address: String // 추가
)

data class AuthResponse(
    val success: Boolean,
    val token: String?,
    val role: String?,
    val name: String?,
    val address: String?, // 추가 (로그인 응답에서 받음)
    val error: String?
)

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse
}