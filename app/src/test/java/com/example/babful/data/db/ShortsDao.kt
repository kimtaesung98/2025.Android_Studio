package com.example.babful.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.babful.data.model.ShortsItem

@Dao
interface ShortsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ShortsItem>)

    @Query("SELECT * FROM shorts")
    suspend fun getAllShorts(): List<ShortsItem>

    @Query("DELETE FROM shorts")
    suspend fun clearAllShorts()
}