package com.example.babful.ui.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// ⭐️ [수정] 1. 11단계의 Placeholder ViewModel 대신 Hilt ViewModel 사용
@Composable
fun StoreMenuScreen(
    storeId: String?, // (NavHost가 전달)
    viewModel: StoreViewModel = hiltViewModel() // ⭐️ Hilt가 storeId를 주입해 줌
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.storeInfo != null) {
            // ⭐️ 2. 로드 성공 시 UI
            StoreMenuContent(
                storeInfo = uiState.storeInfo!!,
                onSubscribeToggle = { viewModel.toggleSubscription() } // ⭐️ VM 함수 호출
            )
        } else {
            // ⭐️ 3. 에러 발생 시 UI
            Text(
                text = "가게 정보를 불러오는 데 실패했습니다. (ID: $storeId)",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

// ⭐️ [신규] 4. 가게 정보 UI Composable
@Composable
fun StoreMenuContent(
    storeInfo: com.example.babful.data.model.StoreInfo,
    onSubscribeToggle: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 1. (임시) 배너 이미지
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "가게 배너 이미지 (임시)", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. 가게 이름
        Text(
            text = storeInfo.storeName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. ⭐️ '구독' 버튼 (토글)
        Button(
            onClick = onSubscribeToggle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            // ⭐️ '구독' 상태에 따라 색상 변경
            colors = ButtonDefaults.buttonColors(
                containerColor = if (storeInfo.isSubscribed) Color.Gray else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (storeInfo.isSubscribed) "구독중" else "구독하기"
            )
        }

        // (향후 이곳에 메뉴 목록이 들어옴)
    }
}