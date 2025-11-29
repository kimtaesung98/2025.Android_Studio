package com.example.deliveryapp2.data.repository

import com.example.deliveryapp2.data.model.Store // 패키지명 주의
import kotlinx.coroutines.delay

interface StoreRepository {
    suspend fun getStoreList(): List<Store>
    suspend fun getStoreDetail(id: String): Store
}

class MockStoreRepository : StoreRepository {
    // Store 생성자의 파라미터 순서: id, name, rating, deliveryTime, minOrderPrice, imageUrl
    private val mockStores = listOf(
        Store(
            "1",
            "Burger King",
            4.5,
            "20-30 min",
            15000,
            "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=500&q=60"
        ),
        Store(
            "2",
            "Pizza Hut",
            4.2,
            "40-50 min",
            20000,
            "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3?auto=format&fit=crop&w=500&q=60"
        ),
        Store(
            "3",
            "Subway",
            4.8,
            "10-20 min",
            10000,
            "https://images.unsplash.com/photo-1556761175-b413da4baf72?auto=format&fit=crop&w=500&q=60"
        )
    )

    override suspend fun getStoreList(): List<Store> {
        delay(500)
        return mockStores
    }

    override suspend fun getStoreDetail(id: String): Store {
        delay(500)
        return mockStores.find { it.id == id } ?: mockStores.first()
    }
}