package com.example.babful.data.model
import com.google.gson.annotations.SerializedName
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deliveries") // ⭐️ [신규] Room 테이블로 지정
data class DeliveryItem(
    @PrimaryKey // ⭐️ [신규] 서버 ID를 기본 키로 사용
    @SerializedName("id")
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