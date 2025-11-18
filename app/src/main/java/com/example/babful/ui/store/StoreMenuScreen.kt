package com.example.babful.ui.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.babful.data.model.Menu
import com.example.babful.data.model.StoreInfo
import com.example.babful.data.model.User

@Composable
fun StoreMenuScreen(
    storeId: String?,
    viewModel: StoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading && uiState.storeInfo == null) { // (최초 로딩)
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.storeInfo != null && uiState.user != null) {
            StoreMenuContent(
                storeInfo = uiState.storeInfo!!,
                user = uiState.user!!,
                menus = uiState.menus,         // ⭐️ [신규]
                cartItems = uiState.cartItems, // ⭐️ [신규]
                totalPrice = uiState.totalPrice, // ⭐️ [신규]
                isLoading = uiState.isLoading,
                onSubscribeToggle = { viewModel.toggleSubscription() },
                onAddToCart = { menu -> viewModel.addToCart(menu) }, // ⭐️ [신규]
                onCompleteOrder = { viewModel.completeOrder() }      // ⭐️ [수정]
            )
        } else {
            // ⭐️ 3. 에러 발생 시 UI
            Text(
                text = "가게 정보를 불러오는 데 실패했습니다. (ID: $storeId)",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun StoreMenuContent(
    storeInfo: StoreInfo,
    user: User,
    menus: List<Menu>,          // ⭐️
    cartItems: List<Menu>,      // ⭐️
    totalPrice: Int,            // ⭐️
    isLoading: Boolean,
    onSubscribeToggle: () -> Unit,
    onAddToCart: (Menu) -> Unit, // ⭐️
    onCompleteOrder: () -> Unit  // ⭐️
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 1. 상단 정보 (배너, 이름, 구독) - 기존 유지
        Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(Color.LightGray))
        Text(text = storeInfo.storeName, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
        Button(onClick = onSubscribeToggle, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(if (storeInfo.isSubscribed) "구독중" else "구독하기")
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // ⭐️ 2. 메뉴 리스트 (중간 영역)
        LazyColumn(modifier = Modifier.weight(1f)) {
            item { Text("메뉴", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp)) }

            if (menus.isEmpty()) {
                item { Text("등록된 메뉴가 없습니다.", modifier = Modifier.padding(16.dp)) }
            }

            items(menus) { menu ->
                // 메뉴 아이템 카드
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    onClick = { onAddToCart(menu) } // ⭐️ 클릭 시 장바구니 담기
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(menu.name, fontSize = 16.sp)
                        Text("${menu.price}원", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ⭐️ 3. 하단 결제 바 (Bottom Bar)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("장바구니 (${cartItems.size}개)")
                    Text("$totalPrice 원", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onCompleteOrder,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = totalPrice > 0 && !isLoading // ⭐️ 0원 이상일 때만 결제 가능
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    else Text("$totalPrice 원 결제하기")
                }
            }
        }
    }
}