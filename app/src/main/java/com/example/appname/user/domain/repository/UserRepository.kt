package com.example.appname.user.domain.repository

import com.example.appname.user.domain.model.User

/**
 * [설계 의도 요약]
 * User(사용자) 데이터에 접근하기 위한 '규칙(Interface)'을 정의합니다.
 */
interface UserRepository {

    /**
     * 이메일과 비밀번호로 로그인을 시도합니다.
     * @return Result<User> - 로그인 성공 시 사용자 정보, 실패 시 에러 반환
     */
    suspend fun login(email: String, password: String): Result<User>

    // TODO: implement details (예: fun signUp(...), fun getMyProfile())
}