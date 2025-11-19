package com.example.babful.data.model
import com.google.gson.annotations.SerializedName

data class Order(
    val id: Int,
    @SerializedName("user_email") val userEmail: String,
    val amount: Int,
    val status: String,
    @SerializedName("created_at") val createdAt: String
)

// ⭐️ [신규] 상태 변경 요청 Body
data class UpdateOrderStatusRequest(
    @SerializedName("order_id") val orderId: Int,
    @SerializedName("status") val status: String
)