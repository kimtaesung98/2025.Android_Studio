// File: /ui/customer/store/StoreDetailScreen.kt
package com.example.deliveryapp2.ui.customer.store

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.deliveryapp2.data.model.MenuItem
import com.example.deliveryapp2.data.repository.CartRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDetailScreen(storeId: String?, onGoToCart: () -> Unit) {
    // Mock Menus for the store (서버 연동 시 API로 대체 가능)
    val menuList = listOf(
        MenuItem("m1", "Signature Burger", 8900, "Best menu"),
        MenuItem("m2", "Cheese Fries", 4500, "Crispy and cheesy"),
        MenuItem("m3", "Large Coke", 2000, "Refreshing")
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Menu Selection") }) },
        floatingActionButton = {
            if (CartRepository.items.isNotEmpty()) {
                FloatingActionButton(onClick = onGoToCart) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${CartRepository.getTotalPrice()} won")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            item {
                Text("Store ID: $storeId", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(menuList) { menu ->
                MenuRowItem(menu = menu)
            }
        }
    }
}

@Composable
fun MenuRowItem(menu: MenuItem) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(menu.name, style = MaterialTheme.typography.titleMedium)
                Text("${menu.price} won", style = MaterialTheme.typography.bodyMedium)
            }
            Button(onClick = { CartRepository.addMenu(menu) }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Text(" Add")
            }
        }
    }
}