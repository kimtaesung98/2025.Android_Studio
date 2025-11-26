package com.example.babful.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.babful.data.model.ActiveOrder

@Composable
fun FloatingDeliveryBar(
    order: ActiveOrder,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(60.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("배달 진행 중 🛵", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(order.storeName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            // 상태 뱃지
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = order.status,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}