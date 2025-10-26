package com.example.appname.shorts.domain.model

/**
 * [설계 의도 요약]
 * 쇼츠 하단 시트에 표시될 댓글 하나의 데이터 모델을 정의합니다.
 */
data class ShortsComment(
    val id: String,
    val shortsId: Int,
    val author: String,
    val content: String
)