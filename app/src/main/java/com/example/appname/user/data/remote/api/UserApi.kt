package com.example.appname.user.data.remote.api

import com.example.appname.user.data.remote.model.LoginRequestDto
import com.example.appname.user.data.remote.model.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * [설계 의도 요약]
 * Retrofit이 사용할 'User' 관련 API 엔드포인트 명세입니다.
 * (Go 서버가 이 엔드포인트들을 구현해야 합니다.)
 */
interface UserApi {

    /**
     * (Go 서버의 POST /auth/login)
     * 이메일, 비밀번호로 로그인을 시도합니다.
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<UserDto>

    /**
     * (Go 서버의 GET /users/me)
     * 저장된 토큰(DataStore)으로 내 프로필 정보를 가져옵니다.
     */
    @GET("users/me")
    suspend fun getMyProfile(@Header("Authorization") token: String): Response<UserDto>

    /**
     * (Go 서버의 POST /auth/logout)
     * 로그아웃 (토큰 만료 처리)
     */
    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Unit>
}