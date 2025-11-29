package com.example.deliveryapp2.ui.customer.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.deliveryapp2.data.model.MenuItem
import com.example.deliveryapp2.data.network.RetrofitClient
import com.example.deliveryapp2.data.repository.CartRepository
import androidx.compose.ui.draw.clip // 추가
import androidx.compose.foundation.shape.RoundedCornerShape // 추가
import androidx.compose.ui.layout.ContentScale // 추가
import coil.compose.AsyncImage // 추가

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDetailScreen(storeId: String?, onGoToCart: () -> Unit) {
    // 서버에서 받아올 메뉴 리스트 상태
    var menuList by remember { mutableStateOf<List<MenuItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // 화면 진입 시 메뉴 로드
    LaunchedEffect(storeId) {
        if (storeId != null) {
            try {
                menuList = RetrofitClient.apiService.getStoreMenus(storeId)
            } catch (e: Exception) {
                // 에러 처리 (로그 등)
            } finally {
                isLoading = false
            }
        }
    }

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
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                item {
                    Text("Store ID: $storeId", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                if (menuList.isEmpty()) {
                    item { Text("No menus available for this store.") }
                }
                items(menuList) { menu ->
                    MenuRowItem(menu = menu)
                }
            }
        }
    }
}

@Composable
fun MenuRowItem(menu: MenuItem) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // [수정] null 안전 처리 (!menu.imageUrl.isNullOrEmpty())
            if (!menu.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = menu.imageUrl,
                    contentDescription = menu.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
            } else {
                // (선택 사항) 이미지가 없을 때 보여줄 회색 박스
                // 필요 없다면 이 else 블록은 없어도 됩니다.
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image", style = MaterialTheme.typography.labelSmall)
                }
                Spacer(modifier = Modifier.width(16.dp))
            }

            // 메뉴 정보 (가운데)
            Column(modifier = Modifier.weight(1f)) {
                Text(menu.name, style = MaterialTheme.typography.titleMedium)
                Text("${menu.price} won", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                Text(
                    menu.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 2
                )
            }

            // 추가 버튼 (오른쪽)
            IconButton(onClick = { CartRepository.addMenu(menu) }) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}