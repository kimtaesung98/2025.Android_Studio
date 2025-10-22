package com.example.appname.model

import androidx.annotation.DrawableRes

data class Post(
    val id: Int,
    val author: String,
    val content: String,
    @DrawableRes val imageRes: Int, // 이미지 리소스를 담을 변수

    val isLiked: Boolean = false
)