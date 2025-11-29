package com.example.deliveryapp2.ui.customer.order


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.deliveryapp2.ui.customer.dummyOrders

@Composable
fun OrderListScreen(onOrderClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("My Orders", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(dummyOrders) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onOrderClick(order.id) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(order.storeName, style = MaterialTheme.typography.titleMedium)
                            Text(order.status, color = if(order.status == "Cooking") Color.Blue else Color.Gray)
                        }
                        Text(order.price)
                    }
                }
            }
        }
    }
}