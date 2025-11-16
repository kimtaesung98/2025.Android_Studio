package com.example.babful.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn // ⭐️ [신규]
import androidx.compose.foundation.lazy.items // ⭐️ [신규]
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.babful.data.model.Transaction // ⭐️ [신규]
import java.text.SimpleDateFormat // ⭐️ [신규]
import java.util.Locale

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // (로그아웃 네비게이션 - 36단계와 동일)
    LaunchedEffect(uiState.navigateToLogin) {
        if (uiState.navigateToLogin) {
            onNavigateToLogin()
            viewModel.onNavigationDone()
        }
    }

    // ⭐️ [수정] 3. 전체 UI 레이아웃
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.isLoading && uiState.user == null) {
            // (최초 로딩)
            CircularProgressIndicator()
        } else if (uiState.user != null) {

            // 4. ⭐️ 프로필 정보 (이메일, 잔액)
            Text(text = "내 프로필", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = uiState.user!!.email, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "내 포인트", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = "${uiState.user!!.points} P", // ⭐️ 포인트 잔액
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))

            // 5. ⭐️ 포인트 사용 내역
            Text(text = "포인트 내역", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator() // (로그아웃 시 로딩)
            }

            if (uiState.transactions.isEmpty()) {
                Text(text = "포인트 내역이 없습니다.", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(uiState.transactions) { transaction ->
                        TransactionItem(transaction)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. ⭐️ 로그아웃 버튼 (36단계와 동일)
            Button(
                onClick = { viewModel.logout() },
                enabled = !uiState.isLoading
            ) {
                Text(text = "로그아웃")
            }
        } else {
            // (에러 발생 시)
            Text(text = "프로필을 불러오는 데 실패했습니다.")
            Button(onClick = { viewModel.loadProfileData() }) {
                Text(text = "재시도")
            }
        }
    }
}

// ⭐️ [신규] 7. 포인트 내역 아이템
@Composable
fun TransactionItem(transaction: Transaction) {
    // (날짜 포맷)
    val formatter = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.type, // (예: "사용: 500P 할인")
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = formatter.format(transaction.timestamp),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Text(
            text = "${transaction.amount} P", // (예: "-500 P")
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (transaction.amount < 0) Color.Red else MaterialTheme.colorScheme.primary
        )
    }
}