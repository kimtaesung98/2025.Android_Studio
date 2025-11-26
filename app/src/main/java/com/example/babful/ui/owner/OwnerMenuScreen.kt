package com.example.babful.ui.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OwnerMenuScreen(
    viewModel: OwnerViewModel = hiltViewModel(),
    storeId: Int // NavHost에서 전달받음
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ⭐️ 화면 진입 시 메뉴 로드
    LaunchedEffect(storeId) {
        viewModel.loadMenus(storeId)
        // (참고: ViewModel 인스턴스가 공유되지 않는 경우 myStore 정보를 다시 불러오거나 해야 함.
        // 여기서는 간단히 storeId만 사용하여 메뉴를 조회합니다.)
    }

    var menuName by remember { mutableStateOf("") }
    var menuPrice by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "메뉴 관리", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // --- 메뉴 등록 폼 ---
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = menuName,
                    onValueChange = { menuName = it },
                    label = { Text("메뉴 이름 (예: 김치찌개)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = menuPrice,
                    onValueChange = { menuPrice = it },
                    label = { Text("가격 (원)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.createMenu(menuName, menuPrice)
                        menuName = ""; menuPrice = "" // 입력창 초기화
                    },
                    enabled = menuName.isNotBlank() && menuPrice.isNotBlank(),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("메뉴 추가")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "등록된 메뉴 목록", style = MaterialTheme.typography.titleMedium)

        // --- 메뉴 리스트 ---
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.menus) { menu ->
                Card {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = menu.name)
                        Text(text = "${menu.price}원", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                }
            }
        }
    }
}