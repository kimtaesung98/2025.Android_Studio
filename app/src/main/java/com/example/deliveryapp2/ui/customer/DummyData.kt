package com.example.deliveryapp2.ui.customer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class DummyStore(val id: String, val name: String, val rating: Double, val time: String)
data class DummyOrder(val id: String, val storeName: String, val status: String, val price: String)

val dummyStores = listOf(
    DummyStore("1", "Burger King", 4.8, "20-30 min"),
    DummyStore("2", "Pizza Hut", 4.5, "30-40 min"),
    DummyStore("3", "Starbucks", 4.9, "10-15 min")
)

val dummyOrders = listOf(
    DummyOrder("101", "Burger King", "Cooking", "15,000 won"),
    DummyOrder("102", "Pizza Hut", "Completed", "22,000 won")
)