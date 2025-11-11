package com.example.babful.data.model
import com.google.gson.annotations.SerializedName
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shorts") // ⭐️ [신규] Room 테이블로 지정
data class ShortsItem(
    @PrimaryKey // ⭐️ [신규] 서버 ID를 기본 키로 사용
    @SerializedName("id")
    val id: String,

    @SerializedName("video_url") // ⭐️ Go의 json:"video_url"과 일치
    val videoUrl: String? = null,

    @SerializedName("store_name")
    val storeName: String,

    @SerializedName("store_id")
    val storeId: String
)