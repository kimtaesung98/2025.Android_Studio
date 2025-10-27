package com.example.appname.user.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.appname.user.ui.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
/**
 * [설계 의도 요약]
 * 2단계: 로그인/회원가입 UI를 표시합니다.
 * ViewModel의 UiState를 구독하고, 사용자 이벤트를 ViewModel로 전달합니다.
 */
@Composable
fun UserScreen(
    // (1) 🚨 Hilt를 통해 ViewModel을 자동으로 주입받음
    userViewModel: UserViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    val uiState by userViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // (2) 🚨 ViewModel의 일회성 이벤트를 구독하여 Toast 메시지 표시
    LaunchedEffect(key1 = true) {
        userViewModel.eventFlow.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(uiState.loginUser) {
        if (uiState.loginUser != null) {
            onLoginSuccess() // 👈 메인 그래프로 이동!
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
// 🚨 (2) [New] 로그인 상태가 변경되면 콜백 호출
        if (uiState.loginUser != null) {
            // (3) 🚨 '프로필' 탭에서 보여줄 UI (로그아웃 버튼 등)
            Text(text = "${uiState.loginUser!!.nickname}님, 환영합니다.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* (4) 🚨 로그아웃 콜백도 필요함 */ },
                // ...
            ) { Text("로그아웃") }
        } else {
            // 로그인 전
            OutlinedTextField(
                value = uiState.emailText,
                onValueChange = { userViewModel.onEmailChanged(it) },
                label = { Text("이메일") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.passwordText,
                onValueChange = { userViewModel.onPasswordChanged(it) },
                label = { Text("비밀번호") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // (4) 🚨 로딩 상태에 따라 버튼 또는 인디케이터 표시
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { userViewModel.onLoginClicked() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("로그인")
                }
            }
        }
    }
}