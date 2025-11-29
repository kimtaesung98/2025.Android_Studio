package com.example.deliveryapp2.ui.customer.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // User Info
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(80.dp).background(Color.Gray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Justin Mason", style = MaterialTheme.typography.titleLarge) // Example Name
                Text("010-1234-5678", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Menu Buttons
        val menuItems = listOf("Manage Address", "Payment Methods", "Favorites", "App Settings", "Log Out")
        menuItems.forEach { item ->
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = Color.Black)
            ) {
                Text(item)
            }
        }
    }
}