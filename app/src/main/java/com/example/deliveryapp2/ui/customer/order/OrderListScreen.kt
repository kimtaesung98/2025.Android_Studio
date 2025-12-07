package com.example.deliveryapp2.ui.customer.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deliveryapp2.data.model.Order
import com.example.deliveryapp2.data.model.OrderStatus
import com.example.deliveryapp2.data.network.RetrofitClient
import com.example.deliveryapp2.data.repository.NetworkDeliveryRepository
import com.example.deliveryapp2.viewmodel.CustomerOrderViewModel
import com.example.deliveryapp2.viewmodel.CustomerOrderViewModelFactory

@Composable
fun OrderListScreen(
    onOrderClick: (String) -> Unit // 상세 화면으로 이동하기 위한 콜백
) {
    val repository = NetworkDeliveryRepository(RetrofitClient.apiService) // 리팩토링된 생성자 사용 (apiService -> api)
    // 주의: 만약 RetrofitClient.apiService가 빨간줄이면, 이전에 수정한 Repository 생성자에 맞춰주세요.
    // 여기서는 RetrofitClient.apiService 객체를 그대로 넘깁니다.

    val viewModel: CustomerOrderViewModel = viewModel(
        factory = CustomerOrderViewModelFactory(repository)
    )

    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("My Orders", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = { viewModel.loadMyOrders() }) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No orders yet.", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    CustomerOrderCard(order = order, onClick = { onOrderClick(order.id) })
                }
            }
        }
    }
}

@Composable
fun CustomerOrderCard(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(order.storeName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                // 상태 뱃지
                val badgeColor = when (order.status) {
                    OrderStatus.PENDING -> Color(0xFFFF9800) // 주황
                    OrderStatus.DELIVERED -> Color(0xFF4CAF50) // 초록
                    OrderStatus.CANCELLED -> Color.Red
                    else -> Color(0xFF2196F3) // 파랑 (나머지 상태: ACCEPTED, COOKING 등)
                }

                Surface(
                    color = badgeColor.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    // 🟢 [수정] 텍스트로 표시할 때는 .name을 사용
                    Text(
                        text = order.status.name,
                        color = badgeColor,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Date: ${order.date}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (order.items.isNotEmpty()) order.items.joinToString(", ") else "No items info",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("${order.totalPrice} won", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        }
    }
}