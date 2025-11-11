package com.example.babful.data.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // ⭐️ 이 모듈을 앱 전체(Singleton) 범위에서 사용
object NetworkModule {

    // ⭐️ [핵심] Go 서버 접속용 Base URL (에뮬레이터의 localhost)
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // 1. Hilt에게 Retrofit 인스턴스 생성법을 알려줌
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Gson '번역기' 장착
            .build()
    }

    // 2. Hilt에게 ApiService 인스턴스 생성법을 알려줌
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        // Retrofit이 ApiService 인터페이스를 구현(implement)해 줌
        return retrofit.create(ApiService::class.java)
    }
}