package com.example.deliveryapp2.ui.owner.menu

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.deliveryapp2.data.model.MenuItem
import com.example.deliveryapp2.data.network.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagementScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 상태 관리
    var menuList by remember { mutableStateOf<List<MenuItem>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }

    // 점주가 관리하는 자신의 매장 ID (데모용으로 "1"번 매장 고정)
    val myStoreId = "1"

    // 메뉴 불러오기 함수
    fun loadMenus() {
        scope.launch {
            try {
                menuList = RetrofitClient.apiService.getStoreMenus(myStoreId)
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load menus", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 초기 로딩
    LaunchedEffect(Unit) { loadMenus() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Menu Management") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Menu")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Current Menu Items: ${menuList.size}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(menuList) { menu ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(menu.name, style = MaterialTheme.typography.titleMedium)
                                Text("${menu.price} won", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }

    // 메뉴 추가 다이얼로그
    if (showDialog) {
        AddMenuDialog(
            onDismiss = { showDialog = false },
            onAdd = { name, price, desc ->
                scope.launch {
                    try {
                        val newItem = MenuItem(
                            id = "", // 서버 생성
                            storeId = myStoreId,
                            name = name,
                            price = price.toIntOrNull() ?: 0,
                            description = desc
                        )
                        RetrofitClient.apiService.addMenu(newItem)
                        loadMenus() // 목록 갱신
                        showDialog = false
                        Toast.makeText(context, "Menu Added!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error adding menu", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}

@Composable
fun AddMenuDialog(onDismiss: () -> Unit, onAdd: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Menu") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Menu Name") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") })
            }
        },
        confirmButton = {
            Button(onClick = { onAdd(name, price, desc) }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}