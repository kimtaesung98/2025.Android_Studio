package com.example.appname.shorts.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.appname.shorts.domain.model.ShortsItem
import com.example.appname.shorts.data.remote.model.ShortsItemDto // (toEntity용)

@Entity(tableName = "shorts")
data class ShortsEntity(
    @PrimaryKey
    val id: Int,
    val videoUrl: String,
    val title: String,
    val isLiked: Boolean
)
fun ShortsEntity.toDomainModel(): ShortsItem {
    return ShortsItem(
        id = this.id,
        videoUrl = this.videoUrl,
        title = this.title,
        isLiked = this.isLiked
    )
}
fun ShortsItemDto.toEntity(): ShortsEntity {
    return ShortsEntity(
        id = this.id,
        videoUrl = this.videoUrl,
        title = this.title,
        isLiked = this.isLiked
    )
}