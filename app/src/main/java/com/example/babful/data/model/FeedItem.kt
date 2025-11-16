package com.example.babful.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "feeds")
data class FeedItem(
    @PrimaryKey
    @SerializedName("id")
    val id: String,

    @SerializedName("user_name")
    val userName: String,

    @SerializedName("user_profile_image_url")
    val userProfileImageUrl: String? = null,

    @SerializedName("post_image_url")
    val postImageUrl: String? = null,

    @SerializedName("content")
    val content: String,

    @SerializedName("likes_count")
    val likesCount: Int? = 0, // 33단계 (Nullable Fix)

    var radius: Int = 0
    // ⭐️ 'isLiked'는 Room 생성자에 없습니다.
) {
    @SerializedName("is_liked") // ⭐️ Go 서버의 `json:"is_liked"`와 이름 매칭
    @Ignore // ⭐️ Room은 이 필드를 무시
    var isLiked: Boolean = false // ⭐️ Gson이 이 값을 (true/false)로 채워줌
}