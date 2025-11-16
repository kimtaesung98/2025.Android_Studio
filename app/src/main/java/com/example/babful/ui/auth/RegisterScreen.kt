package com.example.babful.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect // ⭐️ [신규]
import androidx.compose.material3.CircularProgressIndicator // ⭐️ [신규]

@Composable
fun RegisterScreen(
    // ⭐️ 1. LoginScreen과 '동일한' AuthViewModel 사용 (Hilt가 관리)
    viewModel: AuthViewModel = hiltViewModel(),
    // ⭐️ 2. NavHost로부터 '이벤트 람다' 2개 받기
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // ⭐️ [신규] ViewModel의 'navigateToLogin' 상태가 true가 되면,
    //          네비게이션(onRegisterSuccess)을 실행하고, ViewModel에 '소비'했음을 알림
    LaunchedEffect(uiState.navigateToLogin) {
        if (uiState.navigateToLogin) {
            onRegisterSuccess() // ⭐️ (NavHost의 람다 호출)
            viewModel.onNavigationDone() // ⭐️ (이벤트 소비)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "회원가입", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = { Text("이메일") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.pass,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("비밀번호") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(32.dp))

        // ⭐️ 3. 회원가입 버튼 [수정]
        Button(
            onClick = {
                viewModel.register() // ⭐️ VM에 이벤트만 알림
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading // ⭐️ 로딩 중 비활성화
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text(text = "회원가입")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // ⭐️ 4. 로그인 화면 이동 버튼
        TextButton(onClick = onNavigateToLogin, enabled = !uiState.isLoading) {
            Text(text = "로그인 화면으로 돌아가기")
        }
    }
}