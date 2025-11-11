package com.example.babful // ⭐️ [확인] Logcat이 알려준 실제 패키지명

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.babful.ui.feed.FeedScreen
import com.example.babful.ui.theme.BabfulTheme
import com.example.babful.ui.delivery.DeliveryScreen
import com.example.babful.ui.shorts.ShortsScreen
import com.example.babful.ui.store.StoreMenuScreen // ⭐️ [신규]
import androidx.navigation.NavType // ⭐️ [신규]
import androidx.navigation.navArgument // ⭐️ [신규]
import com.example.babful.ui.NavigationRoutes // ⭐️ [신규]
import dagger.hilt.android.AndroidEntryPoint
import com.example.babful.ui.feed.FeedScreen
import com.example.babful.ui.theme.BabfulTheme
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

// (MainScreen, AppNavHost, Screen, PlaceholderScreen 함수는
//  이전 단계에서 제공한 코드와 동일합니다.)

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navItems = listOf(Screen.Feed, Screen.Delivery, Screen.Shorts)
    // ⭐️ [신규] 현재 경로를 확인하여 BottomBar를 보여줄지 결정
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    // ⭐️ BottomBar에 포함된 3개 탭 경로
    val bottomNavRoutes = listOf(
        NavigationRoutes.FEED,
        NavigationRoutes.DELIVERY,
        NavigationRoutes.SHORTS
    )
    val shouldShowBottomBar = currentDestination?.route in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) { // ⭐️ 상세 화면에서는 BottomBar 숨기기
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    navItems.forEach { screen ->
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
        AppNavHost( // ⭐️ [수정] NavHostController를 AppNavHost에 전달
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

// [수정] AppNavHost : 새 경로(StoreMenu) 추가 및 이벤트 람다 전달
@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.FEED, // ⭐️ 경로 상수 사용
        modifier = modifier
    ) {
        composable(NavigationRoutes.FEED) {
            FeedScreen()
        }
        composable(NavigationRoutes.DELIVERY) {
            DeliveryScreen()
        }
        composable(NavigationRoutes.SHORTS) {
            // ⭐️ [수정] ShortsScreen에 이벤트 람다(onNavigateToStore) 전달
            ShortsScreen(
                onNavigateToStore = { storeId ->
                    // ⭐️ 실제 네비게이션 실행
                    navController.navigate(NavigationRoutes.storeMenuRoute(storeId))
                }
            )
        }

        // ⭐️ [신규] 가게 메뉴 상세 화면 경로 정의
        composable(
            route = NavigationRoutes.STORE_MENU,
            arguments = listOf(navArgument(NavigationRoutes.ARG_STORE_ID) {
                type = NavType.StringType // 인자 타입 정의
            })
        ) { backStackEntry ->
            // ⭐️ 전달받은 인자(storeId)를 꺼내서 StoreMenuScreen에 전달
            val storeId = backStackEntry.arguments?.getString(NavigationRoutes.ARG_STORE_ID)
            StoreMenuScreen(storeId = storeId)
        }
    }
}
// [수정] Screen Sealed Class (경로 상수를 사용하도록 변경)
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Feed : Screen(NavigationRoutes.FEED, "피드", Icons.Default.Home)
    object Delivery : Screen(NavigationRoutes.DELIVERY, "배달", Icons.Default.List)
    object Shorts : Screen(NavigationRoutes.SHORTS, "쇼츠", Icons.Default.PlayArrow)
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$title (개발 예정)")
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    BabfulTheme { // ⭐️ [수정] Bapful(P) -> Babful(B)
        MainScreen()
    }
}