package com.example.deliveryapp2.ui.customer.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.deliveryapp2.data.model.OrderStatus

@Composable
fun OrderDetailScreen(orderId: String?) {
    // 실제 앱에서는 여기서 ViewModel을 통해 orderId로 주문 정보를 실시간 조회해야 합니다.
    // 현재는 UI 데모를 위해 가짜 상태를 사용하여 시연합니다.

    // [Demo] 3초마다 상태가 바뀌는 시뮬레이션 (실제로는 서버 데이터 사용)
    var currentStatus by remember { mutableStateOf(OrderStatus.PENDING) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Order Status", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // 단계별 프로그레스 바 (Stepper)
        DeliveryStepper(currentStatus = currentStatus)

        Spacer(modifier = Modifier.height(32.dp))

        Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Order #$orderId", style = MaterialTheme.typography.titleMedium)
                Text("Status: ${currentStatus.name}", color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Please wait while we prepare your food.")
            }
        }

        // (개발자용 테스트 버튼: 실제 앱에는 없음)
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { currentStatus = currentStatus.next() }) {
            Text("Simulate Next Step (Dev Only)")
        }
    }
}

@Composable
fun DeliveryStepper(currentStatus: OrderStatus) {
    val steps = listOf(
        Pair(OrderStatus.PENDING, Icons.Default.Receipt),
        Pair(OrderStatus.PREPARING, Icons.Default.Restaurant),
        Pair(OrderStatus.ON_DELIVERY, Icons.Default.TwoWheeler),
        Pair(OrderStatus.DELIVERED, Icons.Default.CheckCircle)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, (status, icon) ->
            StepItem(
                icon = icon,
                label = status.toUiString(),
                isCompleted = currentStatus.ordinal >= status.ordinal,
                isCurrent = currentStatus == status
            )
            // 라인 그리기 (마지막 아이템 제외)
            if (index < steps.size - 1) {
                Divider(
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                    color = if (currentStatus.ordinal > status.ordinal) MaterialTheme.colorScheme.primary else Color.Gray,
                    thickness = 2.dp
                )
            }
        }
    }
}

@Composable
fun StepItem(icon: ImageVector, label: String, isCompleted: Boolean, isCurrent: Boolean) {
    val color = if (isCompleted) MaterialTheme.colorScheme.primary else Color.Gray

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isCurrent) MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
}