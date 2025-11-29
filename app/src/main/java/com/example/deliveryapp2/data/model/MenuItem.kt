package com.example.deliveryapp2.data.model

data class MenuItem(
    val id: String,
    val storeId: String,
    val name: String,
    val price: Int,
    val description: String,
    val imageUrl: String? = null
)