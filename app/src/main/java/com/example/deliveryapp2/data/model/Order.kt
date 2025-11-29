package com.example.deliveryapp2.data.model

enum class OrderStatus {
    PENDING, COOKING, DELIVERY, COMPLETED, CANCELLED
}

data class Order(
    val id: String,
    val storeName: String,
    val items: List<String>,
    val totalPrice: Int,
    val status: OrderStatus, // Enum defined below
    val date: String
)