package com.example.babful.data.model
import com.google.gson.annotations.SerializedName // ⭐️ [신규]

data class FeedItem(
    @SerializedName("id") // ⭐️ [수정] JSON Key 매핑
    val id: String,

    @SerializedName("user_name") // ⭐️ Go의 json:"user_name"과 일치
    val userName: String,

    @SerializedName("user_profile_image_url")
    val userProfileImageUrl: String? = null,

    @SerializedName("post_image_url")
    val postImageUrl: String? = null,

    @SerializedName("content")
    val content: String,

    @SerializedName("likes_count")
    val likesCount: Int = 0
)