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
import com.example.deliveryapp2.data.network.RetrofitClient // 추가
import com.example.deliveryapp2.data.repository.NetworkDeliveryRepository // 추가
import com.example.deliveryapp2.ui.customer.cart.CartScreen
import com.example.deliveryapp2.ui.customer.store.StoreDetailScreen
import com.example.deliveryapp2.viewmodel.CustomerHomeViewModel
import com.example.deliveryapp2.viewmodel.CustomerHomeViewModelFactory
import com.example.deliveryapp2.viewmodel.OwnerOrderViewModel
import com.example.deliveryapp2.viewmodel.OwnerOrderViewModelFactory

@Composable
fun AppNavGraph(navController: NavHostController) {
    val repository = NetworkDeliveryRepository(RetrofitClient.apiService)

    // ViewModel Factory에 주입
    val customerViewModel: CustomerHomeViewModel = viewModel(
        factory = CustomerHomeViewModelFactory(repository)
    )
    val ownerViewModel: OwnerOrderViewModel = viewModel(
        factory = OwnerOrderViewModelFactory(repository)
    )
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
            HomeFeedScreen(onStoreClick = { storeId -> navController.navigate("store_detail/$storeId") })
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
        // [추가] 메뉴 선택 화면
        composable("store_detail/{storeId}") { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId")
            StoreDetailScreen(
                storeId = storeId,
                onGoToCart = { navController.navigate("cart") }
            )
        }

// [추가] 장바구니 화면
        composable("cart") {
            CartScreen(
                onOrderComplete = {
                    // 주문 완료 시 홈으로 이동하고 백스택 정리
                    navController.navigate("customer_home") {
                        popUpTo("customer_home") { inclusive = true }
                    }
                }
            )
        }
    }
}