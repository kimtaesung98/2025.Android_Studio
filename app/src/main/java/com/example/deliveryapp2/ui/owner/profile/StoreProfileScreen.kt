package com.example.deliveryapp2.ui.owner.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StoreProfileScreen() {
    var isStoreOpen by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Store Settings", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // Open/Close Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Store Open Status", style = MaterialTheme.typography.titleMedium)
            Switch(checked = isStoreOpen, onCheckedChange = { isStoreOpen = it })
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Info Fields
        OutlinedTextField(
            value = "Best Chicken Shop",
            onValueChange = {},
            label = { Text("Store Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = "10:00 AM - 10:00 PM",
            onValueChange = {},
            label = { Text("Operating Hours") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { /* Save Logic */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Save Changes")
        }
    }
}