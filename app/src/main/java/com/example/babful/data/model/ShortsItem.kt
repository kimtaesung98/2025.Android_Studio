package com.example.babful.data.model // ⚠️ 기존 data/model 패키지 사용

/**
 * 쇼츠 아이템 하나를 나타내는 데이터 클래스 (Model)
 * @property id 고유 ID
 * @property videoUrl 영상 URL (지금은 안 씀)
 * @property storeName 가게 이름 (피드백 반영)
 * @property storeId 가게 ID (피드백 반영)
 */
data class ShortsItem(
    val id: String,
    val videoUrl: String? = null,
    val storeName: String,
    val storeId: String
)