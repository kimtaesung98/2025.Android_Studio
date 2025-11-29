package com.example.deliveryapp2.ui.owner.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class OwnerOrder(val id: String, val menus: String, val price: String, val status: String)

@Composable
fun OrderManagementScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Pending", "Processing", "Done")

    // Mock Data
    val orders = listOf(
        OwnerOrder("#101", "Fried Chicken x1", "18,000", "Pending"),
        OwnerOrder("#102", "Cheese Pizza x1", "22,000", "Pending"),
        OwnerOrder("#099", "Burger Set x2", "15,000", "Processing"),
        OwnerOrder("#098", "Coke x2", "4,000", "Done")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        val filteredOrders = when(selectedTab) {
            0 -> orders.filter { it.status == "Pending" }
            1 -> orders.filter { it.status == "Processing" }
            else -> orders.filter { it.status == "Done" }
        }

        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filteredOrders) { order ->
                OrderActionCard(order)
            }
        }
    }
}

@Composable
fun OrderActionCard(order: OwnerOrder) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(order.id, style = MaterialTheme.typography.titleMedium)
                Text(order.price, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            Text(order.menus, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(12.dp))

            if (order.status == "Pending") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { /* Accept Logic */ }, modifier = Modifier.weight(1f)) {
                        Text("Accept")
                    }
                    OutlinedButton(onClick = { /* Reject Logic */ }, modifier = Modifier.weight(1f)) {
                        Text("Reject")
                    }
                }
            } else if (order.status == "Processing") {
                Button(onClick = { /* Complete Logic */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Mark as Completed")
                }
            }
        }
    }
}