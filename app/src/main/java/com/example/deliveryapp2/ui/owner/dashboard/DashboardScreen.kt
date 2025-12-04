package com.example.deliveryapp2.ui.owner.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.sp
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

    LaunchedEffect(Unit) {
        viewModel.loadStats()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Store Dashboard", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Overview of today's performance", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        if (stats == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val s = stats!!

            // 메인 매출 카드
            Card(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total Sales", color = Color.White.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = NumberFormat.getNumberInstance(Locale.KOREA).format(s.totalSales) + " won",
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 그리드 통계
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { StatCard("Pending", s.pendingOrders.toString(), Icons.Default.NotificationsActive, Color(0xFFFF9800)) }
                item { StatCard("Processing", s.processingOrders.toString(), Icons.Default.Restaurant, Color(0xFF2196F3)) }
                item { StatCard("Total Orders", s.totalOrders.toString(), Icons.Default.Receipt, Color(0xFF9C27B0)) }
                item { StatCard("Menu Count", "Manage >", Icons.Default.MenuBook, Color.Gray) }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
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