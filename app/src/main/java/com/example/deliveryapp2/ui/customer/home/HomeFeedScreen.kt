package com.example.deliveryapp2.ui.customer.home


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.deliveryapp2.ui.customer.dummyStores

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeFeedScreen(onStoreClick: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Baedal App") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // 1. Search Bar
            OutlinedTextField(
                value = "",
                onValueChange = {},
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                placeholder = { Text("Search for food...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // 2. Categories
            val categories = listOf("All", "Chicken", "Pizza", "Burger", "Asian", "Dessert")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = false,
                        onClick = {},
                        label = { Text(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Store List
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Text("Popular Stores", style = MaterialTheme.typography.titleLarge) }
                items(dummyStores) { store ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onStoreClick(store.id) },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(store.name, style = MaterialTheme.typography.titleMedium)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(16.dp))
                                Text(" ${store.rating} • ${store.time}")
                            }
                        }
                    }
                }
            }
        }
    }
}