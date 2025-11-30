package com.example.deliveryapp2.ui.owner.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deliveryapp2.data.model.Order
import com.example.deliveryapp2.data.model.OrderStatus
import com.example.deliveryapp2.data.network.RetrofitClient
import com.example.deliveryapp2.data.network.WebSocketManager
import com.example.deliveryapp2.data.repository.NetworkDeliveryRepository
import com.example.deliveryapp2.viewmodel.OwnerOrderViewModel
import com.example.deliveryapp2.viewmodel.OwnerOrderViewModelFactory

@Composable
fun OrderManagementScreen(
    viewModel: OwnerOrderViewModel = viewModel(
        factory = OwnerOrderViewModelFactory(
            NetworkDeliveryRepository(RetrofitClient.apiService)
        )
    )
) {
    val orders by viewModel.orders.collectAsState()

    // 실시간 업데이트 (WebSocket)
    LaunchedEffect(Unit) {
        viewModel.loadOrders()
        WebSocketManager.eventFlow.collect { eventType ->
            if (eventType == "NEW_ORDER" || eventType == "STATUS_UPDATE") {
                viewModel.loadOrders()
            }
        }
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Pending", "Processing", "Done")

    Column(modifier = Modifier.fillMaxSize()) {
        // 상단 바
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text("Order Management", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = { viewModel.loadOrders() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }

        // 탭 바
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        // 🟢 [핵심 수정] 6단계 상태를 3개 탭으로 분류하는 로직
        val filteredOrders = orders.filter { order ->
            when(selectedTab) {
                0 -> order.status == OrderStatus.PENDING // [Tab 1] 대기중

                1 -> order.status == OrderStatus.PREPARING ||
                        order.status == OrderStatus.READY_FOR_DELIVERY ||
                        order.status == OrderStatus.ON_DELIVERY // [Tab 2] 진행중 (조리~배달)

                else -> order.status == OrderStatus.DELIVERED ||
                        order.status == OrderStatus.CANCELLED // [Tab 3] 완료/취소
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredOrders.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text("No orders in this tab.", color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
            items(filteredOrders) { order ->
                OrderCardItem(order = order, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun OrderCardItem(order: Order, viewModel: OwnerOrderViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (order.status == OrderStatus.DELIVERED)
                MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 상단: 주문번호 및 상태 뱃지
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Order #${order.id.takeLast(4)}", style = MaterialTheme.typography.titleMedium)
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                    color = when(order.status) {
                        OrderStatus.PENDING -> androidx.compose.ui.graphics.Color(0xFFFF9800) // 주황
                        OrderStatus.DELIVERED -> androidx.compose.ui.graphics.Color(0xFF4CAF50) // 초록
                        OrderStatus.CANCELLED -> androidx.compose.ui.graphics.Color.Gray
                        else -> MaterialTheme.colorScheme.primary // 파랑 (진행중)
                    }
                ) {
                    Text(
                        text = order.status.toUiString(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = androidx.compose.ui.graphics.Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 메뉴 목록 (Null Safety 적용)
            val itemsText = if (order.items.isNotEmpty()) order.items.joinToString(", ") else "No Items Info"
            Text(itemsText, style = MaterialTheme.typography.bodyMedium)

            Text("${order.totalPrice} won", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)

            Spacer(modifier = Modifier.height(16.dp))

            // 🟢 [상태별 버튼 로직] - 다음 단계로 진행하는 버튼들
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                when (order.status) {
                    OrderStatus.PENDING -> {
                        Button(
                            onClick = { viewModel.updateStatus(order.id, OrderStatus.PREPARING) },
                            modifier = Modifier.weight(1f)
                        ) { Text("Accept") }
                        OutlinedButton(
                            onClick = { viewModel.updateStatus(order.id, OrderStatus.CANCELLED) },
                            modifier = Modifier.weight(1f)
                        ) { Text("Reject") }
                    }

                    OrderStatus.PREPARING -> {
                        Button(
                            onClick = { viewModel.updateStatus(order.id, OrderStatus.READY_FOR_DELIVERY) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                        ) { Text("Cooking Done (Call Rider)") }
                    }

                    OrderStatus.READY_FOR_DELIVERY -> {
                        Button(
                            onClick = { viewModel.updateStatus(order.id, OrderStatus.ON_DELIVERY) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF03A9F4))
                        ) { Text("Rider Picked Up") }
                    }

                    OrderStatus.ON_DELIVERY -> {
                        Button(
                            onClick = { viewModel.updateStatus(order.id, OrderStatus.DELIVERED) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF4CAF50))
                        ) { Text("Complete Delivery") }
                    }

                    else -> { /* 완료/취소 상태는 버튼 없음 */ }
                }
            }
        }
    }
}