package com.example.appname.shorts.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appname.shorts.data.repository.ShortsRepositoryImpl
import com.example.appname.shorts.domain.usecase.GetShortsUseCase
import com.example.appname.shorts.domain.usecase.LikeShortsUseCase
import com.example.appname.shorts.ui.viewmodel.ShortsViewModel
import com.example.appname.ui.screen.shorts.components.VideoPlayerItem
import androidx.hilt.navigation.compose.hiltViewModel // (1) 🚨 hiltViewModel import
import androidx.lifecycle.viewmodel.compose.viewModel
@Composable
fun ShortsScreen(
    // (3) 🚨 Hilt가 ViewModel을 자동으로 주입하도록 변경
    shortsViewModel: ShortsViewModel = hiltViewModel()
){
    val uiState by shortsViewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { uiState.items.size })

    VerticalPager(
        state = pagerState,
        modifier = Modifier.Companion.fillMaxSize(),
        pageSize = PageSize.Fill
    ) { pageIndex ->
        if (uiState.items.isNotEmpty()) {
            val isSelected = (pagerState.currentPage == pageIndex)
            val currentItem = uiState.items[pageIndex]

            VideoPlayerItem(
                shortsItem = currentItem,
                isSelected = isSelected,
                // 🚨 (5) ViewModel의 '좋아요' 이벤트 핸들러 호출
                onLikeClicked = { shortsViewModel.onLikeClicked(currentItem.id) }
            )
        }
    }
}