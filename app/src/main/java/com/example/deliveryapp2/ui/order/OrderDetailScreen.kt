// File: /ui/order/OrderDetailScreen.kt
package com.example.deliveryapp2.ui.order

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
        Text("Order Detail: $orderId", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Indicator
        Text("Order Progress")
        LinearProgressIndicator(progress = 0.5f, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))

        // Map Placeholder [cite: 7]
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text("Map / Delivery Tracking", color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Menu List will be here...")
    }
}