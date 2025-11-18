package com.example.babful.ui.owner

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OwnerHomeScreen(
    viewModel: OwnerViewModel = hiltViewModel(),
    onNavigateToCustomerMode: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 입력 폼 상태
    var storeName by remember { mutableStateOf("") }
    var storeDesc by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "사장님 모드", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.myStore != null) {
            // ⭐️ [가게가 있을 때] 대시보드
            Text(text = "내 가게: ${uiState.myStore!!.name}", fontSize = 20.sp)
            Text(text = uiState.myStore!!.description)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { /* 메뉴 관리 이동 */ }) { Text("메뉴 관리") }
            Button(onClick = { /* 주문 접수 이동 */ }) { Text("주문 접수 (0)") }
        } else {
            // ⭐️ [가게가 없을 때] 등록 폼
            Text(text = "가게를 등록해주세요", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = storeName,
                onValueChange = { storeName = it },
                label = { Text("가게 이름") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = storeDesc,
                onValueChange = { storeDesc = it },
                label = { Text("가게 설명 (예: 최고의 떡볶이!)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.createStore(storeName, storeDesc) },
                enabled = storeName.isNotEmpty() && storeDesc.isNotEmpty()
            ) {
                Text("가게 등록하기")
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(onClick = onNavigateToCustomerMode) {
            Text("고객 모드로 돌아가기")
        }
    }
}