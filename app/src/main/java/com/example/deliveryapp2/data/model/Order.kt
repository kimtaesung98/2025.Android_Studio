package com.example.deliveryapp2.data.model

data class Order(
    val id: String,
    val storeName: String,
    // [수정] items가 null일 경우 빈 리스트로 초기화하도록 설정
    val items: List<String> = emptyList(),
    val totalPrice: Int,
    val status: OrderStatus,
    val date: String
)