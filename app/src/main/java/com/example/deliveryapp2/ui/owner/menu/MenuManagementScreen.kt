package com.example.deliveryapp2.ui.owner.menu

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deliveryapp2.data.network.RetrofitClient
import com.example.deliveryapp2.data.repository.NetworkDeliveryRepository
import com.example.deliveryapp2.viewmodel.MenuViewModel
import com.example.deliveryapp2.viewmodel.MenuViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagementScreen() {
    val context = LocalContext.current
    val repository = NetworkDeliveryRepository(RetrofitClient.apiService) // 리팩토링된 생성자 사용
    val viewModel: MenuViewModel = viewModel(
        factory = MenuViewModelFactory(repository)
    )

    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    // 입력 상태 변수들
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    // 성공 시 초기화 처리
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            Toast.makeText(context, "Menu Added Successfully!", Toast.LENGTH_SHORT).show()
            name = ""
            price = ""
            description = ""
            imageUrl = ""
            viewModel.resetSuccess()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Add New Menu", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // 메뉴 이름
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Menu Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 가격 (숫자 키보드)
        OutlinedTextField(
            value = price,
            onValueChange = { if (it.all { char -> char.isDigit() }) price = it },
            label = { Text("Price (won)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 설명
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 이미지 URL
        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("Image URL (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("http://...") }
        )

        Spacer(modifier = Modifier.weight(1f))

        // 등록 버튼
        Button(
            onClick = { viewModel.addMenu(name, price, description, imageUrl) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isLoading && name.isNotBlank() && price.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Add Menu")
            }
        }
    }
}