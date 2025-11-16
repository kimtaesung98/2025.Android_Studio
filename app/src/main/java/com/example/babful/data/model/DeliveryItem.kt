package com.example.babful.data.model
import com.google.gson.annotations.SerializedName
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deliveries")
data class DeliveryItem(
    @PrimaryKey
    @SerializedName("id")
    val id: String,

    @SerializedName("store_name")
    val storeName: String,

    @SerializedName("store_image_url")
    val storeImageUrl: String? = null, // ⭐️ (Nullable 확인)

    @SerializedName("estimated_time_in_minutes")
    val estimatedTimeInMinutes: Int? = 0, // ⭐️ [필수 확인] Int -> Int? (nullable)

    @SerializedName("status")
    val status: String = "조리중"
)