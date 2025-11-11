package com.example.babful.ui.shorts

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator // ⭐️ [신규]
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel // ⭐️ [신규]
import androidx.lifecycle.compose.collectAsStateWithLifecycle
// ⭐️ [제거] import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.babful.data.model.ShortsItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShortsScreen(
    // ⭐️ [수정] viewModel() -> hiltViewModel()
    viewModel: ShortsViewModel = hiltViewModel(),
    onNavigateToStore: (storeId: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = uiState.isLoading // ⭐️ 로딩 상태

    val pagerState = rememberPagerState(pageCount = { uiState.shortsItems.size })

    LaunchedEffect(pagerState.currentPage, uiState.shortsItems) {
        if (uiState.shortsItems.isNotEmpty() && pagerState.currentPage < uiState.shortsItems.size) {
            Log.d("ShortsScreen", "현재 페이지: ${pagerState.currentPage} (가게: ${uiState.shortsItems[pagerState.currentPage].storeName})")
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "오늘의 쇼츠 (Go API)", // ⭐️ [수정] 타이틀 변경
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // ⭐️ [신규] 로딩 상태에 따라 스피너 또는 Pager 표시
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            VerticalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { pageIndex ->
                if (pageIndex < uiState.shortsItems.size) {
                    val item = uiState.shortsItems[pageIndex]
                    ShortsItemView(
                        item = item,
                        pageIndex = pageIndex,
                        onStoreClick = {
                            Log.d("ShortsScreen", "${item.storeName} 클릭됨, ID: ${item.storeId} 전달")
                            onNavigateToStore(item.storeId)
                        }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Gray))
                }
            }
        }
    }
}

// [수정] ShortsItemView: 클릭 이벤트를 받도록 onStoreClick 추가
@Composable
fun ShortsItemView(
    item: ShortsItem,
    pageIndex: Int,
    onStoreClick: () -> Unit // ⭐️ [신규] 클릭 이벤트 람다
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(if (pageIndex % 2 == 0) Color.DarkGray else Color.Black)
            .clickable(onClick = onStoreClick), // ⭐️ [수정] 클릭 가능하도록 설정
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "쇼츠 영상 #${pageIndex + 1}",
                fontSize = 22.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "가게명: ${item.storeName}",
                fontSize = 16.sp,
                color = Color.Yellow
            )
            Text(
                text = "(여기를 클릭하여 가게로 이동)", // ⭐️ 안내 문구
                fontSize = 14.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(top = 20.dp)
            )
        }
    }
}