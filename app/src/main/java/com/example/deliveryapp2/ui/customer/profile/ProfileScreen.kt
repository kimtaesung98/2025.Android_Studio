package com.example.deliveryapp2.ui.customer.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

    // 현재 로그인한 역할 (CUSTOMER / OWNER)
    val userRole = userInfo["role"] ?: "GUEST"

    var showDialog by remember { mutableStateOf(false) }
    var newAddress by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. 공통 헤더
        item {
            ProfileHeader(
                name = userInfo["name"] ?: "User",
                email = "user@email.com",
                role = userRole
            )
        }

        // 2. 활동 요약 (역할별로 다르게 표시)
        item {
            if (userRole == "OWNER") {
                OwnerStatsRow()
            } else {
                CustomerStatsRow()
            }
        }

        // 3. 계정 설정 섹션
        item {
            SectionHeader("Account Settings")
            // 주소 변경 (공통)
            ProfileOptionItem(
                icon = Icons.Default.LocationOn,
                title = if (userRole == "OWNER") "Store Address" else "Delivery Address",
                subtitle = userInfo["address"] ?: "No address set",
                onClick = {
                    newAddress = userInfo["address"] ?: ""
                    showDialog = true
                }
            )

            // 점주 전용 메뉴
            if (userRole == "OWNER") {
                ProfileOptionItem(
                    icon = Icons.Outlined.Store,
                    title = "Store Info",
                    subtitle = "Manage opening hours, phone",
                    onClick = { /* 추후 구현 */ }
                )
                ProfileOptionItem(
                    icon = Icons.Outlined.AttachMoney,
                    title = "Settlement Account",
                    subtitle = "Woori Bank 1002...",
                    onClick = { /* 추후 구현 */ }
                )
            } else {
                // 고객 전용 메뉴
                ProfileOptionItem(
                    icon = Icons.Outlined.CreditCard,
                    title = "Payment Methods",
                    subtitle = "Visa ending in 1234",
                    onClick = { /* 추후 구현 */ }
                )
            }
        }

        // 4. 앱 설정 섹션 (공통)
        item {
            SectionHeader("App Settings")
            ProfileOptionItem(
                icon = Icons.Outlined.Notifications,
                title = "Notifications",
                trailing = { Switch(checked = true, onCheckedChange = {}) },
                onClick = {}
            )
            if (userRole == "CUSTOMER") {
                ProfileOptionItem(
                    icon = Icons.Outlined.FavoriteBorder,
                    title = "My Favorites",
                    onClick = { }
                )
            }
            ProfileOptionItem(
                icon = Icons.Outlined.HelpOutline,
                title = "Help & Support",
                onClick = { }
            )
        }

        // 5. 하단 로그아웃
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (userRole == "OWNER") "Store Address" else "Delivery Address") },
            text = {
                OutlinedTextField(
                    value = newAddress,
                    onValueChange = { newAddress = it },
                    label = { Text("New Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
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

@Composable
fun CustomerStatsRow() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .offset(y = (-20).dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(count = "5", label = "Coupons")
            Divider(modifier = Modifier.height(30.dp).width(1.dp))
            StatItem(count = "1200", label = "Points")
            Divider(modifier = Modifier.height(30.dp).width(1.dp))
            StatItem(count = "12", label = "Orders")
        }
    }
}

@Composable
fun OwnerStatsRow() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .offset(y = (-20).dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(count = "4.8", label = "Rating")
            Divider(modifier = Modifier.height(30.dp).width(1.dp))
            StatItem(count = "150", label = "Reviews")
            Divider(modifier = Modifier.height(30.dp).width(1.dp))
            StatItem(count = "25", label = "Likes")
        }
    }
}

// --- 컴포저블 분리 (재사용성 및 가독성 향상) ---

@Composable
fun ProfileHeader(name: String, email: String, role: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(top = 40.dp, bottom = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(role, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
    }
}

//@Composable
//fun ActivityStatsRow() {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//            .offset(y = (-20).dp), // 헤더 위로 살짝 겹치게
//        elevation = CardDefaults.cardElevation(4.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
//    ) {
//        Row(
//            modifier = Modifier.padding(16.dp),
//            horizontalArrangement = Arrangement.SpaceAround,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            StatItem(count = "5", label = "Coupons")
//            Divider(modifier = Modifier.height(30.dp).width(1.dp))
//            StatItem(count = "1200", label = "Points")
//            Divider(modifier = Modifier.height(30.dp).width(1.dp))
//            StatItem(count = "12", label = "Orders")
//        }
//    }
//}

@Composable
fun RowScope.StatItem(count: String, label: String) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(count, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                if (subtitle != null) {
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            if (trailing != null) {
                trailing()
            } else {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
            }
        }
    }
    Divider(modifier = Modifier.padding(start = 56.dp), color = Color.LightGray.copy(alpha = 0.2f))
}