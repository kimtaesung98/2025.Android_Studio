package com.example.appname.user.data.repository

import com.example.appname.user.domain.model.User
import com.example.appname.user.domain.repository.UserRepository
import kotlinx.coroutines.delay

/**
 * [설계 의도 요약]
 * UserRepository 인터페이스의 실제 구현체입니다.
 * 2단계 '살 붙이기'에서 여기에 Retrofit(API) 호출 로직이 추가됩니다.
 */
class UserRepositoryImpl : UserRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        // TODO: implement details (Retrofit API 호출)

        // (임시) 1단계에서는 네트워크 통신을 흉내 내고(delay) 무조건 성공 가정
        delay(1000) // 1초 지연 (네트워크 흉내)

        // (임시) 더미 사용자 반환
        val dummyUser = User(
            id = "uid-123",
            email = email,
            nickname = "테스트 유저"
        )
        return Result.success(dummyUser)
    }
}