package com.example.deliveryapp2.ui.customer.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deliveryapp2.data.model.Store
import com.example.deliveryapp2.data.network.RetrofitClient
import com.example.deliveryapp2.data.repository.NetworkDeliveryRepository
import com.example.deliveryapp2.viewmodel.StoreListViewModel
import com.example.deliveryapp2.viewmodel.StoreListViewModelFactory

@OptIn(ExperimentalMaterial3Api::class) // SearchBar 사용을 위해 필요
@Composable
fun CustomerHomeScreen(
    onStoreClick: (String) -> Unit
) {
    val repository = NetworkDeliveryRepository(RetrofitClient.apiService)
    val viewModel: StoreListViewModel = viewModel(
        factory = StoreListViewModelFactory(repository)
    )

    val stores by viewModel.stores.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // 🟢 [추가된 부분] 상단 검색바
        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.onSearchQueryChanged(it) },
            onSearch = { /* 엔터 쳤을 때 동작 (여기선 실시간이라 불필요) */ },
            active = false, // 항상 펼쳐진 상태 아님
            onActiveChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search hungry?") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        ) {}

        // 기존 매장 리스트
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(stores) { store ->
                StoreCard(store = store, onClick = { onStoreClick(store.id) })
            }

            // 검색 결과가 없을 때 안내 메시지
            if (stores.isEmpty()) {
                item {
                    Text(
                        text = "No stores found.",
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

// 기존 StoreCard 컴포넌트 유지 (기존에 작성하신 코드가 있다면 그대로 두거나 아래 사용)
@Composable
fun StoreCard(store: Store, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = store.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Min Order: ${store.minOrderPrice} won", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            Text(text = "Delivery: ${store.deliveryTime}", style = MaterialTheme.typography.bodySmall)
        }
    }
}