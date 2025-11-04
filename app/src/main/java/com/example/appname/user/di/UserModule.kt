package com.example.appname.user.di

import android.content.Context // 🚨 (1) [New]
import com.example.appname.user.data.local.UserPreferencesRepository // 🚨 (1) [New]
import com.example.appname.user.data.repository.UserRepositoryImpl
import com.example.appname.user.domain.repository.UserRepository
import com.example.appname.user.domain.usecase.CheckLoginStatusUseCase // 🚨 (1) [New]
import com.example.appname.user.domain.usecase.LoginUseCase
import com.example.appname.user.domain.usecase.LogoutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext // 🚨 (1) [New]
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.appname.user.data.remote.api.UserApi
import retrofit2.Retrofit
/**
 * [설계 의도 요약]
 * Hilt가 User 모듈의 의존성을 주입(Inject)하는 방법을 정의합니다.
 */
@Module
@InstallIn(SingletonComponent::class)
object UserModule {

    @Provides

    // 🚨 (2) [New] DataStore 래퍼(Repository)를 주입하는 레시피
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository {
        return UserPreferencesRepository(context)
    }

    @Provides
    fun provideLoginUseCase(repository: UserRepository): LoginUseCase {
        return LoginUseCase(repository)
    }
    @Provides
    fun provideLogoutUseCase(repository: UserRepository): LogoutUseCase {
        return LogoutUseCase(repository)
    }
    // 🚨 (4) [New] CheckLoginStatusUseCase 레시피 추가
    @Provides
    fun provideCheckLoginStatusUseCase(repository: UserRepository): CheckLoginStatusUseCase {
        return CheckLoginStatusUseCase(repository)
    }
    // 🚨 (3) [Update] UserRepositoryImpl이 이제 DataStore 래퍼를 필요로 함
    @Provides
    @Singleton
    fun provideUserRepository(prefs: UserPreferencesRepository): UserRepository {
        return UserRepositoryImpl(prefs)
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }
}