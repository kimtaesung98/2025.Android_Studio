package com.example.babful.ui.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OwnerOrderScreen(
    viewModel: OwnerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadOrders() // ⭐️ 화면 진입 시 주문 로드
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("접수된 주문", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.orders.isEmpty()) {
            Text("아직 들어온 주문이 없습니다.")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.orders) { order ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("주문자: ${order.userEmail}")
                        Text("결제 금액: ${order.amount}원", style = MaterialTheme.typography.titleMedium)
                        Text("상태: ${order.status}", color = Color.Blue)
                        Text("일시: ${order.createdAt}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}