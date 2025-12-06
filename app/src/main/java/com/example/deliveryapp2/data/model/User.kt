package com.example.deliveryapp2.data.model

data class User(
    val id: String,
    val name: String,
    val phone: String,
    val addressList: List<String>
)