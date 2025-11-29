package com.example.deliveryapp2.ui.owner.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnalyticsScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Performance", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Weekly Revenue Trend")
        Spacer(modifier = Modifier.height(8.dp))

        // Graph Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text("Graph UI (Coming Soon)", color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Top Selling Items", style = MaterialTheme.typography.titleMedium)
        Text("1. Fried Chicken (54 orders)", style = MaterialTheme.typography.bodyMedium)
        Text("2. Coke (30 orders)", style = MaterialTheme.typography.bodyMedium)
    }
}