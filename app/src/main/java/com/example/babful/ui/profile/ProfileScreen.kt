package com.example.babful.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    // ⭐️ 1. NavHost로부터 '이벤트 람다' 받기
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ⭐️ 2. ViewModel의 'navigateToLogin' 상태가 true가 되면,
    //          네비게이션(onNavigateToLogin)을 실행하고, ViewModel에 '소비'했음을 알림
    LaunchedEffect(uiState.navigateToLogin) {
        if (uiState.navigateToLogin) {
            onNavigateToLogin() // ⭐️ (NavHost의 람다 호출)
            viewModel.onNavigationDone() // ⭐️ (이벤트 소비)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ⭐️ 3. 로그아웃 버튼
        Button(
            onClick = {
                viewModel.logout() // ⭐️ VM에 이벤트만 알림
            },
            enabled = !uiState.isLoading // ⭐️ 로딩 중에는 버튼 비활성화
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text(text = "로그아웃")
            }
        }
    }
}