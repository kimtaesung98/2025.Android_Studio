package com.example.appname.di

// (1) 🚨 올바른 BuildConfig Import
// 이 경로는 사용자님의 applicationId(com.example.appname)와 일치해야 합니다.
// 'okhttp3.BuildConfig' 등 다른 것을 선택하면 안 됩니다.
import com.example.appname.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // (2) 🚨 임시 베이스 URL (Go 서버의 실제 주소로 변경 필요)
    private const val BASE_URL = "https://api.example.com/"

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) { // 👈 (3) 올바른 BuildConfig 참조
                // 디버그 빌드일 때만 네트워크 로그 전체(BODY)를 출력
                HttpLoggingInterceptor.Level.BODY
            } else {
                // 릴리즈 빌드일 때는 로그를 남기지 않음
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}