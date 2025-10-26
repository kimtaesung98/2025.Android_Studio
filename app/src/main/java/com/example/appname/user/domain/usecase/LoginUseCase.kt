package com.example.appname.user.domain.usecase

import com.example.appname.user.domain.model.User
import com.example.appname.user.domain.repository.UserRepository

/**
 * [설계 의도 요약]
 * "로그인한다"는 단일 비즈니스 로직(UseCase)을 캡슐화합니다.
 */
class LoginUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // TODO: implement details
        // 2단계 '살 붙이기'에서 이메일 형식 검증, 비밀번호 유효성 검사 등
        // '비즈니스 로직'을 여기에 추가할 수 있습니다.
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("이메일과 비밀번호를 입력해주세요."))
        }
        return repository.login(email, password)
    }
}