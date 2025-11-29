package com.example.deliveryapp2.data.model

data class Store(
    val id: String,
    val name: String,
    val rating: Double,
    val deliveryTime: String,
    val minOrderPrice: Int,
    val imageUrl: String = "" // 기본값 설정
)