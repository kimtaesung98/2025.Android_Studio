package com.example.deliveryapp2.data.model

data class DashboardStats(
    val totalSales: Int,
    val totalOrders: Int,
    val pendingOrders: Int,
    val processingOrders: Int
)