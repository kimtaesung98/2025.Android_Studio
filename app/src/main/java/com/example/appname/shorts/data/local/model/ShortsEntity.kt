package com.example.appname.shorts.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.appname.shorts.domain.model.ShortsItem

/**
 * [설계 의도 요약]
 * Room 데이터베이스의 'shorts' 테이블과 매핑되는 Entity입니다.
 */
@Entity(tableName = "shorts")
data class ShortsEntity(
    @PrimaryKey
    val id: Int,
    val videoUrl: String,
    val title: String,
    val isLiked: Boolean
)

/**
 * [설계 의도 요약]
 * DB Entity(Data Layer)를 Domain Model(Domain Layer)로 변환합니다.
 */
fun ShortsEntity.toDomainModel(): ShortsItem {
    return ShortsItem(
        id = this.id,
        videoUrl = this.videoUrl,
        title = this.title,
        isLiked = this.isLiked
    )
}

/**
 * [설계 의도 요약]
 * (DTO -> Entity 변환도 미리 추가합니다)
 * DTO(Network)를 Entity(DB)로 변환합니다.
 */
fun com.example.appname.shorts.data.remote.model.ShortsItemDto.toEntity(): ShortsEntity {
    return ShortsEntity(
        id = this.id,
        videoUrl = this.videoUrl,
        title = this.title,
        isLiked = this.isLiked
    )
}