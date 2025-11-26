package com.example.babful.ui.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.babful.data.model.Order

@Composable
fun OwnerOrderScreen(
    viewModel: OwnerOrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("접수대기", "진행중", "완료")

    LaunchedEffect(Unit) { viewModel.loadOrders() }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        // 탭에 따른 필터링 로직
        val filteredOrders = when (selectedTabIndex) {
            0 -> uiState.orders.filter { it.status == "접수대기" }
            1 -> uiState.orders.filter { it.status == "조리중" || it.status == "배달중" }
            else -> uiState.orders.filter { it.status == "배달완료" }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredOrders) { order ->
                OwnerOrderCard(
                    order = order,
                    onUpdateStatus = { status -> viewModel.updateStatus(order.id, status) }
                )
            }
        }
    }
}

@Composable
fun OwnerOrderCard(order: Order, onUpdateStatus: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("주문 #${order.id}", fontWeight = FontWeight.Bold)
                Text(order.status, color = getStatusColor(order.status), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("메뉴: ${order.amount}원 (상세내역 생략)")
            Text("주문자: ${order.userEmail}", color = Color.Gray, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // 상태별 버튼
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                when (order.status) {
                    "접수대기" -> Button(onClick = { onUpdateStatus("조리중") }) { Text("주문 수락") }
                    "조리중" -> Button(onClick = { onUpdateStatus("배달중") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000))) { Text("배달 출발") }
                    "배달중" -> Button(onClick = { onUpdateStatus("배달완료") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))) { Text("배달 완료") }
                }
            }
        }
    }
}

fun getStatusColor(status: String): Color {
    return when(status) {
        "접수대기" -> Color.Red
        "조리중" -> Color(0xFFFFA000)
        "배달중" -> Color.Blue
        "배달완료" -> Color.Green
        else -> Color.Black
    }
}