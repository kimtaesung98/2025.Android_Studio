package com.example.babful.data.network

import android.util.Log
import com.example.babful.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    // ⭐️ Hilt가 DataStore 래퍼(UserPrefsRepo)를 주입
    private val prefsRepo: UserPreferencesRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // 1. ⭐️ (중요) DataStore에서 '동기적'으로 토큰을 가져옴
        // (Interceptor는 비동기(suspend)를 지원하지 않으므로, runBlocking 사용)
        val token = runBlocking {
            prefsRepo.jwtToken.first() // ⭐️ DataStore에서 현재 토큰 읽기
        }

        // 2. 가로챈 원본 요청(chain.request())을 가져옴
        val originalRequest = chain.request()

        // 3. ⭐️ 토큰이 '있을' 경우에만 헤더를 추가한 새 요청을 만듦
        if (!token.isNullOrBlank()) {
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token") // ⭐️ "Bearer [TOKEN]" 헤더 추가
                .build()

            Log.d("AuthInterceptor", "JWT 토큰 헤더 추가 완료")
            return chain.proceed(newRequest) // ⭐️ '수정된 요청'으로 서버에 전송
        }

        // 4. ⭐️ 토큰이 '없으면' (예: 로그인/회원가입 요청 시) 원본 요청 그대로 전송
        Log.d("AuthInterceptor", "JWT 토큰 없음. (로그인/회원가입 요청)")
        return chain.proceed(originalRequest)
    }
}