package com.example.appname.feed.data.remote.model

import com.example.appname.feed.domain.model.Post // (1) 🚨 Domain 모델을 import (변환용)
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * [설계 의도 요약]
 * 서버 API의 'posts' 엔드포인트 응답과 1:1로 매칭되는 데이터 전송 객체(DTO)입니다.
 * Moshi가 이 클래스를 사용하여 JSON을 파싱합니다.
 */
@JsonClass(generateAdapter = true) // (2) 🚨 Moshi가 코드를 자동 생성하도록 함
data class PostDto(
    @Json(name = "post_id") // (3) 🚨 JSON의 'post_id' 키를 'id' 변수에 매칭
    val id: Int,

    @Json(name = "user_name")
    val author: String,

    @Json(name = "post_content")
    val content: String,

    @Json(name = "image_url")
    val imageUrl: String,

    @Json(name = "is_liked_by_user")
    val isLiked: Boolean
)

/**
 * [설계 의도 요약]
 * DTO(Data Layer)를 Domain Model(Domain Layer)로 변환하는 확장 함수입니다.
 * RepositoryImpl이 이 함수를 사용하여 데이터를 '정제'합니다.
 */
fun PostDto.toDomainModel(): Post {
    return Post(
        id = this.id,
        author = this.author,
        content = this.content,
        // (4) 🚨 DTO의 imageUrl(String)을 Domain의 imageRes(Int)로 변환
        // (실제 앱에서는 Coil/Glide 라이브러리가 이 URL을 사용해 이미지를 로드함)
        // (임시로 더미 drawable을 사용)
        imageRes = com.example.appname.R.drawable.ic_launcher_background,
        isLiked = this.isLiked
    )
}