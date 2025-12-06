package com.example.deliveryapp2.ui.customer.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.deliveryapp2.data.repository.CartItem
import com.example.deliveryapp2.data.repository.CartRepository

@Composable
fun CartScreen(
    onNavigateToPayment: () -> Unit
) {
    // Repository의 상태를 관찰 (실시간 업데이트)
    val cartItems = CartRepository.items
    val totalPrice = CartRepository.getTotalPrice()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Your Cart", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Cart is empty.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            }
        } else {
            // 장바구니 목록
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartItems) { item ->
                    CartItemRow(item)
                }
            }

            // 하단 결제 정보
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("$totalPrice won", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNavigateToPayment,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Checkout")
            }

            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = { CartRepository.clearCart() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear Cart", color = Color.Red)
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.menu.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text("${item.menu.price} won x ${item.quantity}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Text("${item.menu.price * item.quantity} w", style = MaterialTheme.typography.titleMedium)
        }
    }
}