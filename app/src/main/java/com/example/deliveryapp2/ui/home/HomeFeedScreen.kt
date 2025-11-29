package com.example.deliveryapp2.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.deliveryapp2.ui.components.CategoryChip
import com.example.deliveryapp2.ui.components.StoreCard
import com.example.deliveryapp2.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeFeedScreen(
    viewModel: HomeViewModel,
    onStoreClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Delivery App") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Search Bar Placeholder
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search stores...") },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            // Category Chips
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                items(uiState.categories) { category ->
                    CategoryChip(category = category)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Store List
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally))
            } else {
                LazyColumn {
                    items(uiState.storeList) { store ->
                        StoreCard(store = store, onClick = onStoreClick)
                    }
                }
            }
        }
    }
}