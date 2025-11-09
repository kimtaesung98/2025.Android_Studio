package com.example.babful.data.model // ⚠️ 기존 data/model 패키지 사용

/**
 * 배달 주문 아이템 하나를 나타내는 데이터 클래스 (Model)
 * @property id 고유 주문 ID
 * @property storeName 가게 이름
 * @property storeImageUrl 가게 이미지 URL
 * @property estimatedTimeInMinutes 배달 예상 시간 (분)
 * @property status 주문 상태 (예: "조리중", "배달중")
 */
data class DeliveryItem(
    val id: String,
    val storeName: String,
    val storeImageUrl: String? = null,
    val estimatedTimeInMinutes: Int,
    val status: String = "조리중" // 기본값
)