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
import com.example.babful.data.model.StoreInfo
import com.example.babful.data.model.User // ⭐️ [신규]
@Composable
fun StoreMenuScreen(
    storeId: String?, // (NavHost가 전달)
    viewModel: StoreViewModel = hiltViewModel() // ⭐️ Hilt가 storeId를 주입해 줌
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading && uiState.storeInfo == null) { // (최초 로딩)
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.storeInfo != null && uiState.user != null) {
            // ⭐️ 2. 로드 성공 시 UI
            StoreMenuContent(
                storeInfo = uiState.storeInfo!!,
                user = uiState.user!!,
                isLoading = uiState.isLoading, // (결제 시 로딩)
                onSubscribeToggle = { viewModel.toggleSubscription() },
                onCompleteOrder = { viewModel.completeOrder(it) } // ⭐️ [수정]
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

// ⭐️ [수정] 4. 가게 정보 UI Composable
@Composable
fun StoreMenuContent(
    storeInfo: StoreInfo,
    user: User,
    isLoading: Boolean,
    onSubscribeToggle: () -> Unit,
    onCompleteOrder: (Int) -> Unit // ⭐️ [수정] (결제 금액)
) {
    // ⭐️ (임시) 상품 가격
    val itemPrice = 10000

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

        // ⭐️ [수정] 4. 주문/결제 섹션
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text = "주문하기", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // 4-1. 상품 가격
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "상품 가격", fontSize = 16.sp)
                Text(text = "$itemPrice 원", fontSize = 16.sp)
            }

            // ⭐️ [제거] 4-2. 포인트 할인 (제거)
            // ⭐️ [제거] 4-3. 최종 결제 금액 (제거)

            Spacer(modifier = Modifier.height(24.dp))

            // 4-4. '내 포인트' 표시
            Text(text = "내 보유 포인트: ${user.points} P", fontSize = 14.sp, color = Color.Gray)

            // ⭐️ [수정] 4-5. '결제/적립' 버튼
            Button(
                onClick = { onCompleteOrder(itemPrice) }, // ⭐️ 결제 금액(10000원) 전달
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading // ⭐️ 로딩(결제/적립) 중 비활성화
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "$itemPrice 원 결제하고 1% 적립")
                }
            }
        }
    }
}