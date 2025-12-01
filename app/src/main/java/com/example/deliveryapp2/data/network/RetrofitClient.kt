package com.example.deliveryapp2.data.network

import android.content.Context
import com.example.deliveryapp2.data.local.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Singleton이지만 초기화가 필요한 구조로 변경
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private var retrofit: Retrofit? = null

    // 앱 시작 시(MainActivity) 한 번 호출해줘야 함
    fun init(context: Context) {
        if (retrofit == null) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // [추가] AuthInterceptor 연결
            val authInterceptor = AuthInterceptor(TokenManager(context))

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor) // ✅ 토큰 자동 첨부
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    // 초기화 안됐으면 에러 방지용 (안전장치)
    private fun getClient(): Retrofit {
        return retrofit ?: throw IllegalStateException("RetrofitClient must be initialized in MainActivity!")
    }

    val apiService: DeliveryApiService get() = getClient().create(DeliveryApiService::class.java)
    val authService: AuthApiService get() = getClient().create(AuthApiService::class.java)
}