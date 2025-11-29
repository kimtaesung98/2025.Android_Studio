package com.example.deliveryapp2.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.deliveryapp2.ui.customer.home.HomeFeedScreen
import com.example.deliveryapp2.ui.customer.order.OrderDetailScreen
import com.example.deliveryapp2.ui.customer.order.OrderListScreen
import com.example.deliveryapp2.ui.customer.profile.ProfileScreen
import com.example.deliveryapp2.ui.owner.dashboard.DashboardScreen
import com.example.deliveryapp2.ui.owner.orders.OrderManagementScreen
import com.example.deliveryapp2.ui.owner.menu.MenuManagementScreen
import com.example.deliveryapp2.ui.owner.profile.StoreProfileScreen
import com.example.deliveryapp2.ui.owner.analytics.AnalyticsScreen
import com.example.deliveryapp2.ui.common.RoleSelectionScreen // 아래 정의

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "role_selection") {

        // 0. Role Selection (Entry Point)
        composable("role_selection") {
            RoleSelectionScreen(
                onCustomerClick = { navController.navigate("customer_home") },
                onOwnerClick = { navController.navigate("owner_dashboard") }
            )
        }

        // --- Customer Routes ---
        composable("customer_home") {
            HomeFeedScreen(onStoreClick = { /* Navigate logic */ })
        }
        composable("customer_orders") {
            OrderListScreen(onOrderClick = { orderId -> navController.navigate("customer_order_detail/$orderId") })
        }
        composable("customer_order_detail/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
            OrderDetailScreen(orderId)
        }
        composable("customer_profile") { ProfileScreen() }

        // --- Owner Routes ---
        composable("owner_dashboard") {
            DashboardScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("owner_orders") { OrderManagementScreen() }
        composable("owner_menu") { MenuManagementScreen() }
        composable("owner_profile") { StoreProfileScreen() }
        composable("owner_analytics") { AnalyticsScreen() }
    }
}