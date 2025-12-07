package com.example.deliveryapp2.ui.customer.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deliveryapp2.viewmodel.ProfileViewModel
import com.example.deliveryapp2.viewmodel.ProfileViewModelFactory

@Composable
fun ProfileScreen(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(context)
    )
    val userInfo by viewModel.userInfo.collectAsState()

    // 주소 수정 다이얼로그 상태
    var showDialog by remember { mutableStateOf(false) }
    var newAddress by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // 프로필 아이콘
        Box(
            modifier = Modifier.size(100.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(50.dp), tint = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 이름 & 역할
        Text(userInfo["name"] ?: "", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(userInfo["role"] ?: "", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        // 정보 카드
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Delivery Address", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(userInfo["address"] ?: "No address set", style = MaterialTheme.typography.bodyLarge)
                    IconButton(onClick = {
                        newAddress = userInfo["address"] ?: ""
                        showDialog = true
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 로그아웃 버튼
        Button(
            onClick = {
                viewModel.logout()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f))
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Out")
        }
    }

    // 주소 수정 다이얼로그
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Update Address") },
            text = {
                OutlinedTextField(
                    value = newAddress,
                    onValueChange = { newAddress = it },
                    label = { Text("New Address") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateAddress(newAddress)
                    showDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}