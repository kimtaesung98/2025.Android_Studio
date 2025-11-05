package com.example.appname.shorts.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ShortsItemDto(
    val id: Int,
    @Json(name = "video_url")
    val videoUrl: String,
    val title: String,
    @Json(name = "is_liked")
    val isLiked: Boolean
)

@JsonClass(generateAdapter = true)
data class ShortsCommentDto(
    val id: String,
    val author: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class CommentRequestDto(
    val content: String
)