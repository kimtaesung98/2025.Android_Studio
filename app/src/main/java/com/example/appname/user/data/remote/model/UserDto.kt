package com.example.appname.user.data.remote.model

import com.example.appname.user.domain.model.User
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * [설계 의도 요약]
 * 서버의 'login' 또는 'profile' 응답과 매칭되는 DTO
 */
@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "user_id")
    val id: String,

    @Json(name = "email_address")
    val email: String,

    @Json(name = "nickname")
    val nickname: String,

    @Json(name = "auth_token")
    val token: String // 🚨 로그인 성공 시 서버가 발행하는 토큰
)

/**
 * [설계 의도 요약]
 * 'login' API 요청 시 Body에 담을 DTO
 */
@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    val email: String,
    val password: String
)

/**
 * [설계 의도 요약]
 * DTO(Network)를 Domain Model로 변환합니다.
 */
fun UserDto.toDomainModel(): User {
    return User(
        id = this.id,
        email = this.email,
        nickname = this.nickname
        // (참고: token은 DataStore에 저장되고 Domain Model에는 포함되지 않음)
    )
}