package com.example.deliveryapp2.ui.customer.cart // 패키지명 주의

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.deliveryapp2.data.local.TokenManager
import com.example.deliveryapp2.data.network.OrderRequest
import com.example.deliveryapp2.data.network.RetrofitClient
import com.example.deliveryapp2.data.repository.CartRepository
import kotlinx.coroutines.launch

@Composable
fun PaymentScreen(
    onPaymentSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokenManager = remember { TokenManager(context) }

    // 데이터 로드
    val totalPrice = CartRepository.getTotalPrice()
    val address = tokenManager.getUserAddress()

    // 로딩 상태
    var isProcessing by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Payment", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // 1. 배달 주소 확인 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delivery Address", style = MaterialTheme.typography.titleSmall)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if(address.isNotEmpty()) address else "No address found. Please update profile.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. 결제 금액 정보
        Text("Order Summary", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total Amount")
            Text("$totalPrice won", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.weight(1f))

        // 3. 결제 버튼 (주문 전송)
        Button(
            onClick = {
                if (address.isEmpty()) {
                    Toast.makeText(context, "Please set an address first!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isProcessing = true
                scope.launch {
                    try {
                        // 서버로 보낼 주문 데이터 생성
                        val request = OrderRequest(
                            storeId = "1", // TODO: 실제로는 CartItem에 담긴 storeId를 써야 함 (현재는 단일 매장 가정)
                            items = CartRepository.items.map { "${it.menu.name} x${it.quantity}" },
                            totalPrice = totalPrice,
                            deliveryAddress = address
                        )

                        // API 호출
                        val response = RetrofitClient.apiService.placeOrder(request)

                        if (response.success) {
                            Toast.makeText(context, "Order Placed Successfully!", Toast.LENGTH_LONG).show()
                            CartRepository.clearCart() // 장바구니 비우기
                            onPaymentSuccess() // 주문 목록 화면으로 이동
                        } else {
                            Toast.makeText(context, "Failed: ${response.error}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    } finally {
                        isProcessing = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isProcessing && totalPrice > 0
        ) {
            if (isProcessing) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Pay & Order")
            }
        }
    }
}