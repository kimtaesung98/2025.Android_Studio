package com.example.appname.shorts.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appname.ui.screen.shorts.components.VideoPlayerItem
import com.example.appname.shorts.ui.ShortsViewModel

@Composable
fun ShortsScreen(shortsViewModel: ShortsViewModel = viewModel()) {
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

            // (2) 🚨 이제 VideoPlayerItem은 외부에서 가져온 Composable
            VideoPlayerItem(
                shortsItem = currentItem,
                isSelected = isSelected,
                onLikeClicked = { shortsViewModel.onLikeClicked(currentItem.id) }
            )
        }
    }
}