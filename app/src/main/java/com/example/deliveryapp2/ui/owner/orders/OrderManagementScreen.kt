package com.example.deliveryapp2.ui.owner.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh // 아이콘 추가
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deliveryapp2.viewmodel.OwnerOrderViewModel
import com.example.deliveryapp2.data.model.Order // Import 확인

@Composable
fun OrderManagementScreen() {
    // ViewModel 주입 (Factory 설정이 되어 있다고 가정)
    // *주의: MainActivity나 NavGraph에서 Factory를 통해 주입받는 것이 정석입니다.
    // 여기서는 편의상 viewModel() 호출 (Factory 필요 시 이전 단계 코드 참고)
    val viewModel: OwnerOrderViewModel = viewModel()
    val orders by viewModel.orders.collectAsState()

    // LaunchedEffect: 화면 진입 시 자동 로드
    LaunchedEffect(Unit) {
        viewModel.loadOrders()
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Pending", "Processing", "Done")

    Column(modifier = Modifier.fillMaxSize()) {
        // [추가] 상단 타이틀 및 새로고침 버튼
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

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        // 탭 필터링 로직 (서버 데이터 status 문자열과 매칭 필요)
        // 서버의 status는 "PENDING", "COOKING", "COMPLETED" 등임
        val filteredOrders = orders.filter { order ->
            when(selectedTab) {
                0 -> order.status.name == "PENDING"
                1 -> order.status.name == "COOKING" || order.status.name == "DELIVERY"
                else -> order.status.name == "COMPLETED" || order.status.name == "CANCELLED"
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredOrders.isEmpty()) {
                item { Text("No orders in this status.") }
            }
            items(filteredOrders) { order ->
                // 기존 OrderActionCard 사용 (파라미터 타입이 맞는지 확인 필요)
                // OwnerOrder(가짜 데이터) 대신 실제 Order 데이터 모델을 사용하도록 카드도 수정해야 함.
                // 편의상 아래에 간단한 카드 인라인 구현:
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Order #${order.id}", style = MaterialTheme.typography.titleMedium)
                        Text(order.storeName) // 실제 데이터엔 storeName이 없을 수도 있음 (서버 응답 확인)
                        Text("${order.totalPrice} won", color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))

                        // 버튼 로직
                        if (order.status.name == "PENDING") {
                            Button(onClick = {
                                viewModel.updateStatus(order.id, com.example.deliveryapp2.data.model.OrderStatus.COOKING)
                            }) {
                                Text("Accept Order")
                            }
                        } else if (order.status.name == "COOKING") {
                            Button(onClick = {
                                viewModel.updateStatus(order.id, com.example.deliveryapp2.data.model.OrderStatus.COMPLETED)
                            }) {
                                Text("Complete Delivery")
                            }
                        }
                    }
                }
            }
        }
    }
}