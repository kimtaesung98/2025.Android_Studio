package com.example.deliveryapp2.data.model

enum class OrderStatus {
    PENDING,            // 주문 접수 (대기)
    PREPARING,          // 조리 중 (점주 수락)
    READY_FOR_DELIVERY, // 조리 완료 (라이더 대기)
    ON_DELIVERY,        // 배달 중 (라이더 픽업)
    DELIVERED,          // 배달 완료
    CANCELLED;          // 취소됨

    // 다음 단계로 넘어가는 헬퍼 함수
    fun next(): OrderStatus {
        return when (this) {
            PENDING -> PREPARING
            PREPARING -> READY_FOR_DELIVERY
            READY_FOR_DELIVERY -> ON_DELIVERY
            ON_DELIVERY -> DELIVERED
            else -> this // 완료나 취소는 다음 단계 없음
        }
    }

    // UI 표시용 텍스트
    fun toUiString(): String {
        return when(this) {
            PENDING -> "Waiting"
            PREPARING -> "Cooking"
            READY_FOR_DELIVERY -> "Ready"
            ON_DELIVERY -> "Delivery"
            DELIVERED -> "Done"
            CANCELLED -> "Cancel"
        }
    }
}