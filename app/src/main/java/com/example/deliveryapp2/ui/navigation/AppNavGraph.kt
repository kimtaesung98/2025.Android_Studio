package com.example.deliveryapp2.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
// [중요] 리팩토링된 파일 경로들에 맞춰 import 확인
import com.example.deliveryapp2.ui.auth.LoginScreen
import com.example.deliveryapp2.ui.common.SplashScreen
import com.example.deliveryapp2.ui.customer.cart.CartScreen
import com.example.deliveryapp2.ui.customer.cart.PaymentScreen
import com.example.deliveryapp2.ui.customer.home.CustomerHomeScreen
import com.example.deliveryapp2.ui.customer.order.OrderListScreen // (없으면 주석 처리하거나 빈 화면 연결)
import com.example.deliveryapp2.ui.customer.profile.ProfileScreen // (파일 분리 안했으면 기존 경로)
import com.example.deliveryapp2.ui.customer.store.StoreDetailScreen
import com.example.deliveryapp2.ui.owner.dashboard.DashboardScreen
import com.example.deliveryapp2.ui.owner.orders.OrderManagementScreen

@Composable
fun AppNavGraph(navController: NavHostController) {

    // 시작점은 스플래시 화면
    NavHost(navController = navController, startDestination = "splash") {

        // 1. 스플래시 (자동 로그인)
        composable("splash") {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate("login") { popUpTo("splash") { inclusive = true } }
                },
                onNavigateToCustomer = {
                    navController.navigate("customer_home") { popUpTo("splash") { inclusive = true } }
                },
                onNavigateToOwner = {
                    navController.navigate("owner_dashboard") { popUpTo("splash") { inclusive = true } }
                }
            )
        }

        // 2. 로그인 화면
        composable("login") {
            LoginScreen(
                onLoginSuccess = { role ->
                    if (role == "OWNER") {
                        navController.navigate("owner_dashboard") { popUpTo("login") { inclusive = true } }
                    } else {
                        navController.navigate("customer_home") { popUpTo("login") { inclusive = true } }
                    }
                }
            )
        }

        // --- [고객] 화면 ---

        // 3. 고객 홈 (매장 목록)
        composable("customer_home") {
            CustomerHomeScreen(
                onStoreClick = { storeId ->
                    // [중요] 여기서 이동하는 경로가 아래에 정의되어 있어야 함
                    navController.navigate("menu/$storeId")
                }
            )
        }

        // 4. 매장 상세 (메뉴 담기) -> 🚨 이 부분이 없어서 에러가 난 것입니다!
        composable("menu/{storeId}") { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId") ?: return@composable
            StoreDetailScreen(
                storeId = storeId,
                onNavigateToCart = {
                    navController.navigate("cart")
                }
            )
        }

        // 👇 [이 부분이 빠져있을 겁니다! 추가해주세요]
        composable("menu/{storeId}") { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId")
            if (storeId != null) {
                com.example.deliveryapp2.ui.customer.store.StoreDetailScreen(
                    storeId = storeId,
                    onNavigateToCart = {
                        navController.navigate("cart")
                    }
                )
            }
        }
        // 5. 장바구니
        composable("cart") {
            CartScreen(
                onNavigateToPayment = {
                    navController.navigate("payment")
                }
            )
        }

        // 6. 결제 화면
        composable("payment") {
            PaymentScreen(
                onPaymentSuccess = {
                    // 결제 성공 시 주문 내역 탭으로 이동
                    navController.navigate("customer_orders") {
                        // 홈까지의 기록을 남기되, 결제 화면 등은 백스택에서 제거
                        popUpTo("customer_home") { inclusive = false }
                    }
                }
            )
        }

        // 🟢 [수정] 주문 내역 목록 (하단 탭)
        composable("customer_orders") {
            com.example.deliveryapp2.ui.customer.order.OrderListScreen(
                onOrderClick = { orderId ->
                    // [추후 구현] 상세 화면으로 이동 (Tracking)
                    // navController.navigate("order_track/$orderId")
                    // 지금은 임시로 토스트 메시지나 로그만 남겨도 됨
                }
            )
        }

        // 8. 고객 프로필 (하단 탭)
        composable("customer_profile") {
            // ProfileScreen 경로가 ui/customer/profile/CustomerProfileScreen.kt 인지 확인 필요
            // 여기서는 기존에 하나만 있던 ProfileScreen을 재사용한다고 가정
            com.example.deliveryapp2.ui.customer.profile.ProfileScreen(
                onLogout = {
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // --- [점주] 화면 ---

        composable("owner_dashboard") {
            DashboardScreen(onNavigate = { route -> navController.navigate(route) })
        }

        composable("owner_orders") {
            OrderManagementScreen()
        }

        composable("owner_menu") {
            // 메뉴 관리 화면 (없으면 임시)
            androidx.compose.material3.Text("Menu Management")
        }

        composable("owner_profile") {
            // 점주 프로필
            com.example.deliveryapp2.ui.customer.profile.ProfileScreen(
                onLogout = {
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
            )
        }
    }
}