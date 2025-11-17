package com.example.babful.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "deliveries")
data class DeliveryItem(
    @PrimaryKey
    @SerializedName("id")
    val id: String,

    @SerializedName("store_name")
    val storeName: String,

    @SerializedName("store_image_url")
    val storeImageUrl: String? = null,

    @SerializedName("estimated_time_in_minutes")
    val estimatedTimeInMinutes: Int? = 0,

    @SerializedName("status")
    val status: String = "조리중",

    // ⭐️ [신규] 위도/경도 추가 (지도 표시용)
    @SerializedName("lat")
    val lat: Double = 0.0,

    @SerializedName("lng")
    val lng: Double = 0.0
)