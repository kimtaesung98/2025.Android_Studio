package com.example.appname.ui.screen.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar // 🚨 (1) M3의 NavigationBar 사용
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.appname.delivery.ui.screen.DeliveryScreen
import com.example.appname.feed.ui.screen.FeedScreen
import com.example.appname.shorts.ui.screen.ShortsScreen
import com.example.appname.user.ui.screen.UserScreen

// (2) 🚨 탭에 표시될 화면들 (TabScreen 모델만 사용)
sealed class TabScreen(val route: String, val icon: ImageVector, val title: String) {
    object Delivery : TabScreen("delivery", Icons.Default.List, "배달")
    object Feed : TabScreen("feed", Icons.Default.Home, "피드")
    object Shorts : TabScreen("shorts", Icons.Default.PlayArrow, "쇼츠")
    object Profile : TabScreen("profile", Icons.Default.Person, "프로필")
}

// (3) 🚨 네비게이션 그래프(흐름) 정의 (MainActivity가 사용)
object NavGraph {
    const val AUTH_GRAPH = "auth_graph"
    const val MAIN_GRAPH = "main_graph"
}

// (4) 🚨 인증 화면 라우트 정의 (MainActivity가 사용)
object AuthScreen {
    const val LOGIN = "login"
}

/**
 * [설계 의도]
 * 4개의 탭을 가진 메인 화면(Scaffold)을 정의합니다.
 * 이 Composable은 MainActivity의 RootNavigationGraph에 의해 호출됩니다.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    // (5) 🚨 NavController는 rememberNavController()로 자체 생성
    mainNavController: NavHostController = rememberNavController()
) {
    val items = listOf(
        TabScreen.Delivery,
        TabScreen.Feed,
        TabScreen.Shorts,
        TabScreen.Profile
    )

    Scaffold(
        bottomBar = {
            // (6) 🚨 M3의 NavigationBar 사용
            NavigationBar {
                val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            mainNavController.navigate(screen.route) {
                                popUpTo(mainNavController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding -> // 🚨 (7) Scaffold의 Padding을 NavHost에 적용 (필수)

        // (8) 🚨 메인 탭 화면 전용 내부 NavHost
        NavHost(
            navController = mainNavController,
            startDestination = TabScreen.Feed.route,
            modifier = Modifier.padding(innerPadding) // 🚨 Padding 적용
        ) {
            composable(TabScreen.Delivery.route) { DeliveryScreen() }
            composable(TabScreen.Feed.route) { FeedScreen() }
            composable(TabScreen.Shorts.route) { ShortsScreen() }
            composable(TabScreen.Profile.route) { UserScreen() }
        }
    }
}

// 🚨 (9) 붙여넣으신 코드에 있던 BottomNavigationBar()와 NavigationGraph() 함수는
// 모두 MainScreen()으로 통합되었으므로 이 파일에서 삭제합니다.