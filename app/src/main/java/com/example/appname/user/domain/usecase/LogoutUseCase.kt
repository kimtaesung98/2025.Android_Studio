package com.example.appname.user.domain.usecase

import com.example.appname.user.domain.repository.UserRepository

/**
 * [설계 의도 요약]
 * "로그아웃한다"는 단일 비즈니스 로직(UseCase)을 캡슐화합니다.
 */
class LogoutUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        // TODO: implement details (예: 로컬 토큰 삭제 전 검증)
        return repository.logout()
    }
}