package com.example.babful.data.model
import com.google.gson.annotations.SerializedName // ⭐️ [신규]

data class ShortsItem(
    @SerializedName("id") // ⭐️ [수정]
    val id: String,

    @SerializedName("video_url") // ⭐️ Go의 json:"video_url"과 일치
    val videoUrl: String? = null,

    @SerializedName("store_name")
    val storeName: String,

    @SerializedName("store_id")
    val storeId: String
)