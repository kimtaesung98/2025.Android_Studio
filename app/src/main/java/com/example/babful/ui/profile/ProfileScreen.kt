package com.example.babful.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn // ⭐️ [신규]
import androidx.compose.foundation.lazy.items // ⭐️ [신규]
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
    onNavigateToLogin: (String) -> Unit, // ⭐️ Role을 인자로 받음
    onNavigateToOwnerMode: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadProfileInfo() }

    // 로그아웃 상태일 때 -> 로그인 선택 화면 표시
    if (uiState.navigateToLogin || uiState.user == null) {
        if (!uiState.isLoading) {
            LoginSelectionScreen(onNavigateToLogin = onNavigateToLogin)
        }
        return
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
            // ⭐️ [신규] 사장님 모드 전환 버튼 (Role이 owner일 때만 표시하거나, 누구나 전환 가능하게 할 수도 있음)
            // 여기서는 테스트를 위해 누구나 전환 가능하게 하거나, role 체크 후 표시
            if (uiState.user?.role == "owner") {
                Button(
                    onClick = onNavigateToOwnerMode,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(text = "사장님 모드로 전환")
                }
                Spacer(modifier = Modifier.height(16.dp))
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

// ⭐️ [신규] 로그인 역할 선택 화면 (UseCase: 사용자 친숙 UX)
@Composable
fun LoginSelectionScreen(onNavigateToLogin: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("반갑습니다! 👋", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("어떤 분이신가요?", fontSize = 16.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(48.dp))

        // 고객용 버튼
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clickable { onNavigateToLogin("customer") }, // ⭐️ customer 전달
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)) // 파란색 계열
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1565C0), modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("손님으로 시작하기", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1565C0))
                    Text("맛있는 음식을 주문할게요", fontSize = 14.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 점주용 버튼
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clickable { onNavigateToLogin("owner") }, // ⭐️ owner 전달
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)) // 초록색 계열
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Home, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("사장님으로 시작하기", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2E7D32))
                    Text("내 가게를 관리할게요", fontSize = 14.sp, color = Color.Gray)
                }
            }
        }
    }
}