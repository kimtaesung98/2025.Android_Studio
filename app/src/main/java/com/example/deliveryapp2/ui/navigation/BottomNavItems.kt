package com.example.deliveryapp2.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {

    // --- 고객용 탭 ---
    object CustomerHome : BottomNavItem("Home", Icons.Default.Home, "customer_home")
    object CustomerCart : BottomNavItem("Cart", Icons.Default.ShoppingCart, "cart")
    object CustomerOrders : BottomNavItem("Orders", Icons.Default.List, "customer_orders")
    object CustomerProfile : BottomNavItem("Config", Icons.Default.Person, "customer_profile")

    // --- 점주용 탭 ---
    object OwnerDashboard : BottomNavItem("Home", Icons.Default.Dashboard, "owner_dashboard")
    object OwnerOrders : BottomNavItem("Orders", Icons.Default.ReceiptLong, "owner_orders")
    object OwnerMenu : BottomNavItem("Menu", Icons.Default.RestaurantMenu, "owner_menu")
    object OwnerProfile : BottomNavItem("Config", Icons.Default.Store, "owner_profile")
}

// 리스트로 묶어서 관리
val customerTabs = listOf(
    BottomNavItem.CustomerHome,
    BottomNavItem.CustomerCart,
    BottomNavItem.CustomerOrders,
    BottomNavItem.CustomerProfile
)

val ownerTabs = listOf(
    BottomNavItem.OwnerDashboard,
    BottomNavItem.OwnerOrders,
    BottomNavItem.OwnerMenu,
    BottomNavItem.OwnerProfile
)