package com.example.appname.feed.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.appname.feed.domain.model.Post // (1) 🚨 Domain 모델 import (변환용)

/**
 * [설계 의도 요약]
 * Room 데이터베이스의 'posts' 테이블과 매핑되는 Entity(개체)입니다.
 * SSOT 원칙에 따라 이 Entity가 로컬 저장소의 기준이 됩니다.
 */
@Entity(tableName = "posts") // (2) 🚨 'posts'라는 테이블명 정의
data class PostEntity(
    @PrimaryKey // (3) 🚨 기본키(Primary Key) 지정
    val id: Int,

    val author: String,
    val content: String,
    val imageRes: Int, // (참고: DTO는 imageUrl(String)이었음)
    val isLiked: Boolean
)

/**
 * [설계 의도 요약]
 * DB Entity(Data Layer)를 Domain Model(Domain Layer)로 변환하는 확장 함수입니다.
 * RepositoryImpl이 Room에서 데이터를 꺼내 UseCase로 보낼 때 사용합니다.
 */
fun PostEntity.toDomainModel(): Post {
    return Post(
        id = this.id,
        author = this.author,
        content = this.content,
        imageRes = this.imageRes,
        isLiked = this.isLiked
    )
}