package com.example.deliveryapp2.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RoleSelectionScreen(onCustomerClick: () -> Unit, onOwnerClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select App Mode", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onCustomerClick,
            modifier = Modifier.fillMaxWidth(0.7f).height(56.dp)
        ) {
            Text("Enter as Customer")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onOwnerClick,
            modifier = Modifier.fillMaxWidth(0.7f).height(56.dp)
        ) {
            Text("Enter as Store Owner")
        }
    }
}