package com.example.appname.feed.domain.model

/**
 * [설계 의도 요약]
 * 게시물 하위에 표시될 댓글 하나의 데이터 모델을 정의합니다.
 */
data class Comment(
    val id: String,
    val postId: Int,
    val author: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)