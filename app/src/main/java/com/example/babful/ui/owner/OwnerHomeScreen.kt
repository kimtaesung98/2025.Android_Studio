package com.example.babful.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OwnerHomeScreen(
    viewModel: OwnerViewModel = hiltViewModel(),
    onNavigateToCustomerMode: () -> Unit,
    onNavigateToMenu: (Int) -> Unit,
    onNavigateToOrders: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 화면 진입 시 데이터 갱신
    LaunchedEffect(Unit) { viewModel.loadOwnerData() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)) // 연한 회색 배경
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 헤더
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("사장님 대시보드", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onNavigateToCustomerMode) {
                Text("고객 모드 >")
            }
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.myStore == null) {
            // ⭐️ CASE 1: 가게가 없을 때 (등록 폼)
            StoreRegisterForm(
                onSubmit = { name, desc -> viewModel.createStore(name, desc) }
            )
        } else {
            // ⭐️ CASE 2: 가게가 있을 때 (대시보드)
            val store = uiState.myStore!!

            // 1. 가게 상태 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(store.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(store.description, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))

                    // 매출 요약
                    Row(modifier = Modifier.fillMaxWidth()) {
                        StatItem(label = "오늘 매출", value = "${uiState.todaySales}원", modifier = Modifier.weight(1f))
                        StatItem(label = "대기 주문", value = "${uiState.pendingOrderCount}건", modifier = Modifier.weight(1f), isAlert = uiState.pendingOrderCount > 0)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. 관리 메뉴 그리드
            Text("바로가기", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardButton(
                    title = "주문 접수",
                    icon = Icons.Default.List,
                    color = Color(0xFFE3F2FD),
                    textColor = Color(0xFF1565C0),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToOrders
                )
                DashboardButton(
                    title = "메뉴 관리",
                    icon = Icons.Default.Add,
                    color = Color(0xFFE8F5E9),
                    textColor = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToMenu(store.id) }
                )
            }
        }
    }
}

// --- 하위 컴포넌트 ---

@Composable
fun StoreRegisterForm(onSubmit: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("내 가게 등록", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("가게 이름") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("가게 설명") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { onSubmit(name, desc) },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("장사 시작하기")
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, modifier: Modifier = Modifier, isAlert: Boolean = false) {
    Column(modifier = modifier) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (isAlert) Color.Red else Color.Black)
    }
}

@Composable
fun DashboardButton(
    title: String,
    icon: ImageVector,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(120.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = textColor, fontWeight = FontWeight.Bold)
        }
    }
}