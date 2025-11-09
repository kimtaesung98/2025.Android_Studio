package com.example.babful.ui.delivery

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue // ⭐️ [신규] getValue 임포트
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle // ⭐️ [신규]
import androidx.lifecycle.viewmodel.compose.viewModel // ⭐️ [신규]
import com.example.babful.data.model.DeliveryItem
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
// ⭐️ [제거] import java.util.UUID (ViewModel로 이동)

@Composable
fun DeliveryScreen(
    // ⭐️ [신규] viewModel() 헬퍼 함수로 ViewModel 인스턴스 주입
    viewModel: DeliveryViewModel = viewModel()
) {
    // ⭐️ [신규] ViewModel의 StateFlow를 구독
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        Log.d("DeliveryScreen", "배달 화면이 띄워졌습니다 (ViewModel 적용)")
    }

    // ⭐️ [제거] 7단계에 있던 'val deliveryItems = (1..30).map { ... }' 로직 삭제
    // (이 로직은 DeliveryViewModel로 이동했음)

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "배달 현황 (VM)", // 타이틀 변경
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = 0.1f))
        ) {
            // ⭐️ [수정] 7단계의 deliveryItems 대신 viewModel의 uiState.deliveryItems 사용
            items(uiState.deliveryItems, key = { it.id }) { item ->
                DeliveryItemView(item = item)
            }
        }
    }
}


// ⭐️ (DeliveryItemView 코드는 7단계와 '완전히 동일' - 수정 불필요)
@Composable
fun DeliveryItemView(item: DeliveryItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.White, shape = MaterialTheme.shapes.medium)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ⭐️ [수정] Box -> AsyncImage (가게 썸네일)
        AsyncImage(
            model = item.storeImageUrl, // ⭐️ VM에서 받은 URL
            contentDescription = "${item.storeName} 가게 썸네일",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape) // ⭐️ 원형 자르기
                .background(Color.LightGray), // ⭐️ 로딩 중 배경색
            contentScale = ContentScale.Crop // ⭐️ 이미지 비율
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.storeName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "배달 예상: ${item.estimatedTimeInMinutes}분",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = item.status,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (item.status == "배달중") Color(0xFF008000) else Color.Blue
        )
    }
}