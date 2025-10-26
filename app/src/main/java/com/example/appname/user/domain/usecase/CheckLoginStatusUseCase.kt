package com.example.appname.user.domain.usecase

import com.example.appname.user.domain.model.User
import com.example.appname.user.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * [설계 의도 요약]
 * "앱 시작 시 로그인 상태를 확인한다"는 단일 비즈니스 로직(UseCase)을 캡슐화합니다.
 */
class CheckLoginStatusUseCase @Inject constructor( // 🚨 (1) Hilt가 주입할 수 있도록 @Inject 추가
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<User> {
        // (1) DataStore에서 저장된 토큰을 한 번만 가져옴
        val token = repository.getAuthTokenFlow().first()

        if (token.isNullOrBlank()) {
            return Result.failure(Exception("저장된 토큰이 없음"))
        }

        // (2) 토큰이 있다면, 해당 토큰으로 프로필 정보를 요청
        return repository.getUserProfile(token)
    }
}