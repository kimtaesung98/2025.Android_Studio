package com.example.babful.data.model
import com.google.gson.annotations.SerializedName

data class Order(
    val id: Int,
    @SerializedName("user_email") val userEmail: String,
    val amount: Int,
    val status: String,
    @SerializedName("created_at") val createdAt: String
)