package com.example.babful.ui.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.babful.data.model.Order

@Composable
fun OwnerOrderScreen(
    viewModel: OwnerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadOrders() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("주문 접수 현황", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.orders) { order ->
                OrderCard(
                    order = order,
                    onUpdateStatus = { newStatus ->
                        viewModel.updateOrderStatus(order.id, newStatus)
                    }
                )
            }
        }
    }
}

// ⭐️ [신규] 주문 카드 컴포저블 분리
@Composable
fun OrderCard(
    order: Order,
    onUpdateStatus: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("주문 #${order.id}", style = MaterialTheme.typography.titleMedium)
                // 상태 뱃지 (색상 다르게)
                Text(
                    text = order.status,
                    color = when(order.status) {
                        "접수대기" -> Color.Red
                        "조리중" -> Color(0xFFFFA500) // Orange
                        "배달중" -> Color.Blue
                        "배달완료" -> Color.Green
                        else -> Color.Gray
                    },
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("주문자: ${order.userEmail}")
            Text("결제 금액: ${order.amount}원", style = MaterialTheme.typography.bodyLarge)
            Text("일시: ${order.createdAt}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            // ⭐️ 상태별 액션 버튼
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                when (order.status) {
                    "접수대기" -> {
                        Button(onClick = { onUpdateStatus("조리중") }) { Text("주문 수락") }
                    }
                    "조리중" -> {
                        Button(onClick = { onUpdateStatus("배달중") }) { Text("배달 출발") }
                    }
                    "배달중" -> {
                        Button(onClick = { onUpdateStatus("배달완료") }) { Text("배달 완료 처리") }
                    }
                    "배달완료" -> {
                        OutlinedButton(onClick = { }, enabled = false) { Text("완료된 주문") }
                    }
                }
            }
        }
    }
}