package com.example.babful.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SplashScreen(
    viewModel: MainViewModel = hiltViewModel(),
    // ⭐️ 1. NavHost로부터 '이벤트 람다' 2개 받기
    onNavigateToLogin: () -> Unit,
    onNavigateToFeed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 2. UI는 로딩 스피너만 표시
        CircularProgressIndicator()
    }

    // 3. ⭐️ UI 상태 변경 감지
    LaunchedEffect(uiState.isLoading) {
        // 4. ⭐️ 로딩이 완료되었을 때 (isLoading = false)
        if (!uiState.isLoading) {
            // 5. ⭐️ 상태에 따라 분기
            if (uiState.isLoggedIn) {
                onNavigateToFeed() // (토큰 있음) -> 피드로 이동
            } else {
                onNavigateToLogin() // (토큰 없음) -> 로그인으로 이동
            }
        }
    }
}