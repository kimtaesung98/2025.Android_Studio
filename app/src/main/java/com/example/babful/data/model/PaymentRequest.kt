package com.example.babful.data.model
import com.google.gson.annotations.SerializedName

data class PaymentRequest(
    @SerializedName("store_id") val storeId: Int, // ⭐️ [신규]
    @SerializedName("amount_paid") val amountPaid: Int,
    @SerializedName("order_id") val orderId: String
)