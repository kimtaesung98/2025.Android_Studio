package com.example.deliveryapp2.ui.customer.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OrderDetailScreen(orderId: String?) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Order #$orderId", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Burger King - Whopper Set", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(24.dp))

        Text("Delivery Status: Cooking")
        LinearProgressIndicator(progress = 0.4f, modifier = Modifier.fillMaxWidth().height(8.dp))

        Spacer(modifier = Modifier.height(24.dp))

        // Map Placeholder [cite: 7]
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text("Map / Delivery Tracking UI", color = Color.DarkGray)
        }
    }
}