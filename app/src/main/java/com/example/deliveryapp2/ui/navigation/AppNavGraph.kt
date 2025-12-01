package com.example.deliveryapp2.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.deliveryapp2.data.network.RetrofitClient
import com.example.deliveryapp2.data.repository.CartRepository
import com.example.deliveryapp2.data.repository.NetworkDeliveryRepository
import com.example.deliveryapp2.ui.common.RoleSelectionScreen
import com.example.deliveryapp2.ui.customer.cart.CartScreen
import com.example.deliveryapp2.ui.customer.home.HomeFeedScreen
import com.example.deliveryapp2.ui.customer.order.OrderDetailScreen
import com.example.deliveryapp2.ui.customer.order.OrderListScreen
import com.example.deliveryapp2.ui.customer.order.PaymentScreen
import com.example.deliveryapp2.ui.customer.profile.ProfileScreen
import com.example.deliveryapp2.ui.customer.store.StoreDetailScreen
import com.example.deliveryapp2.ui.owner.analytics.AnalyticsScreen
import com.example.deliveryapp2.ui.owner.dashboard.DashboardScreen
import com.example.deliveryapp2.ui.owner.menu.MenuManagementScreen
import com.example.deliveryapp2.ui.owner.orders.OrderManagementScreen
import com.example.deliveryapp2.ui.owner.profile.StoreProfileScreen
import com.example.deliveryapp2.viewmodel.CustomerHomeViewModel
import com.example.deliveryapp2.viewmodel.CustomerHomeViewModelFactory
import com.example.deliveryapp2.viewmodel.OwnerOrderViewModel
import com.example.deliveryapp2.viewmodel.OwnerOrderViewModelFactory

@Composable
fun AppNavGraph(navController: NavHostController) {
    // Repository & ViewModel Factory setup
    val repository = NetworkDeliveryRepository(RetrofitClient.apiService)
    val customerViewModel: CustomerHomeViewModel = viewModel(
        factory = CustomerHomeViewModelFactory(repository)
    )
    val ownerViewModel: OwnerOrderViewModel = viewModel(
        factory = OwnerOrderViewModelFactory(repository)
    )

    // [중요] startDestination은 'role_selection' 이어야 합니다.
    // 'home'이 아닙니다. (홈 화면의 실제 ID는 'customer_home'입니다)
    NavHost(navController = navController, startDestination = "login") {
        // [추가] 로그인 화면
        composable("login") {
            com.example.deliveryapp2.ui.auth.LoginScreen(
                onLoginSuccess = { role ->
                    if (role == "OWNER") {
                        navController.navigate("owner_dashboard") { popUpTo("login") { inclusive = true } }
                    } else {
                        navController.navigate("customer_home") { popUpTo("login") { inclusive = true } }
                    }
                }
            )
        }

        // 0. Role Selection (진입점)
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
        composable("store_detail/{storeId}") { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId")
            StoreDetailScreen(
                storeId = storeId,
                onGoToCart = { navController.navigate("cart") }
            )
        }
        composable("cart") {
            CartScreen(
                onCheckoutClick = { navController.navigate("payment") }
            )
        }
        composable("payment") {
            PaymentScreen(
                totalPrice = CartRepository.getTotalPrice(),
                onPaymentSuccess = {
                    navController.navigate("customer_home") {
                        popUpTo("customer_home") { inclusive = true }
                    }
                }
            )
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
        composable("owner_orders") { OrderManagementScreen() } // ViewModel 내부 주입됨
        composable("owner_menu") { MenuManagementScreen() }
        composable("owner_profile") { StoreProfileScreen() }
        composable("owner_analytics") { AnalyticsScreen() }
    }
}