package com.example.babful.data.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient // ⭐️ [신규]
import okhttp3.logging.HttpLoggingInterceptor // ⭐️ [신규]
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:8080/" // ⭐️ (사용자님 8080 포트 확인)

    // ⭐️ [신규] 1. AuthInterceptor를 주입받는 OkHttpClient 생성
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor // ⭐️ Hilt가 3번에서 만든 Interceptor 주입
    ): OkHttpClient {
        // (네트워크 로그 확인을 위한 로깅 인터셉터)
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // ⭐️ [핵심] JWT 인증 인터셉터 장착
            .addInterceptor(logging) // ⭐️ (선택) 네트워크 요청/응답 로그 보기
            .build()
    }

    // ⭐️ [수정] 2. Hilt에게 Retrofit 인스턴스 생성법을 알려줌
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient // ⭐️ [수정] 기본 OkHttp 대신 1번에서 만든 Client 주입
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // ⭐️ [수정] Interceptor가 장착된 Client 사용
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ⭐️ 3. Hilt에게 ApiService 인스턴스 생성법을 알려줌
    // (이 코드는 19단계와 동일 - 수정 없음)
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}