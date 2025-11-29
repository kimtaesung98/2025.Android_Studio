package com.example.deliveryapp2.ui.owner.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class DashboardAction(val title: String, val icon: ImageVector, val route: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onNavigate: (String) -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Store Owner Dashboard") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            // 1. Summary Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth().height(120.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Today's Revenue", style = MaterialTheme.typography.titleMedium)
                    Text("₩ 345,000", style = MaterialTheme.typography.displayMedium)
                    Text("Active Orders: 5", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Action Grid
            Text("Quick Actions", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            val actions = listOf(
                DashboardAction("Orders", Icons.Default.ListAlt, "owner_orders"),
                DashboardAction("Menu", Icons.Default.Fastfood, "owner_menu"),
                DashboardAction("Store Info", Icons.Default.Store, "owner_profile"),
                DashboardAction("Analytics", Icons.Default.Timeline, "owner_analytics")
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(actions) { action ->
                    DashboardButton(action, onNavigate)
                }
            }
        }
    }
}

@Composable
fun DashboardButton(action: DashboardAction, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier.height(100.dp).clickable { onClick(action.route) },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(action.icon, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(action.title)
        }
    }
}