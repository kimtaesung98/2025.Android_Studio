package com.example.deliveryapp2.ui.customer.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.deliveryapp2.data.local.TokenManager

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    // 저장된 정보 불러오기
    val userRole = tokenManager.getUserRole() ?: "Guest"
    val userAddress = tokenManager.getUserAddress()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // 1. 상단 프로필 카드
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 프로필 아이콘
                Box(
                    modifier = Modifier.size(80.dp).background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if(userRole == "OWNER") Icons.Default.Store else Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = userRole, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = userAddress, style = MaterialTheme.typography.bodyMedium)
            }
        }

        // 2. 메뉴 리스트 (때려 박는 곳)
        LazyColumn(modifier = Modifier.weight(1f).padding(16.dp)) {
            item { SectionTitle("Account") }
            item { MenuItem(Icons.Default.Edit, "Edit Profile") {} }
            item { MenuItem(Icons.Default.LocationOn, "Manage Address") {} }
            item { MenuItem(Icons.Default.CreditCard, "Payment Methods") {} }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item { SectionTitle("App Settings") }
            item { MenuItem(Icons.Default.Notifications, "Notifications") {} }
            item { MenuItem(Icons.Default.Language, "Language") {} }
            item { MenuItem(Icons.Default.Info, "Version 1.0.0") {} }
        }

        // 3. 로그아웃 버튼
        Button(
            onClick = {
                tokenManager.clear() // 내부 저장소 초기화
                onLogout()           // 네비게이션 이동 콜백
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Out")
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun MenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.Gray)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}