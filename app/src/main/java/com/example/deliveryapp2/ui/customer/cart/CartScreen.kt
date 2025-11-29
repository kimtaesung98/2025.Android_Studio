// File: /ui/customer/cart/CartScreen.kt
package com.example.deliveryapp2.ui.customer.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deliveryapp2.data.network.OrderRequest
import com.example.deliveryapp2.data.network.RetrofitClient
import com.example.deliveryapp2.data.repository.CartRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(onCheckoutClick: () -> Unit) {
    val cartItems = CartRepository.items
    val scope = rememberCoroutineScope()
    var isOrdering by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Your Cart") }) },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Button(
                    onClick = onCheckoutClick, // 복잡한 API 호출 로직 제거하고 네비게이션만 실행
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp)
                ) {
                    Text("Checkout (${CartRepository.getTotalPrice()} won)")
                }
            }
        }
    ) { padding ->
        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Cart is empty!")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(cartItems) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.menu.name} x${item.quantity}")
                        Text("${item.menu.price * item.quantity} won")
                    }
                    Divider()
                }
            }
        }
    }
}