package com.example.babful.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Transaction(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("amount") val amount: Int,
    @SerializedName("type") val type: String,
    @SerializedName("timestamp") val timestamp: Date,
    @SerializedName("order_id") val orderId: String? // ⭐️ [신규] (Nullable)
)