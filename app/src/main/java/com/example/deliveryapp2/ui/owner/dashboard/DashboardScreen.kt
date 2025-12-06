package com.example.deliveryapp2.ui.owner.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deliveryapp2.data.network.RetrofitClient
import com.example.deliveryapp2.data.repository.NetworkDeliveryRepository
import com.example.deliveryapp2.viewmodel.OwnerDashboardViewModel
import com.example.deliveryapp2.viewmodel.OwnerDashboardViewModelFactory
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(onNavigate: (String) -> Unit) {
    val repository = NetworkDeliveryRepository(RetrofitClient.apiService)
    val viewModel: OwnerDashboardViewModel = viewModel(
        factory = OwnerDashboardViewModelFactory(repository)
    )

    val stats by viewModel.stats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 화면 진입 시 데이터 로드
    LaunchedEffect(Unit) {
        viewModel.loadStats()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 상단 헤더 (제목 + 새로고침 버튼)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Store Dashboard", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Real-time Overview", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            IconButton(onClick = { viewModel.loadStats() }) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (stats != null) {
            val s = stats!!

            // 1. 총 매출 카드 (Main)
            Card(
                modifier = Modifier.fillMaxWidth().height(140.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total Sales (Today)", color = Color.White.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(8.dp))
                    // 콤마 포맷팅 (예: 15,000 won)
                    val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(s.totalSales)
                    Text(
                        text = "$formattedPrice won",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. 상세 통계 그리드
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pending: 대기 중인 주문 (가장 중요)
                item {
                    StatCard("Pending Orders", s.pendingOrders.toString(), Icons.Default.NotificationsActive, Color(0xFFFF9800)) {
                        onNavigate("owner_orders") // 클릭 시 주문 관리 탭으로 이동
                    }
                }
                // Processing: 조리/배달 중
                item { StatCard("Processing", s.processingOrders.toString(), Icons.Default.Restaurant, Color(0xFF2196F3)) {} }
                // Total: 전체 주문 수
                item { StatCard("Total Orders", s.totalOrders.toString(), Icons.Default.Receipt, Color(0xFF9C27B0)) {} }
                // Menu: 메뉴 관리 바로가기
                item { StatCard("Menu Management", "Edit >", Icons.Default.MenuBook, Color.Gray) { onNavigate("owner_menu") } }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.height(110.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.size(36.dp).background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }

            Column {
                Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(title, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}