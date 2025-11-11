package com.example.babful.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.babful.data.model.DeliveryItem
import com.example.babful.data.model.FeedItem
import com.example.babful.data.model.ShortsItem

// ⭐️ DB 설계도: 3개의 Entity 테이블을 포함
@Database(entities = [FeedItem::class, DeliveryItem::class, ShortsItem::class], version = 1)
abstract class BabfulDatabase : RoomDatabase() {
    // ⭐️ Hilt가 DAO를 주입할 수 있도록 추상 함수 정의
    abstract fun feedDao(): FeedDao
    abstract fun deliveryDao(): DeliveryDao
    abstract fun shortsDao(): ShortsDao
}