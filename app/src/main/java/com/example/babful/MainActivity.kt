package com.example.babful

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.babful.ui.MainViewModel
import com.example.babful.ui.SplashScreen
import com.example.babful.ui.auth.LoginScreen
import com.example.babful.ui.auth.RegisterScreen
import com.example.babful.ui.components.BottomNavigationBar
import com.example.babful.ui.components.FloatingDeliveryBar
import com.example.babful.ui.delivery.DeliveryScreen
import com.example.babful.ui.feed.FeedScreen
import com.example.babful.ui.owner.OwnerHomeScreen
import com.example.babful.ui.owner.OwnerMenuScreen
import com.example.babful.ui.owner.OwnerOrderScreen // ⭐️ [필수] 임포트 확인
import com.example.babful.ui.profile.ProfileScreen
import com.example.babful.ui.shorts.ShortsScreen
import com.example.babful.ui.store.StoreMenuScreen
import com.example.babful.ui.theme.BabfulTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel // ⭐️ FloatingBar용
import androidx.lifecycle.compose.collectAsStateWithLifecycle // ⭐️ FloatingBar용

// ⭐️ 네비게이션 경로 상수 정의 (이 파일 내에 포함)
object NavigationRoutes {
    const val SPLASH = "splash"
    const val FEED = "feed"
    const val DELIVERY = "delivery"
    const val SHORTS = "shorts"
    const val PROFILE = "profile"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ARG_STORE_ID = "storeId"
    const val STORE_MENU = "store_menu/{$ARG_STORE_ID}"

    const val OWNER_HOME = "owner_home"
    const val OWNER_MENU = "owner_menu/{storeId}"
    const val OWNER_ORDERS = "owner_orders" // ⭐️ 추가됨

    fun storeMenuRoute(storeId: String) = "store_menu/$storeId"
    fun ownerMenuRoute(storeId: Int) = "owner_menu/$storeId"
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BabfulTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // ⭐️ Floating Bar 상태 관찰
    val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    // ⭐️ 바텀바를 보여줄 화면들
    val bottomNavRoutes = listOf(
        NavigationRoutes.FEED,
        NavigationRoutes.DELIVERY,
        NavigationRoutes.SHORTS,
        NavigationRoutes.PROFILE
    )
    val shouldShowBottomBar = currentDestination?.hierarchy?.any { it.route in bottomNavRoutes } == true

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                // 기존 Material3 NavigationBar 대신 커스텀 BottomNavigationBar 사용 가능
                // 여기서는 직접 구현된 코드 사용
                NavigationBar {
                    val screens = listOf(Screen.Feed, Screen.Delivery, Screen.Shorts, Screen.Profile)
                    screens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            AppNavHost(navController = navController)

            // ⭐️ [Step 55] Floating Delivery Bar (진행 중인 주문이 있을 때만 표시)
            if (mainUiState.activeOrder != null && shouldShowBottomBar) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                ) {
                    FloatingDeliveryBar(
                        order = mainUiState.activeOrder!!,
                        onClick = {
                            // 클릭 시 프로필 화면(또는 주문 상세)으로 이동
                            navController.navigate(NavigationRoutes.PROFILE) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.SPLASH
    ) {
        // 1. 스플래시 & 인증
        composable(NavigationRoutes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(NavigationRoutes.LOGIN) { popUpTo(NavigationRoutes.SPLASH) { inclusive = true } }
                },
                onNavigateToFeed = {
                    navController.navigate(NavigationRoutes.FEED) { popUpTo(NavigationRoutes.SPLASH) { inclusive = true } }
                }
            )
        }
        composable(NavigationRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavigationRoutes.FEED) { popUpTo(0) { inclusive = true } }
                },
                onNavigateToRegister = { navController.navigate(NavigationRoutes.REGISTER) }
            )
        }
        composable(NavigationRoutes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { navController.popBackStack() }, // 회원가입 후 로그인 화면으로 복귀
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // 2. 메인 탭
        composable(NavigationRoutes.FEED) { FeedScreen() }
        composable(NavigationRoutes.DELIVERY) {
            DeliveryScreen(
                onNavigateToStore = { storeId -> navController.navigate(NavigationRoutes.storeMenuRoute(storeId)) }
            )
        }
        composable(NavigationRoutes.SHORTS) {
            ShortsScreen(
                onNavigateToStore = { storeId -> navController.navigate(NavigationRoutes.storeMenuRoute(storeId)) }
            )
        }
        composable(NavigationRoutes.PROFILE) {
            ProfileScreen(
                onNavigateToLogin = {
                    navController.navigate(NavigationRoutes.LOGIN) { popUpTo(0) { inclusive = true } }
                },
                onNavigateToOwnerMode = { navController.navigate(NavigationRoutes.OWNER_HOME) }
            )
        }

        // 3. 상세 화면
        composable(
            route = NavigationRoutes.STORE_MENU,
            arguments = listOf(navArgument(NavigationRoutes.ARG_STORE_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString(NavigationRoutes.ARG_STORE_ID)
            StoreMenuScreen(storeId = storeId)
        }

        // 4. 점주 모드
        composable(NavigationRoutes.OWNER_HOME) {
            OwnerHomeScreen(
                onNavigateToCustomerMode = { navController.popBackStack() },
                onNavigateToMenu = { storeId -> navController.navigate(NavigationRoutes.ownerMenuRoute(storeId)) },
                // ✅ 연결 완료
                onNavigateToOrders = { navController.navigate(NavigationRoutes.OWNER_ORDERS) }
            )
        }
        composable(NavigationRoutes.OWNER_MENU) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId") ?: "0"
            OwnerMenuScreen(storeId = storeId.toInt())
        }

        // ⭐️ [필수 추가] 주문 접수 화면 연결
        composable(NavigationRoutes.OWNER_ORDERS) {
            OwnerOrderScreen()
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Feed : Screen(NavigationRoutes.FEED, "피드", Icons.Default.Home)
    data object Delivery : Screen(NavigationRoutes.DELIVERY, "배달", Icons.Default.ShoppingCart)
    data object Shorts : Screen(NavigationRoutes.SHORTS, "쇼츠", Icons.Default.PlayArrow)
    data object Profile : Screen(NavigationRoutes.PROFILE, "프로필", Icons.Default.Person)
}