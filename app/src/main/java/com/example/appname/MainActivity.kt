package com.example.appname

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels // 🚨 (1) [New]
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.appname.ui.screen.main.MainScreen
import com.example.appname.ui.screen.main.MainViewModel // 🚨 (1) [New]
import com.example.appname.ui.screen.main.NavGraph // 🚨 (1) [New]
import com.example.appname.ui.screen.main.AuthScreen // 🚨 (1) [New]
import com.example.appname.ui.screen.main.NavigationState // 🚨 (1) [New]
import com.example.appname.ui.theme.AppnameTheme
import com.example.appname.user.ui.screen.UserScreen // 🚨 (1) [New]
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // (2) 🚨 Activity 스코프의 MainViewModel 생성
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppnameTheme {
                // (3) 🚨 NavState에 따라 UI 분기
                val navState by mainViewModel.navState.collectAsState()

                when (navState) {
                    NavigationState.Loading -> {
                        // (4) 🚨 앱 부팅 시 로딩 화면
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    NavigationState.LoggedIn -> {
                        // (5) 🚨 로그인됨 -> 메인 그래프
                        RootNavigationGraph(startDestination = NavGraph.MAIN_GRAPH)
                    }
                    NavigationState.LoggedOut -> {
                        // (6) 🚨 로그아웃됨 -> 인증 그래프
                        RootNavigationGraph(startDestination = NavGraph.AUTH_GRAPH)
                    }
                }
            }
        }
    }
}

// (7) 🚨 [New] 최상위 네비게이션 그래프 Composable
@Composable
fun RootNavigationGraph(startDestination: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination // 👈 (8) MainViewModel이 결정한 시작점
    ) {
        // (9) 🚨 인증 그래프 (로그인 화면)
        navigation(
            startDestination = AuthScreen.LOGIN,
            route = NavGraph.AUTH_GRAPH
        ) {
            composable(AuthScreen.LOGIN) {
                // (10) 로그인 성공 시 -> 메인 그래프로 이동하고 스택 비우기
                UserScreen(
                    onLoginSuccess = {
                        navController.navigate(NavGraph.MAIN_GRAPH) {
                            popUpTo(NavGraph.AUTH_GRAPH) { inclusive = true }
                        }
                    }
                )
            }
            // TODO: 회원가입 화면 등 추가
        }

        // (11) 🚨 메인 그래프 (탭 화면)
        composable(NavGraph.MAIN_GRAPH) {
            MainScreen() // 👈 탭 + 탭 NavHost가 포함된 화면
        }
    }
}