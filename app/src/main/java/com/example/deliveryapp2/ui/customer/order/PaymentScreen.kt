package com.example.deliveryapp2.ui.customer.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.deliveryapp2.data.network.OrderRequest
import com.example.deliveryapp2.data.network.RetrofitClient
import com.example.deliveryapp2.data.repository.CartRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    totalPrice: Int,
    onPaymentSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }

    // 결제 수단
    val paymentMethods = listOf("Credit Card", "Samsung Pay", "Cash on Delivery")
    var selectedMethod by remember { mutableStateOf(paymentMethods[0]) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Payment") }) }
    ) { padding ->
        if (isProcessing) {
            // 로딩 화면
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Processing Payment...")
                }
            }
        } else {
            // 결제 선택 화면
            Column(
                modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Amount", style = MaterialTheme.typography.titleMedium)
                    Text("$totalPrice won", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)

                    Spacer(modifier = Modifier.height(32.dp))

                    Text("Payment Method", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    paymentMethods.forEach { method ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (method == selectedMethod),
                                    onClick = { selectedMethod = method }
                                )
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (method == selectedMethod),
                                onClick = { selectedMethod = method }
                            )
                            Text(text = method, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }

                Button(
                    onClick = {
                        scope.launch {
                            isProcessing = true
                            delay(2000) // 2초간 결제하는 척 대기 (UX)

                            try {
                                // 서버로 주문 전송
                                val request = OrderRequest(
                                    storeId = "1", // 실제 앱에선 동적으로 처리
                                    items = CartRepository.items.map { "${it.menu.name} x${it.quantity}" },
                                    totalPrice = totalPrice
                                )
                                RetrofitClient.apiService.placeOrder(request)
                                CartRepository.clearCart()
                                onPaymentSuccess()
                            } catch (e: Exception) {
                                isProcessing = false
                                // 에러 처리 로직 (생략)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Pay $totalPrice won")
                }
            }
        }
    }
}