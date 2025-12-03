package com.example.deliveryapp2.data.repository

import com.example.deliveryapp2.data.model.*
import kotlinx.coroutines.delay

interface DeliveryRepository {
    suspend fun getStores(): List<Store>
    suspend fun getOrders(): List<Order>
    suspend fun getOwnerOrders(): List<Order>
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus)
}

// MOCK Implementation
class MockDeliveryRepository : DeliveryRepository {

    // In-memory mock data
    private val mockStores = listOf(
        Store("1", "Burger King", 4.8, "20-30 min", 15000),
        Store("2", "Pizza Hut", 4.5, "40-50 min", 20000),
        Store("3", "Kyochon Chicken", 4.9, "30-40 min", 18000)
    )

    private val mockOrders = mutableListOf(
        Order("101", "Burger King", listOf("Whopper Set"), 8900, OrderStatus.PREPARING, "2023-10-25","강남점"),
        Order("102", "Pizza Hut", listOf("Cheese Pizza", "Coke"), 24000, OrderStatus.READY_FOR_DELIVERY, "2023-10-24","선릉점")
    )

    override suspend fun getStores(): List<Store> {
        delay(500) // Simulate network delay
        return mockStores
    }

    override suspend fun getOrders(): List<Order> {
        delay(500)
        return mockOrders
    }

    override suspend fun getOwnerOrders(): List<Order> {
        delay(500)
        // Owner sees all orders in this mock
        return mockOrders
    }

    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus) {
        delay(300)
        val index = mockOrders.indexOfFirst { it.id == orderId }
        if (index != -1) {
            mockOrders[index] = mockOrders[index].copy(status = status)
        }
    }
}