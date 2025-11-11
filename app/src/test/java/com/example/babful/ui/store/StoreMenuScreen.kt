package com.example.babful.ui.store

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 가게 메뉴 상세 화면 (Placeholder)
 * @param storeId 네비게이션을 통해 전달받은 가게 ID
 */
@Composable
fun StoreMenuScreen(storeId: String?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "가게 메뉴 화면",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "전달받은 가게 ID:",
                fontSize = 16.sp
            )
            Text(
                text = storeId ?: "ID 없음", // null일 경우 "ID 없음" 표시
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}