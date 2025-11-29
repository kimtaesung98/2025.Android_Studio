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
fun CartScreen(onOrderComplete: () -> Unit) {
    val cartItems = CartRepository.items
    val scope = rememberCoroutineScope()
    var isOrdering by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Your Cart") }) },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Button(
                    onClick = {
                        scope.launch {
                            isOrdering = true
                            try {
                                // 실제 서버 주문 요청
                                val request = OrderRequest(
                                    storeId = "1", // 실제 앱에선 선택한 매장 ID 사용
                                    items = cartItems.map { "${it.menu.name} x${it.quantity}" },
                                    totalPrice = CartRepository.getTotalPrice()
                                )
                                RetrofitClient.apiService.placeOrder(request)
                                CartRepository.clearCart() // 장바구니 비우기
                                onOrderComplete()
                            } catch (e: Exception) {
                                // Error handling
                            } finally {
                                isOrdering = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                    enabled = !isOrdering
                ) {
                    if (isOrdering) CircularProgressIndicator(color = Color.White)
                    else Text("Order Now (${CartRepository.getTotalPrice()} won)")
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