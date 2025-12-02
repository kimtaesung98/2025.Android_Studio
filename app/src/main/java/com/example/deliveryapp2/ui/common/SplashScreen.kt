package com.example.deliveryapp2.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.deliveryapp2.data.local.TokenManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToCustomer: () -> Unit,
    onNavigateToOwner: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = TokenManager(context)

    LaunchedEffect(Unit) {
        delay(1500) // 1.5초 정도 로고 보여주기 (UX)

        val token = tokenManager.getToken()
        val role = tokenManager.getUserRole()

        if (!token.isNullOrEmpty() && !role.isNullOrEmpty()) {
            // 토큰 있음 -> 역할에 따라 이동
            if (role == "OWNER") {
                onNavigateToOwner()
            } else {
                onNavigateToCustomer()
            }
        } else {
            // 토큰 없음 -> 로그인 화면으로
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 실제로는 앱 로고 이미지를 넣으면 좋습니다.
        Text("DELIVERY APP", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
        CircularProgressIndicator(modifier = Modifier.align(Alignment.BottomCenter))
    }
}