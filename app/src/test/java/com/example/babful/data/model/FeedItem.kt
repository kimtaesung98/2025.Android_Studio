package com.example.babful.data.model
import com.google.gson.annotations.SerializedName
import androidx.room.Entity // ⭐️ [신규]
import androidx.room.PrimaryKey // ⭐️ [신규]

@Entity(tableName = "feeds") // ⭐️ [신규] Room 테이블로 지정
data class FeedItem(
    @PrimaryKey // ⭐️ [신규] 서버 ID를 기본 키로 사용
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
    val likesCount: Int = 0,

    // ⭐️ [신규] Room DB 캐시 구분을 위한 반경 (API 응답에는 없음)
    var radius: Int = 0
)