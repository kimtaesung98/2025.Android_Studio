package com.example.babful.data.model // ⚠️ 신규 패키지

/**
 * 피드 아이템 하나를 나타내는 데이터 클래스 (Model)
 * @property id 고유 식별자
 * @property userName 사용자 이름
 * @property userProfileImageUrl 사용자 프로필 이미지 URL (지금은 안 쓰지만 확장용)
 * @property postImageUrl 피드 이미지 URL (지금은 안 쓰지만 확장용)
 * @property content 글 내용
 * @property likesCount 좋아요 수
 */
data class FeedItem(
    val id: String,
    val userName: String,
    val userProfileImageUrl: String? = null, // null이 가능
    val postImageUrl: String? = null,
    val content: String,
    val likesCount: Int = 0 // 기본값 0
)