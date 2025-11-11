package com.example.babful.data.model
import com.google.gson.annotations.SerializedName // ⭐️ [신규]

data class DeliveryItem(
    @SerializedName("id") // ⭐️ [수정]
    val id: String,

    @SerializedName("store_name") // ⭐️ Go의 json:"store_name"과 일치
    val storeName: String,

    @SerializedName("store_image_url")
    val storeImageUrl: String? = null,

    @SerializedName("estimated_time_in_minutes")
    val estimatedTimeInMinutes: Int,

    @SerializedName("status")
    val status: String = "조리중"
)