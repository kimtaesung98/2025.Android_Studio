package com.example.appname.user.domain.model

/**
 * [설계 의도 요약]
 * 로그인 성공 시 반환될 사용자 정보 모델을 정의합니다.
 */
data class User(
    val id: String,
    val email: String,
    val nickname: String,
    val profileImageUrl: String? = null
)