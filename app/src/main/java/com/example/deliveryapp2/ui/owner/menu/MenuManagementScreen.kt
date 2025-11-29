package com.example.deliveryapp2.ui.owner.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuManagementScreen() {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Add Menu Logic */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Menu")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("My Menu List", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            // Mock Menu List
            val menus = listOf("Fried Chicken - 18,000", "Seasoned Chicken - 19,000", "French Fries - 5,000")

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(menus.size) { index ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(menus[index])
                            IconButton(onClick = { /* Edit Logic */ }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                        }
                    }
                }
            }
        }
    }
}