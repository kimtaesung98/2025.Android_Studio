package com.example.appname.ui.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource // R 클래스 접근을 위해 필요
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.appname.R // R 클래스 접근을 위해 필요
import com.example.appname.delivery.ui.screen.DeliveryScreen
import com.example.appname.feed.ui.screen.FeedScreen
import com.example.appname.shorts.ui.screen.ShortsScreen

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home // (기존)
import androidx.compose.material.icons.filled.List // (기존)
import androidx.compose.material.icons.filled.Person // 🚨 (1) '프로필' 아이콘 import
import androidx.compose.material.icons.filled.PlayArrow // (기존)
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.rememberNavController
import com.example.appname.feed.ui.screen.FeedScreen
import com.example.appname.shorts.ui.screen.ShortsScreen
import com.example.appname.user.ui.screen.UserScreen // 🚨 (2) UserScreen import
// (1) 네비게이션 경로와 아이콘, 라벨을 정의하는 Sealed Class
sealed class BottomNavItem(
    val route: String,
    val icon: Int,
    val label: String
) {
    object Delivery : BottomNavItem("delivery", R.drawable.ic_launcher_foreground, "배달")
    object Feed : BottomNavItem("feed", R.drawable.ic_launcher_foreground, "피드")
    object Shorts : BottomNavItem("shorts", R.drawable.ic_launcher_foreground, "쇼츠")
}

// (2) 🚨 탭에 표시될 화면들
sealed class TabScreen(val route: String, val icon: ImageVector, val title: String) {
    object Delivery : TabScreen("delivery", Icons.Default.List, "배달")
    object Feed : TabScreen("feed", Icons.Default.Home, "피드")
    object Shorts : TabScreen("shorts", Icons.Default.PlayArrow, "쇼츠")
    object Profile : TabScreen("profile", Icons.Default.Person, "프로필")
}

// (3) 🚨 네비게이션 그래프(흐름) 정의
object NavGraph {
    const val AUTH_GRAPH = "auth_graph" // 로그인 흐름
    const val MAIN_GRAPH = "main_graph" // 메인 탭 흐름
}

object AuthScreen {
    const val LOGIN = "login" // 로그인 화면 라우트
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    // (4) 🚨 MainScreen은 이제 메인 탭 NavHost를 위한 NavController를 받습니다.
    mainNavController: NavHostController = rememberNavController()
) {
    // (5) 🚨 4개의 탭 아이템
    val items = listOf(
        TabScreen.Delivery,
        TabScreen.Feed,
        TabScreen.Shorts,
        TabScreen.Profile
    )

    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) }, // 🚨 route 대신 title 사용
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
    ) {
        // (6) 🚨 NavHost가 MainScreen 내부로 이동 (메인 탭 전용)
        NavHost(navController = mainNavController, startDestination = TabScreen.Feed.route) {
            composable(TabScreen.Delivery.route) { DeliveryScreen() }
            composable(TabScreen.Feed.route) { FeedScreen() }
            composable(TabScreen.Shorts.route) { ShortsScreen() }
            composable(TabScreen.Profile.route) { UserScreen() } // 👈 (7) UserScreen이 로그인/프로필 역할 겸임
        }
    }
}

// (3) 하단 네비게이션 바 Composable
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Delivery,
        BottomNavItem.Feed,
        BottomNavItem.Shorts
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // 스택 관리를 위한 옵션
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(painter = painterResource(id = item.icon), contentDescription = item.label) },
                label = { Text(text = item.label) }
            )
        }
    }
}

// (4) 네비게이션 그래프(화면 맵) Composable
@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = BottomNavItem.Delivery.route) {
        composable(BottomNavItem.Delivery.route) {
            DeliveryScreen()
        }
        composable(BottomNavItem.Feed.route) {
            FeedScreen()
        }
        composable(BottomNavItem.Shorts.route) {
            ShortsScreen()
        }
    }
}