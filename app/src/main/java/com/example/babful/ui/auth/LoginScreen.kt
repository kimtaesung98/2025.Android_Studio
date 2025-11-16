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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.CircularProgressIndicator // ⭐️ [신규]
@Composable
fun LoginScreen(
    // ⭐️ 1. Hilt로 ViewModel 주입
    viewModel: AuthViewModel = hiltViewModel(),
    // ⭐️ 2. NavHost로부터 '이벤트 람다' 2개 받기
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // ⭐️ 3. ViewModel의 UI 상태 구독
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // ⭐️ [신규] ViewModel의 'navigateToFeed' 상태가 true가 되면,
    //          네비게이션(onLoginSuccess)을 실행하고, ViewModel에 '소비'했음을 알림
    LaunchedEffect(uiState.navigateToFeed) {
        if (uiState.navigateToFeed) {
            onLoginSuccess() // ⭐️ (NavHost의 람다 호출)
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
        Text(text = "밥풀", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        // ⭐️ 4. 이메일 입력 (State <-> VM 연동)
        OutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChange(it) }, // VM 함수 호출
            label = { Text("이메일") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // ⭐️ 5. 패스워드 입력 (State <-> VM 연동)
        OutlinedTextField(
            value = uiState.pass,
            onValueChange = { viewModel.onPasswordChange(it) }, // VM 함수 호출
            label = { Text("비밀번호") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation() // 비밀번호 가리기
        )
        Spacer(modifier = Modifier.height(32.dp))

        // ⭐️ 6. 로그인 버튼
        Button(
            onClick = {
                viewModel.login() // ⭐️ VM에 이벤트만 알림 (네비게이션은 LaunchedEffect가 담당)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading // ⭐️ 로딩 중에는 버튼 비활성화
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text(text = "로그인")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // ⭐️ 7. 회원가입 이동 버튼
        TextButton(onClick = onNavigateToRegister) { // NavHost에 이동 이벤트 알림
            Text(text = "회원가입")
        }
    }
}