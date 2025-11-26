package com.example.babful.ui.delivery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.babful.data.model.ActiveOrder
import com.example.babful.data.model.DeliveryItem
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class) // ⭐️ [추가] 이 어노테이션을 함수 위에 추가합니다.
@Composable
fun DeliveryScreen(
    viewModel: DeliveryViewModel = hiltViewModel(),
    onNavigateToStore: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("배달 주문", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "새로고침")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ⭐️ 1. 현재 주문 현황 (있을 때만 표시)
            item {
                if (uiState.activeOrder != null) {
                    OrderStatusCard(order = uiState.activeOrder!!)
                } else {
                    // 주문이 없을 때 안내 카드
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Box(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("현재 진행 중인 주문이 없습니다.", color = Color.Gray)
                        }
                    }
                }
            }

            // ⭐️ 2. 맛집 리스트 헤더
            item {
                Text(
                    text = "등록된 맛집 목록",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // ⭐️ 3. 맛집 리스트 아이템
            if (uiState.isLoading && uiState.deliveryItems.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                items(uiState.deliveryItems) { item ->
                    DeliveryStoreItem(
                        item = item,
                        onClick = { onNavigateToStore(item.id) }
                    )
                }
            }
        }
    }
}

// 📦 컴포넌트: 주문 상태 카드 (스테퍼 UI)
@Composable
fun OrderStatusCard(order: ActiveOrder) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("주문 진행 상황", fontSize = 14.sp, color = Color.Gray)
                Text(order.storeName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 진행 단계 표시 (Progress)
            val currentStep = when (order.status) {
                "접수대기" -> 1
                "조리중" -> 2
                "배달중" -> 3
                "배달완료" -> 4
                else -> 0
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepItem(step = 1, label = "접수", currentStep = currentStep, isLast = false)
                StepItem(step = 2, label = "조리", currentStep = currentStep, isLast = false)
                StepItem(step = 3, label = "배달", currentStep = currentStep, isLast = true)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 현재 상태 메시지
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                val message = when (order.status) {
                    "접수대기" -> "사장님이 주문을 확인하고 있습니다."
                    "조리중" -> "맛있게 조리하고 있습니다! 🍳"
                    "배달중" -> "기사님이 배달을 시작했습니다! 🛵"
                    "배달완료" -> "배달이 완료되었습니다. 맛있게 드세요!"
                    else -> "주문 상태를 확인 중입니다."
                }
                Text(message, color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RowScope.StepItem(step: Int, label: String, currentStep: Int, isLast: Boolean) {
    val isActive = step <= currentStep
    val color = if (isActive) MaterialTheme.colorScheme.primary else Color.LightGray

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
    ) {
        // 원형 아이콘
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(step.toString(), color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 12.sp, color = if(isActive) Color.Black else Color.Gray, fontWeight = if(isActive) FontWeight.Bold else FontWeight.Normal)
    }

    // 연결 선
    if (!isLast) {
        Divider(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.Top)
                .padding(top = 16.dp), // 원형 아이콘 중간 높이
            color = if (step < currentStep) MaterialTheme.colorScheme.primary else Color.LightGray,
            thickness = 2.dp
        )
    }
}

// 📦 컴포넌트: 가게 리스트 아이템
@Composable
fun DeliveryStoreItem(item: DeliveryItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.storeImageUrl ?: "https://picsum.photos/100",
                contentDescription = null,
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = item.storeName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if(item.status == "영업중") "영업중 • 배달가능" else "준비중",
                    color = if(item.status == "영업중") Color.Blue else Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}