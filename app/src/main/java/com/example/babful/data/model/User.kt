package com.example.babful.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("points") val points: Int,
    @SerializedName("role") val role: String = "customer" // ⭐️ [신규]
)