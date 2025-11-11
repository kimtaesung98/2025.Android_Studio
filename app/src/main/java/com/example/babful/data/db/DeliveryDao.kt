package com.example.babful.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.babful.data.model.DeliveryItem

@Dao
interface DeliveryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<DeliveryItem>)

    @Query("SELECT * FROM deliveries")
    suspend fun getAllDeliveries(): List<DeliveryItem>

    @Query("DELETE FROM deliveries")
    suspend fun clearAllDeliveries()
}