package com.example.babful.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.babful.NavigationRoutes

// 네비게이션 아이템 데이터 클래스
data class NavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun BottomNavigationBar(
    currentDestination: NavDestination?,
    onNavigate: (String) -> Unit
) {
    // 하단 탭 메뉴 정의
    val items = listOf(
        NavItem("홈", NavigationRoutes.FEED, Icons.Default.Home),
        NavItem("배달", NavigationRoutes.DELIVERY, Icons.Default.ShoppingCart), // 또는 LocationOn
        NavItem("쇼츠", NavigationRoutes.SHORTS, Icons.Default.PlayArrow),
        NavItem("프로필", NavigationRoutes.PROFILE, Icons.Default.Person)
    )

    NavigationBar {
        items.forEach { item ->
            // 현재 화면이 이 탭의 하위 경로인지 확인 (선택 상태 표시)
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(text = item.label) }
            )
        }
    }
}