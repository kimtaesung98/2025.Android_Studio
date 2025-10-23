package com.example.appname.shorts.domain.model

data class ShortsItem(
    val id: Int,
    val videoUrl: String, // 실제 비디오 스트리밍 URL
    val description: String,
    val isLiked: Boolean = false
)